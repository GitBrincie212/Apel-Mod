package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import org.joml.Vector3f;


/**
 * ParticlePoint allows for single particles to be rendered.  It will generally be used in conjunction
 * with other ParticleObjects, likely as part of a ParticleCombiner.  As a single point, scaling and rotation
 * do not apply, since Minecraft will render all particles facing the camera.  By default, it will be drawn
 * at the {@code drawPos} in the {@link #draw(ApelServerRenderer, DrawContext)} method.  Translation is possible
 * using {@link #setOffset(Vector3f)}.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticlePoint extends ParticleObject<ParticlePoint> {

    public static final DrawContext.Key<Vector3f> DRAW_POSITION = DrawContext.vector3fKey("drawPosition");

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticlePoint(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw
        );
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.
     *
     * @param object The particle object to copy from
     */
    public ParticlePoint(ParticlePoint object) {
        super(object);
    }

    @Override
    protected void prepareContext(DrawContext drawContext) {
        drawContext.addMetadata(DRAW_POSITION, drawContext.getPosition());
    }

    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPosition = drawContext.getMetadata(DRAW_POSITION); //, drawContext.getPosition());
        renderer.drawParticle(this.particleEffect, drawContext.getCurrentStep(), objectDrawPosition.add(this.offset));
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticlePoint> {
        protected DrawInterceptor<ParticlePoint> afterDraw;
        protected DrawInterceptor<ParticlePoint> beforeDraw;

        private Builder() {}

        @Override
        public ParticlePoint build() {
            return new ParticlePoint(this);
        }
    }
}
