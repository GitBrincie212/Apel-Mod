package net.mcbrincie.apel.lib.easing.shaped;


import net.mcbrincie.apel.lib.easing.EaseType;
import net.mcbrincie.apel.lib.easing.StatefulEasingCurve;

/** This is the ExpoEasingCurve, which interpolates between the start and end exponentially.
 * You can manipulate the shape of the exponential curve by specifying an easing type
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class ExpoEasingCurve<T> extends StatefulEasingCurve<T> {
    public ExpoEasingCurve(T start, T end, EaseType easeType) {
        super(start, end, easeType);
    }
    public ExpoEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor)  {
        super(start, end, easeType, easingProgressFactor);
    }

    protected float interpolate(float t) {
        return (float) switch (this.easeType) {
            case EASE_IN -> t == 0 ? 0 : Math.pow(2, 10 * t - 10);
            case EASE_OUT -> t == 1 ? 1 : 1 - Math.pow(2, -10 * t);
            case EASE_IN_OUT -> (
                t == 0
                    ? 0
                    : t == 1
                    ? 1
                    : t < 0.5 ? Math.pow(2, 20 * t - 10) / 2
                    : (2 - Math.pow(2, -20 * t + 10)) / 2
            );
        };
    }
}
