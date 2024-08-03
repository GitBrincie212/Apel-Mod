package net.mcbrincie.apel.lib.objects;

import com.google.common.reflect.TypeToken;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import org.joml.Vector3f;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * DrawContext is an extensible mechanism for providing information to the
 * {@code beforeDraw} and {@code afterDraw} interceptors defined on
 * {@link net.mcbrincie.apel.lib.objects.ParticleObject} subclasses.  It encapsulates
 * a few common reference points that may be useful when rendering particle-based
 * objects that need to vary over their animation duration.
 * <br><br>
 * These references are the {@link ServerWorld}, the reference point from which the
 * shape or particle is rendered, and the current animation step number.  Individual
 * subclasses are free to add additional information that may be useful, such as
 * vertices for polygonal or polyhedral shapes, whether child objects should be drawn
 * (as seen in {@link net.mcbrincie.apel.lib.objects.ParticleCombiner}), and any other
 * information they may wish to expose.  These additional fields should be exposed by
 * using the {@link #addMetadata(Key, Object)} method so interceptors can reference
 * them in a reasonably safe manner.
 * <br><br>
 * <b>Modifications:</b> Interceptors that wish to modify the values may do so by
 * retrieving the value of interest using {@link #getMetadata(Key)}.
 * If those changes are in-place, such as for Vector3f and other JOML classes, no further
 * interaction with the map is required.  If the changes produce a new value, then
 * interceptors should call {@link #addMetadata(Key, Object)} to place the updated
 * value in the map so that the particle shape will receive it.
 * <br><br>
 * <b>Retrieving Values:</b> Particle objects should retrieve values using
 * {@link #getMetadata(Key)}.  This is a generic method that will look for
 * the value, and if found, safely cast and return it.  If a default value is desired,
 * {@link #getMetadata(Key, Object)} accepts and returns a default value of the correct
 * type.
 * <br><br>
 * <b>Warning:</b> Casting or auto-unboxing metadata values to primitive types may result in
 * {@code NullPointerException} if the map does not have a value for the given key or
 * if the value of the given key is {@code null}.  It is strongly recommended to use
 * {@link #getMetadata(Key, Object)} when handling primitive types.
 */
public class DrawContext {
    private final int currentStep;
    private final Vector3f position;
    private final ServerWorld world;
    private final Map<Key<?>, Object> metadata;

    /**
     * Indexes metadata in the DrawContext.  Keys are equal if both their name and their type match, regardless of
     * which actual Java class declares the Key.
     * <br><br>
     * Do note that equality will only work correctly if the code literally has the anonymous subclass declared
     * with the full parameterized type.  This is due to how Java's type erasure, compile-time checking, and runtime
     * class knowledge of what types are allowed interact.  Creating a generic factory method, like the following,
     * <strong>will not work</strong> because Java creates a single class that accepts any type so long as the code
     * calling it meets all type criteria.
     * <pre>
     * // Does not work at runtime!
     * static &lt;R&gt; Key&lt;R&gt; keyFor(String name) {
     *     return new Key<>(name) {};
     * }
     *
     * Key&lt;Integer&gt; integerKey = Class.keyFor("property");
     * </pre>
     * The {@code type} of such class will be {@code R}, not whatever is declared in the variable receiving the
     * instance ({@code Integer} in the example). Several common types have Key subclasses provided, but further
     * Keys are available by declaring additional anonymous subclasses:
     * <pre>
     * DrawContext.Key&lt;Type&gt; keyOfType = new DrawContext.Key&lt;Type&gt;("keyName") {};
     * </pre>
     *
     * @param <T> The type of value pointed to by this key.
     */
    public abstract static class Key<T> {
        protected final String name;
        protected TypeToken<T> type;

        public Key(String name) {
            this.name = name;
            // Extracts the generic type from the superclass (Key, in this case)
            this.type = new TypeToken<>(getClass()) {};
        }

        // This method typically would include "|| getClass() != o.getClass()" immediately after checking "o == null".
        // For Key, avoid that because defining equivalent keys may happen in two different classes,
        // since name and type are all that's required to be equivalent.  The class object itself is not
        // important *in this specific case*.
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            Key<?> key = (Key<?>) o;
            return Objects.equals(name, key.name) && Objects.equals(type, key.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }
    }

    // Pre-defined Key types

    public static Key<Integer> integerKey(String name) {
        return new Key<>(name) { };
    }

    public static Key<Boolean> booleanKey(String name) {
        return new Key<>(name) { };
    }

    public static Key<ParticleObject<?>> particleObjectKey(String name) {
        return new Key<>(name) { };
    }

    public static Key<Vector3f> vector3fKey(String name) {
        return new Key<>(name) { };
    }

    public static Key<Vector3f[]> vector3fArrayKey(String name) {
        return new Key<>(name) { };
    }

    public static Key<List<Pair<Vector3f, Vector3f>>> vector3fListPairKey(String name) {
        return new Key<>(name) { };
    }

    @SuppressWarnings("unused")
    public static Key<Float> floatKey(String name) {
        return new Key<>(name) { };
    }

    // End pre-defined key types

    /** Constructs an InterceptorData object to pass to an interceptor
     *
     * @param world the active ServerWorld reference
     * @param position the position at which drawing will occur
     * @param step the current animation step
     */
    public DrawContext(ServerWorld world, Vector3f position, int step) {
        this.currentStep = step;
        this.position = position;
        this.world = world;
        this.metadata = new HashMap<>();
    }

    /** Add metadata to the map for interceptors to use.
     *
     * @param value the value available to the interceptor
     */
    public <T> void addMetadata(Key<T> key, T value) {
        this.metadata.put(key, value);
    }

    /**
     * Retrieve a typed metadata value, possibly updated, from the metadata map. Callers should ensure they placed a
     * value in the metadata before calling this, or they risk a NullPointerException upon using the result from this
     * method.
     *
     * @param key the enum value identifying the metadata
     * @param <T> the type of the key and existing value (if present)
     * @return the value associated with the key, if present, else null
     *
     * @see DrawContext#getMetadata(Key, Object)
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getMetadata(Key<T> key) {
        // This cast is safe because `addMetadata` ensures the key and value types match at compile-time
        return (T) this.metadata.get(key);
    }

    /**
     * Retrieve a typed metadata value, possibly updated, from the metadata map. This method can safely use primitive
     * values as defaults, since ensuring a non-null default value means auto-unboxing will succeed. Callers should
     * ensure their default value's type is the type they want to receive from this method.
     *
     * @param key the enum value identifying the metadata
     * @param defaultValue the default value to be returned if no value exists, or the value is null
     * @param <T> the type of the key, existing value (if present), and default value
     * @return the value associated with the key, if present, else the default value
     *
     * @see DrawContext#getMetadata(Key)
     */
    @SuppressWarnings("unused")
    public <T> T getMetadata(Key<T> key, T defaultValue) {
        return Optional.ofNullable(this.getMetadata(key)).orElse(requireNonNull(defaultValue));
    }

    /** Get the current step of the animation this object is in.
     *
     * @return the current step of the animation
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /** Get the position from which the current shape's rendering is computed
     *
     * @return the position from which the current shape's rendering is computed
     */
    public Vector3f getPosition() {
        return position;
    }

    /** Get the active Minecraft ServerWorld
     *
     * @return the active Minecraft ServerWorld
     */
    public ServerWorld getWorld() {
        return world;
    }
}
