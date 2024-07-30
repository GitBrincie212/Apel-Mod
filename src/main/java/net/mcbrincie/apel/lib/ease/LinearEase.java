package net.mcbrincie.apel.lib.ease;

@SuppressWarnings("unused")
public class LinearEase<T extends Number> extends BoundedEase<T> {
    protected T slope;
    protected T offset;

    public LinearEase(T slope, T offset, T min_value, T max_value) {
        super(min_value, max_value);
        this.slope = slope;
        this.offset = offset;
    }

    public LinearEase(T slope, T offset) {
        this(slope, offset, null, null);
    }

    @Override
    public T compute(int renderStep) {
        double computedValue = this.slope.doubleValue() * (this.offset.doubleValue() + renderStep);
        if (this.max_value != null) {
            computedValue = Math.min(computedValue, this.max_value.doubleValue());
        }
        if (this.min_value != null) {
            computedValue = Math.max(computedValue, this.min_value.doubleValue());
        }
        return (T) (Number) computedValue;
    }
}
