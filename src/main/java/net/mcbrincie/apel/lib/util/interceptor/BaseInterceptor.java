package net.mcbrincie.apel.lib.util.interceptor;

public interface BaseInterceptor<D, T> {
    /** Apply the interceptor (which basically is computing the interceptor).
     * <br><br>
     * There are no results returned
     *
     * @param data metadata useful within the interceptor
     * @param object the object to intercept
     */
    void apply(D data, T object);
}
