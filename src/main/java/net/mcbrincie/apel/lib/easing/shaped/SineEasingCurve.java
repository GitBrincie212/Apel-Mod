package net.mcbrincie.apel.lib.easing.shaped;


import net.mcbrincie.apel.lib.easing.EaseType;
import net.mcbrincie.apel.lib.easing.StatefulEasingCurve;

/** This is the SineEasingCurve, which interpolates between the start and end using a sine wave.
 * You can manipulate the shape of the quadratic curve by specifying an easing type
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class SineEasingCurve<T> extends StatefulEasingCurve<T> {
    public SineEasingCurve(T start, T end, EaseType easeType) {
        super(start, end, easeType);
    }
    public SineEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor)  {
        super(start, end, easeType, easingProgressFactor);
    }

    protected float interpolate(float t) {
        return (float) switch (this.easeType) {
            case EASE_IN -> 1f - Math.cos((t * Math.PI) / 2f);
            case EASE_OUT -> Math.sin((t * Math.PI) / 2f);
            case EASE_IN_OUT ->  -(Math.cos(Math.PI * t) - 1) / 2f;
        };
    }
}
