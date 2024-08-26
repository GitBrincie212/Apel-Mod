package net.mcbrincie.apel.lib.util.interceptor;

import com.google.common.reflect.TypeToken;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import org.joml.Vector3f;

import java.util.Objects;

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
public abstract class Key<T> {
    protected final String name;
    protected TypeToken<T> type;

    public Key(String name) {
        this.name = name;
        // Extracts the generic type from the superclass (Key, in this case)
        this.type = new TypeToken<>(getClass()) {
        };
    }

    public static Key<Integer> integerKey(String name) {
        return new Key<>(name) { };
    }

    public static Key<Boolean> booleanKey(String name) {
        return new Key<>(name) { };
    }

    /**
     * Create a Key that holds a ParticleObject.  Note that due to Java's generic handling, if a specific type of
     * ParticleObject is needed, a specific method will be required, such as this:
     * <pre>{@code
     * public static Key<ParticleCircle> particleCircleKey(String name) {
     *     return new Key<>(name) { };
     * }
     * }</pre>
     * @param name The name of the key
     * @return The Key instance, equal to all other Key instances with the same name and referring to the same type
     */
    public static Key<ParticleObject<? extends ParticleObject<?>>> particleObjectKey(String name) {
        return new Key<>(name) { };
    }

    public static Key<Vector3f> vector3fKey(String name) {
        return new Key<>(name) { };
    }

    public static Key<Vector3f[]> vector3fArrayKey(String name) {
        return new Key<>(name) { };
    }

    @SuppressWarnings("unused")
    public static Key<Float> floatKey(String name) {
        return new Key<>(name) { };
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
