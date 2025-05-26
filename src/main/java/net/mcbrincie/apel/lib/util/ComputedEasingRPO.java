package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.objects.RenderableParticleObject;

public class ComputedEasingRPO extends ComputedEasingPO {
    public final int computedAmount;

    public ComputedEasingRPO(RenderableParticleObject<?> particleObject, float currStep, float numberOfSteps) {
        super(particleObject, currStep, numberOfSteps);
        this.computedAmount = particleObject.getAmount().getValue(this.tVal);
        if (this.computedAmount <= 0) {
            throw new IllegalArgumentException("Amount of particles has to be above 0");
        }
    }

    public <T> ComputedEasingRPO addComputedField(String name, EasingCurve<T> value) {
        this.computedFields.put(name, value.getValue(this.tVal));
        return this;
    }
}
