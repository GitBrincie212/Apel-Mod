package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.util.math.ApelMath;
import org.joml.Vector3f;

import java.util.HashMap;

public class ComputedEasingPO {
    public final Vector3f computedRotation;
    public final Vector3f computedOffset;
    public final int computedAmount;
    protected HashMap<String, Object> computedFields = new HashMap<>();

    private final float tVal;

    public ComputedEasingPO(ParticleObject<?> particleObject, float currStep, float numberOfSteps) {
        this.tVal = currStep / numberOfSteps;
        this.computedAmount = particleObject.getAmount().getValue(this.tVal);
        this.computedRotation = ApelMath.normalizeRotation(particleObject.getRotation().getValue(this.tVal));
        this.computedOffset = particleObject.getOffset().getValue(this.tVal);
        if (this.computedAmount <= 0) {
            throw new IllegalArgumentException("Amount of particles has to be above 0");
        }
    }

    public <T> ComputedEasingPO addComputedField(String name, EasingCurve<T> value) {
        this.computedFields.put(name, value.getValue(this.tVal));
        return this;
    }

    public Object getComputedField(String name) {
        return this.computedFields.get(name);
    }
}
