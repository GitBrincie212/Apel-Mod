package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;
import oshi.util.tuples.Pair;

import java.util.Arrays;
import java.util.Optional;

/** The particle object class that represents a 2D bézier curve. It is a bit more
 * advanced than the ParticleLine due to its curvature and flexibility in terms of
 * its shape. The bézier curve can also be linear
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleBezierCurve extends ParticleObject {
    protected BezierCurve[] endpoints;
    protected int[] amount;

    private DrawInterceptor<ParticleBezierCurve, CommonDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleBezierCurve, CommonDrawData> beforeDraw = DrawInterceptor.identity();

    public enum CommonDrawData {BEZIER_CURVE, AMOUNT}

    /** Constructor for the particle bézier curve which is a curve. It accepts as parameters
     * the particle effect to use, the bézier curves, the amount per bézier curve and the rotation.
     * Although the pivot is the starting position of the bézier curves and not the center, there is
     * also a simplified constructor for no rotation
     *
     * @param particleEffect The particle effect to use
     * @param curves The Bézier curves to use
     * @param amount The number of particles
     *
     * @see ParticleBezierCurve#ParticleBezierCurve(ParticleEffect, BezierCurve[], int[])
    */
    public ParticleBezierCurve(ParticleEffect particleEffect, BezierCurve[] curves, int[] amount, Vector3f rotation) {
        super(particleEffect);
        this.setPair(amount, curves);
        this.setRotation(rotation);
    }

    /** Constructor for the particle bézier curve which is a bézier curve. It accepts as parameters
     * the particle effect to use, the bézier curves and the amount per bézier curves. There is a more
     * complex constructor for rotation
     *
     * @param particleEffect The particle effect to use
     * @param curves The Bézier curves to use
     * @param amount The number of particles
     *
     * @see ParticleBezierCurve#ParticleBezierCurve(ParticleEffect, BezierCurve[], int[], Vector3f)
     */
    public ParticleBezierCurve(ParticleEffect particleEffect, BezierCurve[] curves, int[] amount) {
        this(particleEffect, curves, amount, new Vector3f());
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param curve The particle object to copy from
    */
    public ParticleBezierCurve(ParticleBezierCurve curve) {
        super(curve);
        this.endpoints = curve.endpoints;
        this.beforeDraw = curve.beforeDraw;
        this.amount = curve.amount;
        this.afterDraw = curve.afterDraw;
    }

    /** Sets the bézier curves to iterate over
     *
     * @param newEndpoints The new bézier curves
     * @return The previous starting point
    */
    public BezierCurve[] setBezierEndpoints(BezierCurve[] newEndpoints) {
        if (newEndpoints.length != this.amount.length) {
            throw new IllegalArgumentException("The endpoint's length has to match with the amount's length");
        }
        BezierCurve[] prevEndpoints = this.endpoints;
        this.endpoints = newEndpoints;
        return prevEndpoints;
    }

    /** Sets the amount to a specific value. Returns -1 if there are different values otherwise
     * it returns the value that is present on the integer list(a.k.a. the constant)
     *
     * @param amount The new particle
     * @return The constant amount (if there isn't any return -1)
     */
    @Override
    public int setAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The amount is below or equal to 0");
        }
        int[] prevAmount = this.amount;
        int[] intArray = new int[this.endpoints.length];
        Arrays.fill(intArray, amount);
        this.amount = intArray;
        int[] dummyArray = new int[this.endpoints.length];
        int firstElement = prevAmount[0];
        Arrays.fill(dummyArray, firstElement);
        return Arrays.equals(dummyArray, prevAmount) ? firstElement : -1;
    }

    /** Sets the amounts to a new value and returns the previous one
     *
     * @param amount The new amounts value
     * @return The previous amounts value
     */
    public int[] setAmount(int[] amount) {
        if (amount.length != this.endpoints.length) {
            throw new IllegalArgumentException("The amount length has to match with the endpoint's length");
        }
        for (int i : amount) {
            if (i <= 0) throw new IllegalArgumentException("One of the amount is set below or equal to 0");
        }
        int[] prevAmount = this.amount;
        this.amount = amount;
        return prevAmount;
    }

    /** Sets both the Bézier curves and the amounts to new arrays and returns them
     *
     * @param amount The amounts
     * @param curves The curves
     * @return The pair of the previous used values
     */
    public Pair<int[], BezierCurve[]> setPair(int[] amount, BezierCurve[] curves) {
        if (amount.length != curves.length) {
            throw new IllegalArgumentException("The amount length has to match with the endpoint's length");
        }
        for (int i : amount) {
            if (i <= 0) throw new IllegalArgumentException("One of the amount is set below or equal to 0");
        }
        int[] prevAmount = this.amount;
        this.amount = amount;
        BezierCurve[] prevEndpoints = this.endpoints;
        this.endpoints = curves;
        return new Pair<>(prevAmount, prevEndpoints);
    }

    /** Gets the CONSTANT amount present in the array and returns it. If the
     *  array has different values, then it returns -1
     *
     * @return The constant value
     */
    @Override
    public int getAmount() {
        int[] dummyArray = new int[this.endpoints.length];
        int firstElement = this.amount[0];
        Arrays.fill(dummyArray, firstElement);
        return Arrays.equals(dummyArray, this.amount) ? firstElement : -1;
    }

    /** Gets the ENTIRE amount array and returns it
     *
     * @return the amount array
     */
    public int[] getAmounts() {
        return this.amount;
    }

    /** Gets the bézier endpoints and returns them
     *
     * @return The bézier endpoints
     */
    public BezierCurve[] getEndpoints() {
        return this.endpoints;
    }

    @Override
    public void draw(ApelRenderer renderer, int step, Vector3f drawPos) {
        int index = 0;
        for (BezierCurve endpoint : this.endpoints) {
            int amountForCurve = this.amount[index];
            float interval = 1.0f / amountForCurve;
            InterceptData<CommonDrawData> interceptData =
                    this.doBeforeDraw(renderer.getWorld(), endpoint, amountForCurve, step);
            endpoint = (BezierCurve) interceptData.getMetadata(CommonDrawData.BEZIER_CURVE);
            amountForCurve = (int) interceptData.getMetadata(CommonDrawData.AMOUNT);
            for (int i = 0; i < amountForCurve; i++) {
                Vector3f pos = endpoint.compute(interval * i);
                this.rigidTransformation(renderer, this.rotation, this.offset, pos);
                this.drawParticle(renderer, step, pos);
            }
            this.doAfterDraw(renderer.getWorld(), endpoint, amountForCurve, step);
        }
        this.endDraw(renderer, step, drawPos);
    }

    /** Sets the interceptor to run after drawing the bézier curve.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleLine
     * instance.
     *
     * @param afterDraw the new interceptor to execute after drawing the bézier curve
     */
    public void setAfterDraw(DrawInterceptor<ParticleBezierCurve, CommonDrawData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, BezierCurve curve, int amount, int step) {
        InterceptData<CommonDrawData> interceptData = new InterceptData<>(world, null, step, CommonDrawData.class);
        interceptData.addMetadata(CommonDrawData.BEZIER_CURVE, curve);
        interceptData.addMetadata(CommonDrawData.AMOUNT, amount);
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run before drawing the bézier curve.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleLine
     * instance.
     *
     * @param beforeDraw the new interceptor to execute before drawing the bézier curve
     */
    public void setBeforeDraw(DrawInterceptor<ParticleBezierCurve, CommonDrawData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private InterceptData<CommonDrawData> doBeforeDraw(ServerWorld world, BezierCurve curve, int amount, int step) {
        InterceptData<CommonDrawData> interceptData = new InterceptData<>(world, null, step, CommonDrawData.class);
        interceptData.addMetadata(CommonDrawData.BEZIER_CURVE, curve);
        interceptData.addMetadata(CommonDrawData.AMOUNT, amount);
        this.beforeDraw.apply(interceptData, this);
        return interceptData;
    }
}
