package net.mcbrincie.apel.lib.easing.shaped;


import net.mcbrincie.apel.lib.easing.EaseType;
import net.mcbrincie.apel.lib.easing.StatefulEasingCurve;

/** This is the CircEasingCurve, which interpolates between the start and end circularly.
 * You can manipulate the shape of the circular curve by specifying an easing type
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class CircEasingCurve<T> extends StatefulEasingCurve<T> {
    public CircEasingCurve(T start, T end, EaseType easeType) {
        super(start, end, easeType);
    }
    public CircEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor)  {
        super(start, end, easeType, easingProgressFactor);
    }

    protected float interpolate(float t) {
        return (float) switch (this.easeType) {
            case EASE_IN -> 1 - Math.sqrt(1 - Math.pow(t, 2));
            case EASE_OUT -> Math.sqrt(1 - Math.pow(t - 1, 2));
            case EASE_IN_OUT -> (
                t < 0.5
                    ? (1 - Math.sqrt(1 - Math.pow(2 * t, 2)))
                    : (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1)
            ) / 2;
        };
    }
}
