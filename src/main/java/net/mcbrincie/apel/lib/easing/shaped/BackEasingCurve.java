package net.mcbrincie.apel.lib.easing.shaped;


import net.mcbrincie.apel.lib.easing.EaseType;
import net.mcbrincie.apel.lib.easing.StatefulEasingCurve;

/** This is the BackEasingCurve, which interpolates between the start and end using a "back"
 * easing function which creates an overshooting motion similar to pulling
 * and releasing a rubber band. You can manipulate the shape of the circular curve by specifying an easing type
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class BackEasingCurve<T> extends StatefulEasingCurve<T> {
    public BackEasingCurve(T start, T end, EaseType easeType) {
        super(start, end, easeType);
    }
    public BackEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor)  {
        super(start, end, easeType, easingProgressFactor);
    }

    protected float interpolate(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        float c2 = c1 * 1.525f;

        return (float) switch (this.easeType) {
            case EASE_IN -> c3 * t * t * t - c1 * t * t;
            case EASE_OUT -> 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
            case EASE_IN_OUT -> (
                t < 0.5
                ? (Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2))
                : (Math.pow(2 * t - 2, 2) * ((c2 + 1) * (t * 2 - 2) + c2) + 2)
            ) / 2;
        };
    }
}
