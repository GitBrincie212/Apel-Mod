package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.lib.animators.PathAnimatorBase;

public class ComputedEasingPA extends ComputedEasings {
    public ComputedEasingPA(PathAnimatorBase<?> pathAnimator, float currStep, float numberOfSteps) {
        super(currStep, numberOfSteps);
    }
}