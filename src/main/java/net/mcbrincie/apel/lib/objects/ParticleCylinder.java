package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a 3D cylinder.
 * It has a radius which dictates how large or small the cylinder is depending on the
 * radius value supplied and a height value for how tall it is
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCylinder extends ParticleObject {
    protected float radius;
    protected float height;
    private DrawInterceptor<ParticleCylinder, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleCylinder, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    /** This data is used before calculations (it contains the iterated rotation) */
    public enum BeforeDrawData {
        ITERATED_ROTATION
    }

    /** This data is used after calculations (it contains the drawing position) */
    public enum AfterDrawData {
        DRAW_POSITION
    }

    /** Constructor for the particle cylinder which is a 3D shape. It accepts as parameters
     * the particle effect to use, the radius of the cylinder, the height of the cylinder,
     * the rotation to apply & the number of particles. There is also a simplified version
     * for no rotation.
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
            @NotNull ParticleEffect particleEffect, float radius,
            float height, Vector3f rotation, int amount
    ) {
        super(particleEffect, rotation);
        this.setRadius(radius);
        this.setAmount(amount);
        this.setHeight(height);
    }

    /** Constructor for the particle cylinder which is a 3D shape. It accepts as parameters
     * the particle effect to use, the radius of the cylinder, the height of the cylinder
     * & the number of particles. There is also a version that allows for rotation.
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
        this.amount = cylinder.amount;
        this.afterDraw = cylinder.afterDraw;
        this.beforeDraw = cylinder.beforeDraw;
        this.height = cylinder.height;
    }

    /** Gets the radius of the ParticleCylinder and returns it.
     *
     * @return the radius of the ParticleCylinder
     */
    public float getRadius() {
        return radius;
    }

    /** Set the radius of this ParticleCylinder and returns the previous radius that was used.
     *
     * @param radius the new radius
     * @return the previously used radius
     */
    public float setRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Height cannot be negative");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    /** Gets the height of the ParticleCylinder and returns it
     *
     * @return the height of the ParticleCylinder
    */
    public float getHeight() {return height;}

    /** Sets the height of the ParticleCylinder and returns the previous height that was used
     *
     * @param height The new height
     * @return The previous used height
     */
    public float setHeight(float height) {
        if (height < 0) {
            throw new IllegalArgumentException("Height cannot be negative");
        }
        float prevHeight = this.height;
        this.height = height;
        return prevHeight;
    }

    @Override
    public void draw(ApelRenderer renderer, int step, Vector3f drawPos) {
        float stepHeight = this.height / this.amount;
        float stepAngle = (float) Math.TAU / 1.618033f;
        for (int i = 0; i < this.amount; i++) {
            float angle = i * stepAngle;
            InterceptData<ParticleCylinder.BeforeDrawData> interceptData = this.doBeforeDraw(
                    renderer.getWorld(), step, drawPos, angle
            );
            angle = interceptData.getMetadata(BeforeDrawData.ITERATED_ROTATION, angle);
            Vector3f finalPosVec = this.drawEllipsePoint(renderer, this.radius, this.radius, angle, drawPos, step);
            this.doAfterDraw(renderer.getWorld(), step, finalPosVec, drawPos);
        }
        this.endDraw(renderer, step, drawPos);
    }

    /** Set the interceptor to run after drawing the cylinder. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the cylinder is rendered.  It will also have the position around the cylinder
     * at which the current particle was drawn.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public void setAfterDraw(DrawInterceptor<ParticleCylinder, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f drawPos, Vector3f centerPos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, centerPos, step, AfterDrawData.class);
        interceptData.addMetadata(AfterDrawData.DRAW_POSITION, drawPos);
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run prior to drawing the cylinder. The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the
     * position where the cylinder is rendered.  It will also have the angle around the cylinder at
     * which the current particle is.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing each particle
     */
    public void setBeforeDraw(DrawInterceptor<ParticleCylinder, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private InterceptData<BeforeDrawData> doBeforeDraw(ServerWorld world, int step, Vector3f pos, float angle) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, pos, step, BeforeDrawData.class);
        interceptData.addMetadata(BeforeDrawData.ITERATED_ROTATION, angle);
        this.beforeDraw.apply(interceptData, this);
        return interceptData;
    }
}
