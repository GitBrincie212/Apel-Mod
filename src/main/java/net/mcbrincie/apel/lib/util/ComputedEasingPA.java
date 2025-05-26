package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.lib.animators.PathAnimatorBase;

public class ComputedEasingPA extends ComputedEasings<ComputedEasingPA> {
    public ComputedEasingPA(PathAnimatorBase<?> pathAnimator, float currStep, float numberOfSteps) {
        super(currStep, numberOfSteps);
    }
}