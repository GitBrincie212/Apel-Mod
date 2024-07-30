package net.mcbrincie.apel.lib.ease;

@SuppressWarnings({"UnusedReturnValue", "BooleanMethodIsAlwaysInverted", "unused"})
public abstract class BoundedEase<T extends Number> implements EaseCurve<T> {
    protected T min_value = null;
    protected T max_value = null;

    public BoundedEase(T min_value, T max_value) {
        this.setMaxValue(max_value);
        this.setMinValue(min_value);
    }

    public T getMaxValue() {
        return this.max_value;
    }

    public T getMinValue() {
        return this.min_value;
    }

    public T setMaxValue(T newValue) {
        T prevMaxValue = this.max_value;
        this.max_value = newValue;
        return prevMaxValue;
    }

    public T setMinValue(T newValue) {
        T prevMinValue = this.min_value;
        this.min_value = newValue;
        return prevMinValue;
    }

    public abstract T compute(int renderStep);
}
