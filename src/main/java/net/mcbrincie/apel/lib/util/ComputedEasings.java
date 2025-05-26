package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.lib.easing.EasingCurve;

import java.util.HashMap;

public class ComputedEasings<E extends ComputedEasings<E>> {
    protected final float tVal;
    protected HashMap<String, Object> computedFields = new HashMap<>();

    public ComputedEasings(float currStep, float numberOfSteps) {
        this.tVal = currStep / numberOfSteps;
    }

    public <T> E addComputedField(String name, EasingCurve<T> value) {
        this.computedFields.put(name, value.getValue(this.tVal));
        return (E) this;
    }

    public Object getComputedField(String name) {
        return this.computedFields.get(name);
    }
}
