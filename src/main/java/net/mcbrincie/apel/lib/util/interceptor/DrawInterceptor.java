package net.mcbrincie.apel.lib.util.interceptor;

/** DrawInterceptor defines the signature for interceptor methods in APEL.
 * At several points in the library, Apel allows developers to inject custom
 * handling.  There are two options in these situations: provide an implementation
 * of this functional interface as either a lambda or a class instance, or provide
 * nothing and have the {@link #identity()} implementation be called.
 * <br><br>
 * Callers can expect to be passed the object on which the interception is occurring
 * and a set of metadata that may be useful within the interceptor.  Many updates
 * to the object being intercepted are in-place such as setting rotation, modifying
 * an offset, changing a vertex, and so on.  The {@link InterceptData} parameter
 * will contain additional useful information for the point at which the intercept
 * occurs, and modifications to that information are also done in-place via its
 * {@link InterceptData#getMetadata(Enum, Object)} and
 * {@link InterceptData#addMetadata(Enum, Object)} methods to retrieve and update
 * metadata values, respectively.
 * @param <T> The type being intercepted
 * @param <R> The enum type defining the additional keys available in {@code InterceptData}.
 */
@FunctionalInterface
public interface DrawInterceptor<T, R extends Enum<R>> {
    /** Apply the interceptor.
     * <br><br>
     * Return values are sent via the {@code InterceptData}'s metadata map.
     *
     * @param data metadata useful within the interceptor
     * @param obj the object being intercepted
     */
    void apply(InterceptData<R> data, T obj);

    /** An identity interceptor that does nothing.  May be used when clearing an
     * interceptor.
     * @return the identity interceptor
     * @param <T> The type being intercepted
     * @param <R> The enum type defining the additional keys available in {@code InterceptData}.
     */
    static <T, R extends Enum<R>> DrawInterceptor<T, R> identity() {
        return (data, object) -> {};
    }
}
