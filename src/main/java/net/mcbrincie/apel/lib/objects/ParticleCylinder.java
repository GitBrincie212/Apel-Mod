package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a cylinder.
 * It has a radius which dictates how large or small the cylinder is depending on the
 * radius value supplied and a height value for how tall it is.  The cylinder is drawn
 * with particles evenly dispersed around its sides, but has no particles filling in
 * the bases.  One base is in the xz-plane by default, and the other base is in the positive-y
 * direction.  If the cylinder should be in the negative-y direction, a rotation about the
 * x-axis will achieve that.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCylinder extends ParticleObject {
    protected float radius;
    protected float height;

    private DrawInterceptor<ParticleCylinder, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleCylinder, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    /** This data is used before calculations (it contains the iterated rotation) */
    public enum BeforeDrawData {}

    /** This data is used after calculations (it contains the drawing position) */
    public enum AfterDrawData {}

    /** Constructor for the particle cylinder. It accepts as parameters the particle effect to use, the radius of the
     * cylinder, the height of the cylinder, the rotation to apply, and the number of particles.
     *
     * <p>This implementation calls setters for amount, rotation, height, and radius so checks are performed to
     * ensure valid values are accepted for each property.  Subclasses should take care not to violate these lest
     * they risk undefined behavior.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param radius The radius of the cylinder (how xz wise big it is)
     * @param height The height of the cylinder (how tall it is)
     * @param rotation The rotation to apply
     *
     * @see ParticleCylinder#ParticleCylinder(ParticleEffect, float, float, int)
    */
    public ParticleCylinder(
            @NotNull ParticleEffect particleEffect, float radius, float height, Vector3f rotation, int amount
    ) {
        super(particleEffect, rotation);
        this.setRadius(radius);
        this.setAmount(amount);
        this.setHeight(height);
    }

    /** Constructor for the particle cylinder. It accepts as parameters the particle effect to use, the radius of the
     * cylinder, the height of the cylinder, and the number of particles.
     *
     * <p>This implementation calls setters for amount, rotation, height, and radius so checks are performed to
     * ensure valid values are accepted for each property.  Subclasses should take care not to violate these lest
     * they risk undefined behavior.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param height The height of the cylinder (how tall it is)
     * @param radius The radius of the cylinder (how xz wise big it is)
     *
     * @see ParticleCylinder#ParticleCylinder(ParticleEffect, float, float, Vector3f, int)
    */
    public ParticleCylinder(
            @NotNull ParticleEffect particleEffect,
            float radius, float height, int amount
    ) {
        this(particleEffect, radius, height, new Vector3f(0,0,0), amount);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param cylinder The particle cylinder object to copy from
    */
    public ParticleCylinder(ParticleCylinder cylinder) {
        super(cylinder);
        this.radius = cylinder.radius;
        this.height = cylinder.height;
        this.amount = cylinder.amount;
        this.afterDraw = cylinder.afterDraw;
        this.beforeDraw = cylinder.beforeDraw;
    }

    /** Gets the radius of the ParticleCylinder and returns it.
     *
     * @return the radius of the ParticleCylinder
     */
    public float getRadius() {
        return radius;
    }

    /** Set the radius of this ParticleCylinder and returns the previous radius that was used.  Radius must be positive.
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public float setRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the height of the ParticleCylinder and returns it.
     *
     * @return the height of the ParticleCylinder
    */
    public float getHeight() {
        return height;
    }

    /** Sets the height of the ParticleCylinder and returns the previous height that was used.  Height must be positive.
     *
     * @param height The new height
     * @return The previous used height
     */
    public float setHeight(float height) {
        if (height < 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        float prevHeight = this.height;
        this.height = height;
        return prevHeight;
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getServerWorld(), step, drawPos);
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);
        renderer.drawCylinder(this.particleEffect, step, objectDrawPos, this.radius, this.height, this.rotation, this.amount);
        this.doAfterDraw(renderer.getServerWorld(), step, drawPos);
        this.endDraw(renderer, step, drawPos);
    }

    /** Set the interceptor to run after drawing the cylinder. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the cylinder is rendered.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public void setAfterDraw(DrawInterceptor<ParticleCylinder, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f centerPos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, centerPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run prior to drawing the cylinder. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the cylinder is rendered.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public void setBeforeDraw(DrawInterceptor<ParticleCylinder, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f pos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, pos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }
}
