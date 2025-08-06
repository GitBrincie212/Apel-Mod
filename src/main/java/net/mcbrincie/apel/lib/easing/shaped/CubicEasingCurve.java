package net.mcbrincie.apel.lib.easing.shaped;


import net.mcbrincie.apel.lib.easing.EaseType;
import net.mcbrincie.apel.lib.easing.StatefulEasingCurve;

/** This is the CubicEasingCurve, which interpolates between the start and end cubically.
 * You can manipulate the shape of the cubic curve by specifying an easing type
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class CubicEasingCurve<T> extends StatefulEasingCurve<T> {
    public CubicEasingCurve(T start, T end, EaseType easeType) {
        super(start, end, easeType);
    }
    public CubicEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor)  {
        super(start, end, easeType, easingProgressFactor);
    }

    protected float interpolate(float t) {
        return switch (this.easeType) {
            case EASE_IN -> t * t * t;
            case EASE_OUT -> 1 - ((1 - t) * (1 - t) * (1 - t));
            case EASE_IN_OUT -> t < 0.5 ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
        };
    }
}
