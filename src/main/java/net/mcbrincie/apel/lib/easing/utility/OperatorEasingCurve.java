package net.mcbrincie.apel.lib.easing.utility;

import net.mcbrincie.apel.lib.easing.EasingCurve;

/** This is the OperatorEasingCurve, which acts as a utility for operating on multiple curves together.
 * Either be adding, subtracting, multiplying or even dividing. OperatorEasingCurves can also operate
 * on themselves, creating a tree of operations. Due to the fact that its utility, there is no
 * exact shape since the individual curves make it up along with the operation, so there is no
 * Ease Type. You supply only the operation to perform and the easing curves
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class OperatorEasingCurve<T> extends EasingCurve<T> {
    protected EasingCurve<T>[] easingCurves;
    protected OperatorEasingOperation easingOperation;

    @SafeVarargs
    public OperatorEasingCurve(OperatorEasingOperation easingOperation, EasingCurve<T>... easingCurves) {
        super(null, null);
        this.easingCurves = easingCurves;
        this.easingOperation = easingOperation;
    }

    @Override
    public float[] compute(float[] t) {
        float[] tCopy = new float[t.length];
        for (int i = 0; i < t.length; i++) {
            float val = 0;
            float t2 = computeValueT(t[i]);
            for (EasingCurve<T> easingCurve : this.easingCurves) {
                float computedValT = easingCurve.compute(t2);
                val = switch (easingOperation) {
                    case ADD -> computedValT + val;
                    case SUBTRACT -> computedValT - val;
                    case MULTIPLY -> computedValT * val;
                    case DIVIDE -> computedValT / val;
                };
            }
            tCopy[i] = val;
        }
        return tCopy;
    }

    @Override
    protected float interpolate(float t) {
        return -1;
    }
}
