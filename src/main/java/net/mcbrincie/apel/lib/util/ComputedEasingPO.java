package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.lib.objects.ParticleObject;
import org.joml.Vector3f;

public class ComputedEasingPO extends ComputedEasings<ComputedEasingPO> {
    public final Vector3f computedRotation;
    public final Vector3f computedOffset;

    public ComputedEasingPO(ParticleObject<?> particleObject, float currStep, float numberOfSteps) {
        super(currStep, numberOfSteps);
        this.computedRotation = ApelUtils.normalizeRotation(particleObject.getRotation().getValue(this.tVal));
        this.computedOffset = particleObject.getOffset().getValue(this.tVal);
    }
}
