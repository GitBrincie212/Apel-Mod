package net.mcbrincie.apel.lib.easing.utility;

import net.mcbrincie.apel.lib.easing.EasingCurve;

/** This is the ChainedEasingCurve, which acts as a utility for combining multiple easing curves
 * as one thing, this allows for intricate designs of easing curves making it possible to interpolate
 * between multiple values. Due to the fact that its utility, there is no exact shape since
 * the individual curves make it up along with the operation, so there is no Ease Type.
 * You supply only supply the easing curves and their timing offsets
 * <p>
 * <b>Note:</b> This easing curve will only use the first field of a vector (and that is the x value). So
 * supplying different t values on the vector will do nothing
 * </p>
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class ChainedEasingCurve<T> extends EasingCurve<T> {
    protected ChainedEaseCurveEntry<T>[] easingCurves;

    @SafeVarargs
    public ChainedEasingCurve(ChainedEaseCurveEntry<T>... easingCurves) {
        super(null, null);
        this.easingCurves = easingCurves;
    }

    @Override
    public float[] compute(float[] t) {
        float[] tCopy = new float[t.length];
        for (int i = 0; i < t.length; i++) {
            float t2 = computeValueT(t[i]);
            float prevEnd = 0;
            boolean hasComputed = false;
            for (ChainedEaseCurveEntry<T> chainedEaseCurveEntry : this.easingCurves) {
                if (t2 > prevEnd && t2 <= chainedEaseCurveEntry.end()) {
                    hasComputed = true;
                    tCopy[i] = chainedEaseCurveEntry.easingCurve().compute(t2);
                }
                prevEnd = chainedEaseCurveEntry.end();
            }
            if (!hasComputed) throw new RuntimeException("Could not find a easing curve to compute");
        }
        return tCopy;
    }

    @Override
    protected float interpolate(float t) {
        return -1;
    }
}
