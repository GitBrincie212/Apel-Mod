package net.mcbrincie.apel.lib.util.interceptor;

import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class AnimationContext {
    private final ServerWorld world;

    private final Vector3f position;
    private boolean shouldRender;
    private final Map<DrawContext.Key<?>, Object> metadata;

    public AnimationContext(ServerWorld world, Vector3f position) {
        this.world = world;
        this.position = position;
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
     *
     * @return the position from which the current shape's rendering is computed
     */
    public Vector3f getPosition() {
        return position;
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
