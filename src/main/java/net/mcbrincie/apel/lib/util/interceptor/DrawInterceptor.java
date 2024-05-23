package net.mcbrincie.apel.lib.util.interceptor;

@FunctionalInterface
public interface DrawInterceptor<T> {
    InterceptedResult<T> apply(InterceptData data, T obj);
}
