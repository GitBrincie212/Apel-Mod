package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
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
    private DrawInterceptor<ParticlePoint, BeforeDrawData> beforeDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticlePoint, AfterDrawData> afterDraw = DrawInterceptor.identity();

    public enum BeforeDrawData {
        DRAW_POSITION
    }
    public enum AfterDrawData {}

    /** Constructor for the particle object which is a point. It accepts as parameters
     * the particle effect to use. It is a simplified version of the previous constructor
     * and is meant to be used when you want the object to not have a rotation offset.
     * In the case you do want, there is a constructor for that (won't apply to this class)
     *
     * @param particleEffect The particle effect to use
    */
    public ParticlePoint(ParticleEffect particleEffect) {
        super(particleEffect);
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

    /** Set the interceptor to run before drawing the cone. The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticlePoint
     * instance.  The metadata will include the drawing position.
     *
     * @param beforeDraw The new interceptor to use
     */
    public void setBeforeDraw(DrawInterceptor<ParticlePoint, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    /** Set the interceptor to run before drawing the cone. The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticlePoint
     * instance.
     *
     * @param afterDraw The new interceptor to use
    */
    public void setAfterDraw(DrawInterceptor<ParticlePoint, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private InterceptData<BeforeDrawData> doBeforeDraw(ServerWorld world, Vector3f drawPos, int step) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, drawPos, step, BeforeDrawData.class);
        interceptData.addMetadata(BeforeDrawData.DRAW_POSITION, drawPos);
        this.beforeDraw.apply(interceptData, this);
        return interceptData;
    }

    private void doAfterDraw(ServerWorld world, Vector3f drawPos, int step) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, drawPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }
}
