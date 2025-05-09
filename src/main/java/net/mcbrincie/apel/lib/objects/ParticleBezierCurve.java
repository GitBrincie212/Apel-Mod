package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.mcbrincie.apel.lib.util.interceptor.Key;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import org.joml.Vector3f;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The particle object class that represents a series of 3D Bézier curves. It is a bit more
 * advanced than the {@code ParticleLine} due to its curvature and flexibility in terms of
 * its shape. Bézier curves can be linear, but it is recommended to use {@code ParticleLine} if a line is desired.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleBezierCurve extends ParticleObject<ParticleBezierCurve> {
    protected List<BezierCurve> bezierCurves;
    protected List<Integer> amounts;

    public static final Key<BezierCurve> BEZIER_CURVE = new Key<>("bezierCurve") {};
    public static final Key<Integer> AMOUNT = Key.integerKey("amount");

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleBezierCurve(Builder<?> builder) {
        super(
                builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
                builder.afterDraw
        );
        this.setBezierCurves(builder.bezierCurves, builder.amounts);
    }

    /** The copy constructor for a specific particle object. It makes shallow copies of all
     * properties, including the interceptors the particle object has.
     *
     * @param curve The particle object to copy from
    */
    public ParticleBezierCurve(ParticleBezierCurve curve) {
        super(curve);
        this.bezierCurves = new ArrayList<>(curve.bezierCurves);
        this.amounts = new ArrayList<>(curve.amounts);
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
     * Sets both the Bézier curves and the amounts.  This may be used after construction, and is the proper method to
     * change the number of curves, since the individual setters for curves and amounts require a List with the same
     * number of elements as the other field currently has.  In this method, the lists must be of equal size, and all
     * amounts must be positive.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param curves The curves
     * @param amounts The amounts
     * @return The pair of the previous used values
     *
     * @see #setBezierCurves(List)
     */
    public final Pair<List<Integer>, List<BezierCurve>> setBezierCurves(
            List<BezierCurve> curves, List<Integer> amounts
    ) {
        if (curves.size() != amounts.size()) {
            throw new IllegalArgumentException(
                    "The number of curves and number of amounts must be equal, but there are " + curves.size()
                    + " curves and " + amounts.size() + " amounts.");
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
     *
     * @see #getAmounts()
     */
    @Override
    @Deprecated
    public EasingCurve<Integer> getAmount() {
        throw new UnsupportedOperationException("Each curve may have a different amount; use getAmounts()");
    }

    /** Gets the array of particle amounts and returns it
     *
     * @return the amount array
     */
    public List<Integer> getAmounts() {
        return this.amounts;
    }

    /**
     * Sets the particle amount for every curve to the given value. Returns a list containing the previous amounts for
     * each curve.
     *
     * @param amount The new particle count to be applied to every curve
     * @return The previous amounts for each curve
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

    /**
     * Sets the particle amount for every curve based on the given list. The list's size must match the number of
     * Bézier curves, and each entry in the array must be positive. Returns a list containing the previous amounts for
     * each curve.
     *
     * @param amounts The new amounts
     * @return The previous amounts
     */
    public List<Integer> setAmounts(List<Integer> amounts) {
        if (amounts.size() != this.bezierCurves.size()) {
            throw new IllegalArgumentException(
                    "The list must contain " + this.bezierCurves.size() + " values; has " + amounts.size()
                    + " values.");
        }
        for (int i : amounts) {
            if (i <= 0) throw new IllegalArgumentException("All amounts must be positive");
        }
        List<Integer> prevAmount = this.amounts;
        this.amounts = amounts;
        return prevAmount;
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        // Compute total offset from origin
        ComputedEasingPO computedEasings = drawContext.getComputedEasings();
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasings.computedOffset);

        int curveCount = this.bezierCurves.size();
        for (int i = 0; i < curveCount; i++) {
            drawContext.addMetadata(BEZIER_CURVE, this.bezierCurves.get(i));
            drawContext.addMetadata(AMOUNT, this.amounts.get(i));
            this.beforeDraw.apply(drawContext, this);
            BezierCurve bezierCurve = drawContext.getMetadata(BEZIER_CURVE, this.bezierCurves.get(i));
            int amountForCurve = drawContext.getMetadata(AMOUNT, this.amounts.get(i));

            renderer.drawBezier(this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, bezierCurve,
                    computedEasings.computedRotation, amountForCurve
            );
        }
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleBezierCurve> {
        protected List<BezierCurve> bezierCurves = new ArrayList<>();
        protected List<Integer> amounts = new ArrayList<>();

        private Builder() {}

        /**
         * Adds a single Bézier curve to the particle object.  This method is cumulative, so it may be called
         * repeatedly to add multiple curves.
         */
        public B bezierCurve(BezierCurve bezierCurve) {
            this.bezierCurves.add(bezierCurve);
            return self();
        }

        /**
         * Adds multiple Bézier curves to the particle object.  This method is cumulative, so it may be called
         * repeatedly to add multiple lists of curves.
         */
        public B bezierCurves(List<BezierCurve> bezierCurves) {
            this.bezierCurves.addAll(bezierCurves);
            return self();
        }

        /**
         * Adds a single amount to the particle object.  This method is cumulative, so it may be called
         * repeatedly to add multiple amounts.
         */
        public B amounts(int amount) {
            this.amounts.add(amount);
            return self();
        }

        /**
         * Adds multiple amounts to the particle object.  This method is cumulative, so it may be called
         * repeatedly to add multiple lists of amounts.
         */
        public B amounts(List<Integer> amounts) {
            this.amounts.addAll(amounts);
            return self();
        }

        @Override
        public ParticleBezierCurve build() {
            return new ParticleBezierCurve(this);
        }
    }
}
