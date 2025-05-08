package net.mcbrincie.apel.lib.easing.utility;

import net.mcbrincie.apel.lib.easing.EasingCurve;

import java.util.function.BiFunction;

/** This is the CompositeEasingCurve, which acts as a utility for operating on multiple curves together.
 * Specifically, each t value including the starting one is fed to the EasingCurve then it continues with the next
 * and repeats til it passes through all curves. Due to the fact that its utility, there is no exact shape
 * since the individual curves make it up along with the operation, so there is no Ease Type.
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class CompositeEasingCurve<T> extends EasingCurve<T> {
    protected EasingCurve<T>[] easingCurves;

    @SafeVarargs
    public CompositeEasingCurve(EasingCurve<T>... easingCurves) {
        super(null, null);
        this.easingCurves = easingCurves;
    }

    @Override
    public float[] compute(float[] t) {
        float[] tCopy = new float[t.length];
        for (int i = 0; i < t.length; i++) {
            float val = 0;
            float t2 = computeValueT(t[i]);
            for (EasingCurve<T> easingCurve : this.easingCurves) {
                t2 = easingCurve.compute(t2);
            }
            tCopy[i] = t2;
        }
        return tCopy;
    }

    @Override
    protected float interpolate(float t) {
        return -1;
    }
}
