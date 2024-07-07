package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** The particle object class that represents a series of 3D Bézier curves. It is a bit more
 * advanced than the {@code ParticleLine} due to its curvature and flexibility in terms of
 * its shape. Bézier curves can be linear, but it is recommended to use {@code ParticleLine} if a line is desired.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleBezierCurve extends ParticleObject {
    protected List<BezierCurve> bezierCurves;
    protected List<Integer> amounts;

    private DrawInterceptor<ParticleBezierCurve, CommonDrawData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleBezierCurve, CommonDrawData> beforeDraw = DrawInterceptor.identity();

    public enum CommonDrawData {BEZIER_CURVE, AMOUNT}

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleBezierCurve(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount);
        this.setBezierCurves(builder.bezierCurves, builder.amounts);
        this.setBeforeDraw(builder.beforeDraw);
        this.setAfterDraw(builder.afterDraw);
    }

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
    */

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
     */

    /** The copy constructor for a specific particle object. It makes shallow copies of all
     * properties, including the interceptors the particle object has.
     *
     * @param curve The particle object to copy from
    */
    public ParticleBezierCurve(ParticleBezierCurve curve) {
        super(curve);
        this.bezierCurves = new ArrayList<>(curve.bezierCurves);
        this.amounts = new ArrayList<>(curve.amounts);
        this.beforeDraw = curve.beforeDraw;
        this.afterDraw = curve.afterDraw;
    }

    /**
     * Gets the Bézier curves.
     *
     * @return The Bézier curves
     */
    public List<BezierCurve> getBezierCurves() {
        return this.bezierCurves;
    }

    /**
     * Sets the Bézier curves.  This is expected to be used after construction, so the number of curves provided here
     * must match the current number of amount values.
     *
     * @param bezierCurves The new Bézier curves
     * @return The previous Bézier curves
     *
     * @see #setBezierCurves(List, List)
    */
    public List<BezierCurve> setBezierCurves(List<BezierCurve> bezierCurves) {
        if (bezierCurves.size() != this.amounts.size()) {
            throw new IllegalArgumentException("The number of Bézier curves must match the number of amounts");
        }
        List<BezierCurve> prevEndpoints = this.bezierCurves;
        this.bezierCurves = bezierCurves;
        return prevEndpoints;
    }

    /**
     * Sets both the Bézier curves and the amounts.  This is expected to be used after construction, and is the
     * proper method to change the number of curves, since the individual setters for curves and amounts require a
     * List with the same number of elements as the other field currently has.  In this method, the arrays must be of
     * equal size, and the amounts must be positive.
     * <p>
     * This implementation is also used by the constructor, so subclasses must not change the requirements described.
     *
     * @param curves The curves
     * @param amounts The amounts
     * @return The pair of the previous used values
     *
     * @see #setBezierCurves(List)
     */
    public Pair<List<Integer>, List<BezierCurve>> setBezierCurves(List<BezierCurve> curves, List<Integer> amounts) {
        if (amounts.size() != curves.size()) {
            throw new IllegalArgumentException("The number of curves and number of amounts must be equal");
        }
        for (int i : amounts) {
            if (i <= 0) throw new IllegalArgumentException("One of the amounts is set below or equal to 0");
        }
        List<Integer> prevAmounts = this.amounts;
        this.amounts = amounts;
        List<BezierCurve> prevEndpoints = this.bezierCurves;
        this.bezierCurves = curves;
        return new Pair<>(prevAmounts, prevEndpoints);
    }

    /**
     * Unsupported in this class, as each curve may have a different number of particles.
     */
    @Override
    @Deprecated
    public int getAmount() {
        throw new UnsupportedOperationException();
    }

    /** Gets the array of particle amounts and returns it
     *
     * @return the amount array
     */
    public List<Integer> getAmounts() {
        return this.amounts;
    }

    /**
     * Sets the particle amount for every curve to the given value. Returns -1.
     *
     * @param amount The new particle count to be applied to every curve
     * @return The constant amount (if there isn't any return -1)
     */
    public List<Integer> setAmounts(int amount) {
        List<Integer> prevAmounts = this.amounts;
        if (amount <= 0) {
            throw new IllegalArgumentException("The amount is below or equal to 0");
        }
        this.amounts = new ArrayList<>(this.bezierCurves.size());
        Collections.fill(this.amounts, amount);
        return prevAmounts;
    }

    /** Sets the amounts to a new value and returns the previous one.  The array.size() must match the number of
     * Bézier curves, and each entry in the array must be positive.
     *
     * @param amounts The new amounts
     * @return The previous amounts
     */
    public List<Integer> setAmounts(List<Integer> amounts) {
        if (amounts.size() != this.bezierCurves.size()) {
            throw new IllegalArgumentException("The amount.size() has to match with the endpoint's.size()");
        }
        for (int i : amounts) {
            if (i <= 0) throw new IllegalArgumentException("One of the amounts is set below or equal to 0");
        }
        List<Integer> prevAmounts = this.amounts;
        this.amounts = amounts;
        return prevAmounts;
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        int index = 0;
        // Compute total offset from origin
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);

        for (BezierCurve bezierCurve : this.bezierCurves) {
            int amountForCurve = this.amounts.get(index);
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

    public static class Builder<B extends ParticleBezierCurve.Builder<B>> extends ParticleObject.Builder<B> {
        protected List<BezierCurve> bezierCurves = new ArrayList<>();
        protected List<Integer> amounts = new ArrayList<>();
        protected DrawInterceptor<ParticleBezierCurve, ParticleBezierCurve.CommonDrawData> afterDraw;
        protected DrawInterceptor<ParticleBezierCurve, ParticleBezierCurve.CommonDrawData> beforeDraw;

        private Builder() {}

        /**
         * Curves will be added in order, blah blah blah
         */
        public B bezierCurve(BezierCurve bezierCurve) {
            this.bezierCurves.add(bezierCurve);
            return self();
        }

        public B bezierCurves(List<BezierCurve> bezierCurves) {
            this.bezierCurves.addAll(bezierCurves);
            return self();
        }

        public B amounts(int amount) {
            this.amounts.add(amount);
            return self();
        }

        public B amounts(List<Integer> amounts) {
            this.amounts.addAll(amounts);
            return self();
        }

        public B beforeDraw(DrawInterceptor<ParticleBezierCurve, ParticleBezierCurve.CommonDrawData> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        public B afterDraw(DrawInterceptor<ParticleBezierCurve, ParticleBezierCurve.CommonDrawData> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        @Override
        public ParticleBezierCurve build() {
            return new ParticleBezierCurve(this);
        }
    }
}
