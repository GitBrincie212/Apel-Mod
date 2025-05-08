package net.mcbrincie.apel.lib.easing.shaped;


import net.mcbrincie.apel.lib.easing.EaseType;
import net.mcbrincie.apel.lib.easing.StatefulEasingCurve;

/** This is the QuadEasingCurve, which interpolates between the start and end quadratically.
 * You can manipulate the shape of the quadratic curve by specifying an easing type
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class BounceEasingCurve<T> extends StatefulEasingCurve<T> {
    public BounceEasingCurve(T start, T end, EaseType easeType) {
        super(start, end, easeType);
    }
    public BounceEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor)  {
        super(start, end, easeType, easingProgressFactor);
    }

    private float easeOutBounce(float t) {
        float n1 = 7.5625f;
        float d1 = 2.75f;

        if (t < 1 / d1) {
            return n1 * t * t;
        } else if (t < 2 / d1) {
            return n1 * (t -= 1.5f / d1) * t + 0.75f;
        } else if (t < 2.5 / d1) {
            return n1 * (t -= 2.25f / d1) * t + 0.9375f;
        } else {
            return n1 * (t -= 2.625f / d1) * t + 0.984375f;
        }
    }

    protected float interpolate(float t) {
        return switch (this.easeType) {
            case EASE_IN -> 1 - this.easeOutBounce(1 - t);
            case EASE_OUT -> this.easeOutBounce(t);
            case EASE_IN_OUT -> (
                t < 0.5
                    ? (1 - easeOutBounce(1 - 2 * t))
                    : (1 + easeOutBounce(2 * t - 1))
            ) / 2;
        };
    }
}
