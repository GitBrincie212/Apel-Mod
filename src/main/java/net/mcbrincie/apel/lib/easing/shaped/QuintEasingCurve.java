package net.mcbrincie.apel.lib.easing.shaped;


import net.mcbrincie.apel.lib.easing.EaseType;
import net.mcbrincie.apel.lib.easing.StatefulEasingCurve;

/** This is the QuintEasingCurve, which interpolates between the start and end quintically.
 * You can manipulate the shape of the quintic curve by specifying an easing type
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class QuintEasingCurve<T> extends StatefulEasingCurve<T> {
    public QuintEasingCurve(T start, T end, EaseType easeType) {
        super(start, end, easeType);
    }
    public QuintEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor)  {
        super(start, end, easeType, easingProgressFactor);
    }


    protected float interpolate(float t) {
        return (float) switch (this.easeType) {
            case EASE_IN -> t * t * t * t * t;
            case EASE_OUT -> 1 - Math.pow(1 - t, 5);
            case EASE_IN_OUT -> t < 0.5 ? 16 * t * t * t * t * t : 1 - Math.pow(-2 * t + 2, 5) / 2;
        };
    }
}
