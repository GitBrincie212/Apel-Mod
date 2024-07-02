package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;
import oshi.util.tuples.Pair;

import java.util.Arrays;
import java.util.Optional;

/** The particle object class that represents a series of 3D Bézier curves. It is a bit more
 * advanced than the {@code ParticleLine} due to its curvature and flexibility in terms of
 * its shape. Bézier curves can be linear, but it is recommended to use {@code ParticleLine} if a line is desired.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleBezierCurve extends ParticleObject {
    protected BezierCurve[] bezierCurves;
    protected int[] amounts;

    private DrawInterceptor<ParticleBezierCurve, CommonDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleBezierCurve, CommonDrawData> beforeDraw = DrawInterceptor.identity();

    public enum CommonDrawData {BEZIER_CURVE, AMOUNT}

    /** Constructor for the particle Bézier curve. It accepts as parameters
     * the particle effect to use, the Bézier curves, the amount of particles per Bézier curve, and the rotation.
     * The pivot point for rotation is the {@code drawPos} parameter of
     * {@link #draw(ApelServerRenderer, int, Vector3f)}, and the points in the Bézier curves are relative to that
     * point.  There is also a simplified constructor for no rotation.
     *
     * <p>This implementation calls setters for rotation and pairs of curves/amounts so checks are performed to
     * ensure valid values are accepted for each property.  Subclasses should take care not to violate these lest
     * they risk undefined behavior.
     *
     * @param particleEffect The particle effect to use
     * @param curves The Bézier curves to use
     * @param amounts The number of particles for each Bézier curve
     *
     * @see ParticleBezierCurve#ParticleBezierCurve(ParticleEffect, BezierCurve[], int[])
    */
    public ParticleBezierCurve(ParticleEffect particleEffect, BezierCurve[] curves, int[] amounts, Vector3f rotation) {
        super(particleEffect, rotation);
        this.setPairs(curves, amounts);
    }

    /** Constructor for the particle bézier curve which is a bézier curve. It accepts as parameters
     * the particle effect to use, the bézier curves and the amount per bézier curves. There is a more
     * complex constructor for rotation.
     *
     * <p>This implementation calls setters for rotation and pairs of curves/amounts so checks are performed to
     * ensure valid values are accepted for each property.  Subclasses should take care not to violate these lest
     * they risk undefined behavior.
     *
     * @param particleEffect The particle effect to use
     * @param curves The Bézier curves to use
     * @param amounts The number of particles
     *
     * @see ParticleBezierCurve#ParticleBezierCurve(ParticleEffect, BezierCurve[], int[], Vector3f)
     */
    public ParticleBezierCurve(ParticleEffect particleEffect, BezierCurve[] curves, int[] amounts) {
        this(particleEffect, curves, amounts, new Vector3f());
    }

    /** The copy constructor for a specific particle object. It makes shallow copies of all
     * properties, including the interceptors the particle object has.
     *
     * @param curve The particle object to copy from
    */
    public ParticleBezierCurve(ParticleBezierCurve curve) {
        super(curve);
        this.bezierCurves = curve.bezierCurves.clone();
        this.amounts = curve.amounts.clone();
        this.beforeDraw = curve.beforeDraw;
        this.afterDraw = curve.afterDraw;
    }

    /** Gets the bézier curves and returns them
     *
     * @return The bézier curves
     */
    public BezierCurve[] getBezierCurves() {
        return this.bezierCurves;
    }

    /** Sets the bézier curves to iterate over
     *
     * @param newEndpoints The new bézier curves
     * @return The previous starting point
    */
    public BezierCurve[] setBezierCurves(BezierCurve[] newEndpoints) {
        if (newEndpoints.length != this.amounts.length) {
            throw new IllegalArgumentException("The endpoint's length has to match with the amount's length");
        }
        BezierCurve[] prevEndpoints = this.bezierCurves;
        this.bezierCurves = newEndpoints;
        return prevEndpoints;
    }

    /** Gets the amount of particles that make up each curve.  If all curves have the same number of particles, it
     * returns that number.  If any curve has a different number of particles, then it returns -1.
     *
     * @return The common value or -1 if any curve has a distinct number of particles
     */
    @Override
    public int getAmount() {
        int[] dummyArray = new int[this.bezierCurves.length];
        int firstElement = this.amounts[0];
        Arrays.fill(dummyArray, firstElement);
        return Arrays.equals(dummyArray, this.amounts) ? firstElement : -1;
    }

    /** Gets the array of particle amounts and returns it
     *
     * @return the amount array
     */
    public int[] getAmounts() {
        return this.amounts;
    }

    /** Sets the particle amount for every curve to the given value. Returns -1 if there are different values otherwise
     * it returns the value shared by all curves.
     *
     * @param amount The new particle count to be applied to every curve
     * @return The constant amount (if there isn't any return -1)
     */
    @Override
    public int setAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The amount is below or equal to 0");
        }
        int[] prevAmount = this.amounts;
        int[] intArray = new int[this.bezierCurves.length];
        Arrays.fill(intArray, amount);
        this.amounts = intArray;
        int[] dummyArray = new int[this.bezierCurves.length];
        int firstElement = prevAmount[0];
        Arrays.fill(dummyArray, firstElement);
        return Arrays.equals(dummyArray, prevAmount) ? firstElement : -1;
    }

    /** Sets the amounts to a new value and returns the previous one.  The array length must match the number of
     * Bézier curves, and each entry in the array must be positive.
     *
     * @param amount The new amounts
     * @return The previous amounts
     */
    public int[] setAmount(int[] amount) {
        if (amount.length != this.bezierCurves.length) {
            throw new IllegalArgumentException("The amount length has to match with the endpoint's length");
        }
        for (int i : amount) {
            if (i <= 0) throw new IllegalArgumentException("One of the amount is set below or equal to 0");
        }
        int[] prevAmount = this.amounts;
        this.amounts = amount;
        return prevAmount;
    }

    /**
     * Sets both the Bézier curves and the amounts to new arrays and returns them.  The arrays must be of equal length,
     * and the amounts must be positive.
     *
     * @param curves The curves
     * @param amounts The amounts
     * @return The pair of the previous used values
     */
    public Pair<int[], BezierCurve[]> setPairs(BezierCurve[] curves, int[] amounts) {
        if (amounts.length != curves.length) {
            throw new IllegalArgumentException("The number of curves and number of amounts must be equal");
        }
        for (int i : amounts) {
            if (i <= 0) throw new IllegalArgumentException("One of the amounts is set below or equal to 0");
        }
        int[] prevAmount = this.amounts;
        this.amounts = amounts;
        BezierCurve[] prevEndpoints = this.bezierCurves;
        this.bezierCurves = curves;
        return new Pair<>(prevAmount, prevEndpoints);
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        int index = 0;
        // Compute total offset from origin
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);

        for (BezierCurve bezierCurve : this.bezierCurves) {
            int amountForCurve = this.amounts[index];
            InterceptData<CommonDrawData> interceptData =
                    this.doBeforeDraw(renderer.getServerWorld(), bezierCurve, amountForCurve, step);
            bezierCurve = interceptData.getMetadata(CommonDrawData.BEZIER_CURVE, bezierCurve);
            amountForCurve = interceptData.getMetadata(CommonDrawData.AMOUNT, amountForCurve);

            renderer.drawBezier(this.particleEffect, step, drawPos, bezierCurve, this.rotation, amountForCurve);
            this.doAfterDraw(renderer.getServerWorld(), bezierCurve, amountForCurve, step);
        }
        this.endDraw(renderer, step, drawPos);
    }

    /** Sets the interceptor to run after drawing the bézier curve.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleBezierCurve
     * instance.  Metadata will include the individual Bézier curve object and its amount of particles.
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
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleBezierCurve
     * instance.  Metadata will include the individual Bézier curve object and its amount of particles.
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
