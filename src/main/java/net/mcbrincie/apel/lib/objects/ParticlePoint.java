package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.Optional;


/**
 * ParticlePoint allows for single particles to be rendered.  It will generally be used in conjunction
 * with other ParticleObjects, likely as part of a ParticleCombiner.  As a single point, scaling and rotation
 * do not apply, since Minecraft will render all particles facing the camera.  By default, it will be drawn
 * at the {@code drawPos} in the {@link #draw(ApelServerRenderer, int, Vector3f)} method.  Translation is possible
 * using {@link #setOffset(Vector3f)}.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticlePoint extends ParticleObject {
    private DrawInterceptor<ParticlePoint, AfterDrawData> afterDraw;
    private DrawInterceptor<ParticlePoint, BeforeDrawData> beforeDraw;

    public enum BeforeDrawData {
        DRAW_POSITION
    }
    public enum AfterDrawData {}

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticlePoint(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount);
        this.setAfterDraw(builder.afterDraw);
        this.setBeforeDraw(builder.beforeDraw);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.
     *
     * @param object The particle object to copy from
     */
    public ParticlePoint(ParticlePoint object) {
        super(object);
        this.beforeDraw = object.beforeDraw;
        this.afterDraw = object.afterDraw;
    }

    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        InterceptData<BeforeDrawData> interceptData = this.doBeforeDraw(renderer.getServerWorld(), drawPos, step);
        Vector3f objectDrawPosition = interceptData.getMetadata(BeforeDrawData.DRAW_POSITION, drawPos);
        renderer.drawParticle(this.particleEffect, step, objectDrawPosition.add(this.offset));
        this.doAfterDraw(renderer.getServerWorld(), objectDrawPosition, step);
        this.endDraw(renderer, step, objectDrawPosition);
    }

    /**
     * Set the interceptor to run before drawing the cone. The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticlePoint
     * instance.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param afterDraw The new interceptor to use
    */
    public final void setAfterDraw(DrawInterceptor<ParticlePoint, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, Vector3f drawPos, int step) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, drawPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /**
     * Set the interceptor to run before drawing the cone. The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticlePoint
     * instance.  The metadata will include the drawing position.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeDraw The new interceptor to use
     */
    public final void setBeforeDraw(DrawInterceptor<ParticlePoint, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private InterceptData<BeforeDrawData> doBeforeDraw(ServerWorld world, Vector3f drawPos, int step) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, drawPos, step, BeforeDrawData.class);
        interceptData.addMetadata(BeforeDrawData.DRAW_POSITION, drawPos);
        this.beforeDraw.apply(interceptData, this);
        return interceptData;
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B> {
        protected DrawInterceptor<ParticlePoint, AfterDrawData> afterDraw;
        protected DrawInterceptor<ParticlePoint, BeforeDrawData> beforeDraw;

        private Builder() {}

        /**
         * Sets the interceptor to run after drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticlePoint#setAfterDraw(DrawInterceptor)
         */
        public B afterDraw(DrawInterceptor<ParticlePoint, AfterDrawData> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.  This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticlePoint#setBeforeDraw(DrawInterceptor)
         */
        public B beforeDraw(DrawInterceptor<ParticlePoint, BeforeDrawData> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        @Override
        public ParticlePoint build() {
            return new ParticlePoint(this);
        }
    }
}
