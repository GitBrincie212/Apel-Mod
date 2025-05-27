package net.mcbrincie.apel.lib.util.interceptor.context;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * DrawContext is an extensible mechanism for providing information to the {@code beforeDraw} and {@code afterDraw}
 * interceptors defined on {@link net.mcbrincie.apel.lib.objects.ParticleObject} subclasses.  It encapsulates common
 * information that is likely to be useful when rendering particle-based objects that need to vary over their animation
 * duration. This common information is the {@link ServerWorld}, the reference point from which the shape or particle is
 * rendered, and the current animation step number.
 * <p>
 * <strong>Custom Metadata: </strong>Individual subclasses are free to add additional information that may be useful,
 * such as vertices for polygonal or polyhedral shapes, whether child objects should be drawn (as seen in
 * {@link net.mcbrincie.apel.lib.objects.ParticleCombiner}), and any other information they may wish to expose.  These
 * additional fields should be exposed by defining a {@link Key} with a name and data type, and then using the
 * {@link #addMetadata(Key, Object)} method so interceptors can retrieve the value in a type-safe manner.
 * <p>
 * <strong>Modifications:</strong> Interceptors that wish to modify the values should retrieve the metadata with one
 * of the {@code getMetadata} methods.  If the modifications are in-place, such as those for {@code Vector3f} and other
 * JOML classes, no further interaction with the metadata is required.  If the changes require creating a new instance
 * or updating a primitive value, then interceptors should call {@link #addMetadata(Key, Object)} to place the updated
 * value back in the metadata so the particle object may retrieve the updated value.
 * <p>
 * <strong>Retrieving Values:</strong> Particle objects should retrieve values using {@link #getMetadata(Key)}.  This
 * is a generic, type-safe method that will return the value from the metadata map.  If a default value is desired, or
 * nothing was placed in the metadata, {@link #getMetadata(Key, Object)} will accept and return a default value of the
 * correct type.
 * <p>
 * <strong>Warning:</strong> Casting or auto-unboxing metadata values to primitive types may result in
 * {@code NullPointerException} if the given key does not have a value or has a null value.  It is strongly recommended
 * to use {@link #getMetadata(Key, Object)} when handling primitive types.
 */
public class DrawContext<E extends ComputedEasingPO> {
    private final int currentStep;
    private final int numberOfSteps;
    private final Vector3f position;
    private final ServerWorld world;
    private final float deltaTickTime;
    private final Map<Key<?>, Object> metadata;
    private final E computedEasings;

    /** Constructs an InterceptorData object to pass to an interceptor
     *
     * @param world the active ServerWorld reference
     * @param position the position at which the drawing will occur
     * @param step the current animation step
     */
    public DrawContext(
            ServerWorld world, Vector3f position, int step, int numberOfSteps,
            float deltaTickTime, E computedEasingPO
    ) {
        this.currentStep = step;
        this.position = position;
        this.world = world;
        this.metadata = new HashMap<>();
        this.numberOfSteps = numberOfSteps;
        this.computedEasings = computedEasingPO;
        this.deltaTickTime = deltaTickTime;
    }

    public DrawContext(DrawContext<E> context) {
        this.currentStep = context.currentStep;
        this.position = context.position;
        this.deltaTickTime = context.deltaTickTime;
        this.numberOfSteps = context.numberOfSteps;
        this.metadata = new HashMap<>();
        this.world = context.world;
        this.computedEasings = context.computedEasings;
    }

    public DrawContext(ApelServerRenderer renderer, DrawContext<E> context, E computedEasings) {
        this(
                renderer.getServerWorld(),
                context.getPosition(),
                context.getCurrentStep(),
                context.getNumberOfStep(),
                context.getDeltaTickTime(),
                computedEasings
        );
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

    /** Get the computed easing of the particle object (rotation, offset and amount specifically)
     *
     * @return a collection of the computed easings
     */
    public E getComputedEasings() {
        return computedEasings;
    }

    /** Get the number of steps of the path animator.
     *
     * @return the number of steps the animation has
     */
    public int getNumberOfStep() {
        return numberOfSteps;
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

    /** Get the delta tick time. Which measures the time difference between the last and the current tick
     *
     * @return the delta tick time
     */
    @SuppressWarnings("unused")
    public float getDeltaTickTime() {
        return this.deltaTickTime;
    }
}
