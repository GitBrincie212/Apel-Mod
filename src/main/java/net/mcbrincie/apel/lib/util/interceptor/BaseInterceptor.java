package net.mcbrincie.apel.lib.util.interceptor;

import net.mcbrincie.apel.lib.objects.ParticleObject;

public interface BaseInterceptor<D, T> {
    /** Apply the interceptor (which basically is computing the interceptor).
     * <br><br>
     * There are no results returned
     *
     * @param data metadata useful within the interceptor
     * @param object the object to intercept
     */
    void apply(D data, T object);

    /** An identity interceptor that does nothing. May be used when clearing an
     * interceptor.
     * @return the identity interceptor
     * @param <T> The type being intercepted
     */
    static <D, T> BaseInterceptor<D, T> identity() {
        return (data, object) -> {};
    }
}
