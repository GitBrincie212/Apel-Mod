package net.mcbrincie.apel.lib.easing;


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

    protected float interpolate(float a, float b, float t) {
        return ((1 - t) * a) + (t * b);
    }
}
