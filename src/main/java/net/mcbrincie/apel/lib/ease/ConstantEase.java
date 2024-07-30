package net.mcbrincie.apel.lib.ease;

@SuppressWarnings("unused")
public class ConstantEase<T extends Number> implements EaseCurve<T> {
    protected T value;

    public ConstantEase(T value) {
        this.value = value;
    }

    @Override
    public T compute(int renderStep) {
        return this.value;
    }
}
