package net.mcbrincie.apel.lib.easing;


/** This is the ConstantEasingCurve, which acts as more of a wrapper and returns the same value regardless
 * of any t value picked. Due to its nature of being a horizontal line, it has no Ease Type since
 * it doesn't change the computations which makes it a bit simpler to use than the rest easing curves
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class ConstantEasingCurve<T> extends EasingCurve<T> {
    public ConstantEasingCurve(T value) {
        super(value, value);
    }

    protected float interpolate(float a, float b, float t) {
        return a;
    }
}
