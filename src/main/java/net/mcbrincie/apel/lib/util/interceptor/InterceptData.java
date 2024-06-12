package net.mcbrincie.apel.lib.util.interceptor;

import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/** InterceptData is an extensible mechanism for providing information to the
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
 * using the {@link #addMetadata(Enum, Object)} method so interceptors can reference
 * them in a reasonably safe manner.
 * <br><br>
 * <b>Modifications:</b> Interceptors that wish to modify the values may do so by
 * retrieving the value of interest.  They will have to cast it to a usable type,
 * as the metadata map only has {@code Object} references.  After casting, interceptors
 * are free to make any changes necessary.  If those changes are in-place, no further
 * interaction with the map is required.  If the changes produce a new value, then
 * interceptors should call {@link #addMetadata(Enum, Object)} to place the updated
 * value in the map so that the particle shape will receive it.
 * <br><br>
 * <b>Retrieving Values:</b> Particle objects should retrieve values using
 * {@link #getMetadata(Enum, Object)}.  This is a generic method that will look for
 * the value, if found, cast and return it, and if not found, it will return the
 * given default value.
 * <br><br>
 * <b>Warning:</b> Casting metadata values to primitive types may result in
 * {@code NullPointerException} if the map does not have a value for the given key or
 * if the value of the given key is {@code null}.
 *
 * @param <T>
 */
public class InterceptData<T extends Enum<T>> {
    private final int currentStep;
    private final Vector3f position;
    private final ServerWorld world;
    private final EnumMap<T, Object> metadata;

    /** Constructs an InterceptorData object to pass to an interceptor
     *
     * @param world the active ServerWorld reference
     * @param position the position at which drawing will occur
     * @param step the current animation step
     * @param keyType the type of enum used for keys in the metadata map
     */
    public InterceptData(ServerWorld world, Vector3f position, int step, Class<T> keyType) {
        this.currentStep = step;
        this.position = position;
        this.world = world;
        this.metadata = new EnumMap<>(keyType);
    }

    /** Add metadata to the map for interceptors to use.
     *
     * @param name the enum value identifying the metadata
     * @param value the value available to the interceptor
     */
    public void addMetadata(T name, Object value) {
        this.metadata.put(name, value);
    }

    /** Retrieve a metadata value, possibly updated, from the metadata map.  The value
     * will need to be cast by the caller to a usable type, unless they desire {@code Object}.
     * <br><br>
     * <b>It is highly recommended to use {@link #getMetadata(Enum, Object)} instead!</b>
     * <br><br>
     * <b>Warning:</b> Casting metadata values to primitive types may result in
     * {@code NullPointerException} if the map does not have a value for the given key or
     * if the value of the given key is {@code null}.
     *
     * @param name the enum value identifying the metadata
     * @return the value associated with the key, if present, else null
     */
    @SuppressWarnings({"unused"})
    public Object getMetadata(T name) {
        return this.metadata.get(name);
    }

    /** Retrieve a typed metadata value, possibly updated, from the metadata map.  The
     * value will be cast to the type of the default value.  If this cast isn't possible,
     * the caller will see a {@code ClassCastException} upon retrieving the value.
     * <br><br>
     * This method should be safe for primitive values as defaults, since ensuring a value
     * means the auto-unboxing will succeed.
     * <br><br>
     * Callers should ensure their default value's type is the type they want to receive
     * from this method.
     *
     * @param name the enum value identifying the metadata
     * @param defaultValue the default value to be returned if no value exists, or it is null
     * @return the value associated with the key, if present, else the default value
     * @param <Z> the type of the default value, and the type to which an existing value will
     *           be cast
     */
    @SuppressWarnings({"unchecked"})
    public <Z> Z getMetadata(T name, Z defaultValue) {
        return (Z) Optional.ofNullable(this.metadata.get(name)).orElse(requireNonNull(defaultValue));
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
