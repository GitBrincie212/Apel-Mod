package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.Optional;

/** The particle object class that represents a 3D shape(a cone).
 * It requires a height value which dictates how tall the cone is as well as
 * the maximum radius, it also accepts rotation for the cone
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleCone extends ParticleObject {
    protected float height;
    protected float radius;

    private DrawInterceptor<ParticleCone, AfterDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleCone, BeforeDrawData> beforeDraw = DrawInterceptor.identity();

    public enum BeforeDrawData {}
    public enum AfterDrawData {}

    /** Constructor for the particle cone which is a 3D shape. It accepts as parameters
     * the particle effect to use, the height of the cone, the maximum radius,
     * the rotation to apply and the number of particles.
     * There is also a simplified constructor for no rotation
     *
     * @param particleEffect The particle effect to use
     * @param height The height of the cone
     * @param radius The radius of the cone
     * @param rotation The rotation to apply
     * @param amount The number of particles
     *
     * @see ParticleCone#ParticleCone(ParticleEffect, float, float, int)
    */
    public ParticleCone(ParticleEffect particleEffect, float height, float radius, Vector3f rotation, int amount) {
        super(particleEffect);
        this.setAmount(amount);
        this.setRotation(rotation);
        this.setHeight(height);
        this.setRadius(radius);
    }

    /** Constructor for the particle cone which is a 3D shape. It accepts as parameters
     * the particle effect to use, the height of the cone, the maximum radius,
     * the rotation to apply and the number of particles.
     * There is also a more complex version for rotation
     *
     * @param particleEffect The particle effect to use
     * @param height The height of the cone
     * @param radius The radius of the cone
     * @param amount The number of particles
     *
     * @see ParticleCone#ParticleCone(ParticleEffect, float, float, Vector3f, int)
     */
    public ParticleCone(ParticleEffect particleEffect, float height, float radius, int amount) {
        this(particleEffect, height, radius, new Vector3f(), amount);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param cone The particle object to copy from
    */
    public ParticleCone(ParticleCone cone) {
        super(cone);
        this.height = cone.height;
        this.radius = cone.radius;
        this.beforeDraw = cone.beforeDraw;
        this.afterDraw = cone.afterDraw;
    }

    /** Sets the height of the cone
     *
     * @param height The new height
     * @return The previous height
    */
    public float setHeight(float height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be above 0");
        }
        float prevHeight = this.height;
        this.height = height;
        return prevHeight;
    }

    /** Gets the height of the cone
     *
     * @return The height of the cone
     */
    public float getHeight() {return this.height;}

    /** Gets the maximum radius of the cone and returns it.
     *
     * @return the maximum radius of the cone
     */
    public float getRadius() {
        return radius;
    }

    /** Set the maximum radius of this cone and returns the previous maximum radius that was used.
     *
     * @param radius the new maximum radius
     * @return the previously used maximum radius
     */
    public float setRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        float prevRadius = this.radius;
        this.radius = radius;
        return prevRadius;
    }

    @Override
    public void draw(ApelRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getWorld(), step);

        final double sqrt5Plus1 = 3.23606;
        Quaternionfc quaternion = new Quaternionf()
                .rotateZ(this.rotation.z)
                .rotateY(this.rotation.y)
                .rotateX(this.rotation.x);
        for (int i = 0; i < this.amount; i++) {
            // Offset into the real-number distribution
            float k = i + .5f;
            // Project point on a unit sphere
            float phi = trigTable.getArcCosine(1f - ((2f * k) / this.amount));
            float theta = (float) (Math.PI * k * sqrt5Plus1);
            double sinPhi = trigTable.getSine(phi);
            float x = (float) (trigTable.getCosine(theta) * sinPhi);
            float z = (float) (trigTable.getSine(theta) * sinPhi);
            float y = (x * x + z * z) * this.height;
            // Scale, rotate, translate
            Vector3f pos = new Vector3f(x * this.radius, y, z * this.radius)
                    .rotate(quaternion)
                    .add(drawPos)
                    .add(this.offset);
            renderer.drawParticle(this.particleEffect, step, pos);
        }
        this.doAfterDraw(renderer.getWorld(), step);
        this.endDraw(renderer, step, drawPos);
    }

    /** Sets the interceptor to run after drawing the cone. The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleCone
     * instance.
     *
     * @param afterDraw the new interceptor to execute after drawing the cone
     */
    public void setAfterDraw(DrawInterceptor<ParticleCone, AfterDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, null, step, AfterDrawData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run before drawing the cone. The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleCone
     * instance.
     *
     * @param beforeDraw the new interceptor to execute before drawing the cone
     */
    public void setBeforeDraw(DrawInterceptor<ParticleCone, BeforeDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, null, step, BeforeDrawData.class);
        this.beforeDraw.apply(interceptData, this);
    }
}
