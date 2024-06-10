package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

/** The particle object class that represents a single point.
 * <br>
 * Rotation and amount have no effect on this subclass.  While mods could
 * simply call {@link ServerWorld#spawnParticles(ParticleEffect, double, double, double, int, double, double, double, double)},
 * providing this class allows developers to incorporate individual particles
 * into more complex animations.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticlePoint extends ParticleObject {
    private Vector3f offset;

    private DrawInterceptor<ParticlePoint, BeforeDrawData> beforeDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticlePoint, AfterDrawData> afterDraw = DrawInterceptor.identity();

    public enum BeforeDrawData {}
    public enum AfterDrawData {}

    /** Constructor for the particle point.  It accepts a particle to use.
     * <br>
     * @param particleEffect The particle to use
     *
     * @see ParticlePoint#ParticlePoint(ParticleEffect, Vector3f)
     */
    public ParticlePoint(ParticleEffect particleEffect) {
        this(particleEffect, new Vector3f(0));
    }

    /** Constructor for the particle point.  It accepts a particle to use and the offset
     * at which to render.
     * <br>
     * @param particleEffect The particle to use
     * @param offset The offset from the draw position
     *
     * @see ParticlePoint#ParticlePoint(ParticleEffect)
     */
    public ParticlePoint(ParticleEffect particleEffect, Vector3f offset) {
        super(particleEffect);
        // Defensive copy to prevent inadvertent access
        this.offset = new Vector3f(offset);
    }

    /** Gets the offset vector
     *
     * @return a copy of the offset vector
     */
    public Vector3f getOffset() {
        // Defensive copy to prevent leaking the private instance member
        return new Vector3f(this.offset);
    }

    /** Sets the new offset vector and returns the previous value.
     *
     * @param offset the new offset vector
     * @return the previous offset vector
     */
    public Vector3f setOffset(Vector3f offset) {
        Vector3f prevOffset = this.offset;
        this.offset = offset;
        return prevOffset;
    }

    @Override
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        this.doBeforeDraw(world, step);
        Vector3f actualPosition = new Vector3f(drawPos).add(this.offset);
        this.drawParticle(world, actualPosition);
        this.doAfterDraw(world, step);
    }

    /** Set the interceptor to run before drawing the point.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticlePoint
     * instance.
     *
     * @param beforeDraw the new interceptor to execute before drawing the point
     */
    public void setBeforeDraw(DrawInterceptor<ParticlePoint, BeforeDrawData> beforeDraw) {
        this.beforeDraw = beforeDraw;
    }

    private void doBeforeDraw(ServerWorld world, int step) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, null, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }

    /** Sets the interceptor to run after drawing the point.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticlePoint
     * instance.
     *
     * @param afterDraw the new interceptor to execute after drawing the point
     */
    public void setAfterDraw(DrawInterceptor<ParticlePoint, AfterDrawData> afterDraw) {
        this.afterDraw = afterDraw;
    }

    private void doAfterDraw(ServerWorld world, int step) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, null, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }
}
