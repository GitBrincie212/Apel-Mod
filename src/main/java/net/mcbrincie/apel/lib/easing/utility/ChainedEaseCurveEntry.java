package net.mcbrincie.apel.lib.easing.utility;

import net.mcbrincie.apel.lib.easing.EasingCurve;

public record ChainedEaseCurveEntry<T>(EasingCurve<T> easingCurve, float end) {
}
