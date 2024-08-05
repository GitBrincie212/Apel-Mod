package net.mcbrincie.apel.lib.util.interceptor;

import net.mcbrincie.apel.lib.objects.ParticleObject;

/** ObjectInterceptor defines the signature for interceptor methods in APEL.
 * At several points in the library, Apel allows developers to inject custom
 * handling.  There are two options in these situations: provide an implementation
 * of this functional interface as either a lambda or a class instance, or provide
 * nothing and have the {@link #identity()} implementation be called.
 * <br><br>
 * Callers can expect to be passed the object on which the interception is occurring
 * and a set of metadata that may be useful within the interceptor.  Many updates
 * to the object being intercepted are in-place such as setting rotation, modifying
 * an offset, changing a vertex, and so on.  The {@link DrawContext} parameter
 * will contain additional useful information for the point at which the intercept
 * occurs, and modifications to that information are also done in-place via its
 * {@link DrawContext#getMetadata(DrawContext.Key, Object)} and
 * {@link DrawContext#addMetadata(DrawContext.Key, Object)} methods to retrieve and
 * update metadata values, respectively.
 *
 * @param <T> The ParticleObject type being intercepted
 */
@FunctionalInterface
public interface ObjectInterceptor<T extends ParticleObject<T>> {
    /** Apply the interceptor.
     * <br><br>
     * Return values are sent via the {@code InterceptData}'s metadata map.
     *
     * @param data metadata useful within the interceptor
     * @param obj the object being intercepted
     */
    void apply(DrawContext data, T obj);

    /** An identity interceptor that does nothing.  May be used when clearing an
     * interceptor.
     * @return the identity interceptor
     * @param <T> The type being intercepted
     */
    static <T extends ParticleObject<T>> ObjectInterceptor<T> identity() {
        return (data, object) -> {};
    }
}
