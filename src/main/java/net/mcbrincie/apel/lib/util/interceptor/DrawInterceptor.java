package net.mcbrincie.apel.lib.util.interceptor;

@FunctionalInterface
public interface DrawInterceptor<T, R extends Enum<R>> {
    InterceptedResult<T, R> apply(InterceptData<R> data, T obj);

    static <T, R extends Enum<R>> DrawInterceptor<T, R> identity() {
        return InterceptedResult::new;
    }
}
