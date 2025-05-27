package net.mcbrincie.apel.lib.util.interceptor;

import net.mcbrincie.apel.lib.objects.ParticleObject;
import net.mcbrincie.apel.lib.util.interceptor.context.DrawContext;

/** This is a dispatcher where it can host multiple {@link ObjectInterceptor} to be executed on a specific code section,
 * it executes the interceptors based on their priority whereby interceptors with higher priority will be executed earlier
 * than those with lower priority. If in any case, there are interceptors that happen to have the same priority, the interceptor
 * that was added the earliest will be executed as first and then the others. You can add interceptors with a specific priority
 * via {@link #addInterceptor(int, ObjectInterceptor)} or without using {@link #addInterceptor(ObjectInterceptor)}. In the
 * case where you don't add a specific priority, there is a hidden priority counter that increases by 1 per new object interceptor
 * assigned; it will always create a new priority entry (this is where interceptors of the same priority are grouped)
 * <br /> <br />
 * In the case where you have specified a priority. The dispatcher will check if the priority exists, and in that case it
 * will add the interceptor to the final backline of the interceptor list, otherwise it creates a new priority entry. The
 * priority counter takes the priority assigned if it is larger than it
 * <br /> <br />
 *
 * When it is time to execute the interceptors {@link #compute(ParticleObject, DrawContext)} may be called which takes
 * care of the rest. <strong>Keep in mind that the {@link DrawContext} and {@link ParticleObject} will be modified on the compute method</strong>
 *
 * @param <T> The particle object which the interceptors will modify
 */
@SuppressWarnings("unused")
public class ObjectInterceptorDispatcher<T extends ParticleObject<T>>
        extends InterceptorDispatcher<T, DrawContext, ObjectInterceptor<T>> {

    public ObjectInterceptorDispatcher() {}

    /** Add a particle object interceptor without any predefined priority to the dispatcher
     *
     * @param objectInterceptor The object interceptor to add
     * @return The priority assigned to the object interceptor
     */
    public int addInterceptor(ObjectInterceptor<T> objectInterceptor) {
        return super.addInterceptor(objectInterceptor);
    }

    /** Add a particle object interceptor with a predefined priority to the dispatcher
     *
     * @param objectInterceptor The object interceptor to add
     * @param priority The priority of the object interceptor (if there are more than one interceptor with
     * the same priority then the one that was added the earliest will first execute)
     * @return If there were any other interceptors in that priority
     */
    public boolean addInterceptor(int priority, ObjectInterceptor<T> objectInterceptor) {
        return super.addInterceptor(priority, objectInterceptor);
    }

    /** Trigger a computation in the dispatcher whereby the interceptors with the highest priority will be executed
     * first (in case there are more than one interceptor with the same priority then the one added the earliest will execute
     * first). <strong>Keep in mind this modifies the particle object and the draw context</strong>
     *
     * @param object The particle object to use in the computations
     * @param drawContext The draw context to use in the computations
     */
    public void compute(T object, DrawContext drawContext) {
        super.compute(object, drawContext);
    }
}
