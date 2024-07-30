package net.mcbrincie.apel.lib.ease;

@SuppressWarnings("unused")
public interface EaseCurve<T extends Number> {
    T compute(int renderStep);
}
