package net.mcbrincie.apel.lib.util.interceptor;

import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * AnimationContext is an extensible collection of relevant information that may be used by the {@code beforeRender}
 * interceptor defined on {@link net.mcbrincie.apel.lib.animators.PathAnimatorBase} subclasses.  It encapsulates
 * common information that is likely to be useful when making adjustments to the animator and its path.  This common
 * information is the {@link ServerWorld}, the point used as the current step's origin, and the current animation step
 * number.
 * <p>
 * <strong>Custom Metadata: </strong>Individual animators are free to add additional information that may be useful,
 * such as the segment number for animators that traverse a segment-based path (e.g., the {@code LinearAnimator}).
 * These additional fields should be exposed by using the {@link #addMetadata(DrawContext.Key, Object)} method so
 * interceptors can retrieve the value in a type-safe manner.
 * <p>
 * <strong>Modifications: </strong>Interceptors that wish to modify the values should retrieve the metadata with one
 * of the {@code getMetadata} methods.  If the modifications are in-place, such as those for {@code Vector3f} and other
 * JOML classes, no further interaction with the metadata is required.  If the changes require creating a new instance
 * or updating a primitive value, then interceptors should call {@link #addMetadata(DrawContext.Key, Object)} to place
 * the updated value back in the metadata so the animator may retrieve the updated value.
 * <p>
 * <strong>Retrieving Values:</strong> Animators should retrieve values using {@link #getMetadata(DrawContext.Key)}.
 * This is a generic, type-safe method that will return the value from the metadata map.  If a default value is desired,
 * or nothing was placed in the metadata, {@link #getMetadata(DrawContext.Key, Object)} will accept and return a
 * default value of the correct type.
 * <p>
 * <strong>Warning:</strong> Casting or auto-unboxing metadata values to primitive values may result in
 * {@code NullPointerException} if the given key does not have a value or has a null value.  It is strongly recommended
 * to use {@link #getMetadata(DrawContext.Key, Object)} when handling primitive types.
 */
public class AnimationContext {
    private final ServerWorld world;

    private final Vector3f position;
    private final int currentStep;
    private boolean shouldRender;
    private final Map<DrawContext.Key<?>, Object> metadata;

    /**
     * Creates an AnimationContext with no position or step number.  Useful for composite animators that do not render
     * objects directly.
     *
     * @param world The Minecraft ServerWorld instance
     */
    public AnimationContext(ServerWorld world) {
        this(world, null, 0);
    }

    /**
     * Creates an AnimationContext for use by animators that directly render objects.
     *
     * @param world The Minecraft ServerWorld instance
     * @param position The position along the animation path
     * @param currentStep The step number of the current animator
     */
    public AnimationContext(ServerWorld world, Vector3f position, int currentStep) {
        this.world = world;
        this.position = position;
        this.currentStep = currentStep;
        this.metadata = new HashMap<>();
        this.shouldRender = true;
    }

    /**
     * Get the active Minecraft ServerWorld.  Provided in case the interceptor needs information from the ServerWorld.
     *
     * @return the active Minecraft ServerWorld
     */
    public ServerWorld getWorld() {
        return world;
    }

    /**
     * Get the position from which the current shape's rendering is computed.  It is considered the origin from which
     * any particle objects render themselves.  This value may be modified in-place, but it will not live beyond the
     * current step.
     * <p>
     * Note that this value will be {@code null} for composite animations such as the {@code ParallelAnimator}.
     *
     * @return the position from which the current shape's rendering is computed
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Get the current step number of the animation.  This starts from 0 and counts until the animation is complete,
     * except when called on composite animators such as {@code ParallelAnimator}.  When called on composite animators,
     * it always returns 0, since they do not have steps.
     *
     * @return The current step number of the animation
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * Get the rendering status of the current step.
     *
     * @return the rendering status
     */
    public boolean shouldRender() {
        return this.shouldRender;
    }

    /**
     * Set whether to render on the current step or not.
     *
     * @param shouldRender Whether to render (true) or not (false)
     */
    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    /**
     * Add metadata for Animator interceptors to use.
     *
     * @param key The key name used to refer to the value
     * @param value the value available to the interceptor
     * @param <T> the type of the key and new value
     */
    public <T> void addMetadata(DrawContext.Key<T> key, T value) {
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
     * @see DrawContext#getMetadata(DrawContext.Key, Object)
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getMetadata(DrawContext.Key<T> key) {
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
     * @see DrawContext#getMetadata(DrawContext.Key)
     */
    @SuppressWarnings("unused")
    public <T> T getMetadata(DrawContext.Key<T> key, T defaultValue) {
        return Optional.ofNullable(this.getMetadata(key)).orElse(requireNonNull(defaultValue));
    }
}
