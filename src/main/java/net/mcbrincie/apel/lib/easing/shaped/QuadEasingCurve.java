package net.mcbrincie.apel.lib.easing.shaped;


import net.mcbrincie.apel.lib.easing.EaseType;
import net.mcbrincie.apel.lib.easing.StatefulEasingCurve;

/** This is the QuadEasingCurve, which interpolates between the start and end quadratically.
 * You can manipulate the shape of the quadratic curve by specifying an easing type
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class QuadEasingCurve<T> extends StatefulEasingCurve<T> {
    public QuadEasingCurve(T start, T end, EaseType easeType) {
        super(start, end, easeType);
    }
    public QuadEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor)  {
        super(start, end, easeType, easingProgressFactor);
    }

    protected float interpolate(float t) {
        return switch (this.easeType) {
            case EASE_IN -> t * t;
            case EASE_OUT -> 1 - ((1 - t) * (1 - t));
            case EASE_IN_OUT -> t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2f) / 2f;
        };
    }
}
