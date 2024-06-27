package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;

/**
 * The particle object class that represents an ellipsoid (3D shape) and not a 2D circle.
 * It has three semi-axis values that determine the ellipsoid's shape. It projects the <em>golden spiral</em>
 * on to the ellipsoid to distribute particles evenly across the surface.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleEllipsoid extends ParticleObject {
    public static final double SQRT_5_PLUS_1 = 3.23606;
    protected float xSemiAxis;
    protected float ySemiAxis;
    protected float zSemiAxis;

    private DrawInterceptor<ParticleEllipsoid, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleEllipsoid, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    /**
     * This data is used before calculations
     */
    public enum BeforeDrawData {}

    /**
     * This data is used after calculations
     */
    public enum AfterDrawData {}

    /**
     * Constructor for the particle ellipsoid which is a 3D shape. It accepts as parameters
     * the particle effect to use, the semi-axes of the ellipsoid, the number of particles,
     * and the rotation to apply. There is also a simplified version for no rotation.
     *
     * @param particleEffect The particle to use
     * @param xSemiAxis The length of the X semi-axis
     * @param ySemiAxis The length of the Y semi-axis
     * @param zSemiAxis The length of the Z semi-axis
     * @param amount The number of particles for the object
     * @param rotation The rotation to apply
     * @see ParticleEllipsoid#ParticleEllipsoid(ParticleEffect, float, float, float, int)
     */
    public ParticleEllipsoid(
            @NotNull ParticleEffect particleEffect, float xSemiAxis, float ySemiAxis, float zSemiAxis, int amount,
            Vector3f rotation
    ) {
        super(particleEffect, rotation);
        this.setXSemiAxis(xSemiAxis);
        this.setYSemiAxis(ySemiAxis);
        this.setZSemiAxis(zSemiAxis);
        this.setAmount(amount);
    }

    /**
     * Constructor for the particle cuboid which is a 3D shape. It accepts as parameters
     * the particle effect to use, the semi-axes of the ellipsoid, and the number of particles.
     * It is a simplified version for the case when no rotation is meant to be applied.
     *
     * @param particleEffect The particle to use
     * @param xSemiAxis The length of the X semi-axis
     * @param ySemiAxis The length of the Y semi-axis
     * @param zSemiAxis The length of the Z semi-axis
     * @param amount The number of particles for the object
     * @see ParticleEllipsoid#ParticleEllipsoid(ParticleEffect, float, float, float, int, Vector3f)
     */
    public ParticleEllipsoid(
            @NotNull ParticleEffect particleEffect, float xSemiAxis, float ySemiAxis, float zSemiAxis, int amount
    ) {
        this(particleEffect, xSemiAxis, ySemiAxis, zSemiAxis, amount, new Vector3f(0));
    }

    /**
     * The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param particleEllipsoid The particle ellipsoid object to copy from
     */
    public ParticleEllipsoid(ParticleEllipsoid particleEllipsoid) {
        super(particleEllipsoid);
        this.xSemiAxis = particleEllipsoid.xSemiAxis;
        this.ySemiAxis = particleEllipsoid.ySemiAxis;
        this.zSemiAxis = particleEllipsoid.zSemiAxis;
        this.amount = particleEllipsoid.amount;
        this.afterDraw = particleEllipsoid.afterDraw;
        this.beforeDraw = particleEllipsoid.beforeDraw;
    }

    /**
     * Gets length of the X semi-axis
     *
     * @return the length of the X semi-axis
     */
    public float getXSemiAxis() {
        return this.xSemiAxis;
    }

    /**
     * Sets length of the X semi-axis
     *
     * @param xSemiAxis The length of the X semi-axis
     * @return The previous length of the X semi-axis
     */
    public float setXSemiAxis(float xSemiAxis) {
        if (xSemiAxis <= 0) {
            throw new IllegalArgumentException("Length of X semi-axis cannot be below or equal to 0");
        }
        float prevXSemiAxis = this.xSemiAxis;
        this.xSemiAxis = xSemiAxis;
        return prevXSemiAxis;
    }

    /**
     * Gets length of the Y semi-axis
     *
     * @return the length of the Y semi-axis
     */
    public float getYSemiAxis() {
        return this.ySemiAxis;
    }

    /**
     * Sets length of the Y semi-axis
     *
     * @param ySemiAxis The length of the Y semi-axis
     * @return The previous length of the Y semi-axis
     */
    public float setYSemiAxis(float ySemiAxis) {
        if (ySemiAxis <= 0) {
            throw new IllegalArgumentException("Length of Y semi-axis cannot be below or equal to 0");
        }
        float prevYSemiAxis = this.ySemiAxis;
        this.ySemiAxis = ySemiAxis;
        return prevYSemiAxis;
    }

    /**
     * Gets length of the Z semi-axis
     *
     * @return the length of the Z semi-axis
     */
    public float getZSemiAxis() {
        return this.zSemiAxis;
    }

    /**
     * Sets length of the Z semi-axis
     *
     * @param zSemiAxis The length of the Z semi-axis
     * @return The previous length of the Z semi-axis
     */
    public float setZSemiAxis(float zSemiAxis) {
        if (zSemiAxis <= 0) {
            throw new IllegalArgumentException("Length of Z semi-axis cannot be below or equal to 0");
        }
        float prevZSemiAxis = this.zSemiAxis;
        this.zSemiAxis = zSemiAxis;
        return prevZSemiAxis;
    }

    @Override
    public void draw(ApelRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getWorld(), step, drawPos);
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);
        renderer.drawEllipsoid(this.particleEffect, step, objectDrawPos, this.xSemiAxis, this.ySemiAxis, this.zSemiAxis,
                               this.rotation, this.amount
        );
        this.doAfterDraw(renderer.getWorld(), step, drawPos);
        this.endDraw(renderer, step, drawPos);
    }

    /**
     * Set the interceptor to run after drawing the ellipsoid.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the position
     * where the ellipsoid is rendered.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public void setAfterDraw(DrawInterceptor<ParticleEllipsoid, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, drawPos, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /**
     * Set the interceptor to run prior to drawing the ellipsoid.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the step number of the animation, and the position
     * where the ellipsoid is rendered.
     *
     * @param beforeDraw the new interceptor to execute prior to drawing the ellipsoid
     */
    public void setBeforeDraw(DrawInterceptor<ParticleEllipsoid, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, drawPos, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }
}
