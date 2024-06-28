package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.Optional;


/** The base class for any particle object. Particle objects are the things
 * that will be rendered. This can be a cube, a sphere, a 2D circle, a cat, a
 * dog... etc. The calculation logic is done in the draw method which the
 * handler system periodically calls each render step. You can inherit
 * from the class and even use it (it will be a point), it has common interceptors
 * like the before calculation interceptor & the after calculation interceptor.
 * <br><br>
 * <strong>Note</strong> rotation calculations are in radians and not in degrees.
 * As well as if the rotation on one or multiple axis exceeds 2π, then it is rounded
 * to the scope for that (-2π, 2π)
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
     * the particle to use and the rotation to apply (which has no effect. Only on the
     * path animators that extend this class). There is also a simplified version
     * for no rotation.
     *
     * @see ParticlePoint#ParticlePoint(ParticlePoint)
     *
     * @param particleEffect The particle effect to use
     * @param rotation The rotation (IN RADIANS)
    */
    public ParticlePoint(ParticleEffect particleEffect, Vector3f rotation) {
        super(particleEffect, rotation);
    }

    /** Constructor for the particle object which is a point. It accepts as parameters
     * the particle effect to use. It is a simplified version of the previous constructor
     * and is meant to be used when you want the object to not have a rotation offset.
     * In the case you do want, there is a constructor for that (won't apply to this class)
     *
     * @see ParticlePoint#ParticlePoint(ParticleEffect, Vector3f)
     *
     * @param particleEffect The particle effect to use
    */
    public ParticlePoint(ParticleEffect particleEffect) {
        super(particleEffect);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
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


    /** Sets the before draw interceptor, the method executes right before the particle point
     * is drawn onto the screen. And for data it has the drawing position
     *
     * @param beforeDraw The new interceptor to use
     */
    public void setBeforeDraw(DrawInterceptor<ParticlePoint, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    /** Sets the after draw interceptor, the method executes right after the particle point
     * is drawn onto the screen. And has no data attached
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
