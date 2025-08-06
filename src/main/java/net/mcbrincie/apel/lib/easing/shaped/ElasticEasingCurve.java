package net.mcbrincie.apel.lib.easing.shaped;


import net.mcbrincie.apel.lib.easing.EaseType;
import net.mcbrincie.apel.lib.easing.StatefulEasingCurve;

/** This is the BackEasingCurve, which interpolates between the start and end circularly.
 * You can manipulate the shape of the circular curve by specifying an easing type
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public class ElasticEasingCurve<T> extends StatefulEasingCurve<T> {
    public ElasticEasingCurve(T start, T end, EaseType easeType) {
        super(start, end, easeType);
    }
    public ElasticEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor)  {
        super(start, end, easeType, easingProgressFactor);
    }

    protected float interpolate(float t) {
        float c4 = (float) Math.TAU / 3f;
        float c5 = (float) Math.TAU / 4.5f;

        return (float) switch (this.easeType) {
            case EASE_IN -> (
                    t == 0
                        ? 0
                        : t == 1
                            ? 1
                            : -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * c4)
            );
            case EASE_OUT -> (
                    t == 0
                        ? 0
                        : t == 1
                            ? 1
                            : Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1
            );
            case EASE_IN_OUT -> (
                t == 0
                    ? 0
                    : t == 1
                        ? 1
                        : t < 0.5
                            ? -(Math.pow(2, 20 * t - 10) * Math.sin((20 * t - 11.125) * c5)) / 2
                            : (Math.pow(2, -20 * t + 10) * Math.sin((20 * t - 11.125) * c5)) / 2 + 1
            );
        };
    }
}
