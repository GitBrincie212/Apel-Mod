package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.util.math.ApelMath;
import org.joml.Vector3f;

public class ComputedEasingPO extends ComputedEasings {
    public final Vector3f computedRotation;
    public final Vector3f computedOffset;
    public final int computedAmount;

    public ComputedEasingPO(ParticleObject<?> particleObject, float currStep, float numberOfSteps) {
        super(currStep, numberOfSteps);
        this.computedAmount = particleObject.getAmount().getValue(this.tVal);
        this.computedRotation = ApelMath.normalizeRotation(particleObject.getRotation().getValue(this.tVal));
        this.computedOffset = particleObject.getOffset().getValue(this.tVal);
        if (this.computedAmount <= 0) {
            throw new IllegalArgumentException("Amount of particles has to be above 0");
        }
    }
}
