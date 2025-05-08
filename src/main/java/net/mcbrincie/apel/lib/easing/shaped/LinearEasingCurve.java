package net.mcbrincie.apel.lib.easing.shaped;

import net.mcbrincie.apel.lib.easing.EasingCurve;

/** This is the LinearEasingCurve, which interpolates between the start and end linearly (hence lerp).
 * Due to its nature of being a line, it has no Ease Type since it doesn't change the computations which
 * makes it a bit simpler to use than the rest easing curves
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class LinearEasingCurve<T> extends EasingCurve<T> {
    public LinearEasingCurve(T start, T end) {
        super(start, end);
    }
    public LinearEasingCurve(T start, T end, float easingProgressFactor)  {
        super(start, end, easingProgressFactor);
    }

    @Override
    protected float interpolate(float t) {
        return t;
    }
}
