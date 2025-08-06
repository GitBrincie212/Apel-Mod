package net.mcbrincie.apel.lib.util.interceptor;

import net.mcbrincie.apel.lib.animators.PathAnimatorBase;
import net.mcbrincie.apel.lib.util.interceptor.context.AnimationContext;

/** This is a dispatcher where it can host multiple {@link AnimationInterceptor} to be executed on a specific code section,
 * it executes the interceptors based on their priority whereby interceptors with higher priority will be executed earlier
 * than those with lower priority. If in any case, there are interceptors that happen to have the same priority, the interceptor
 * that was added the earliest will be executed as first and then the others. You can add interceptors with a specific priority
 * via {@link #addInterceptor(int, AnimationInterceptor)} or without using {@link #addInterceptor(AnimationInterceptor)}. In the
 * case where you don't add a specific priority, there is a hidden priority counter that increases by 1 per new object interceptor
 * assigned; it will always create a new priority entry (this is where interceptors of the same priority are grouped)
 * <br /> <br />
 * In the case where you have specified a priority. The dispatcher will check if the priority exists, and in that case it
 * will add the interceptor to the final backline of the interceptor list, otherwise it creates a new priority entry. The
 * priority counter takes the priority assigned if it is larger than it
 * <br /> <br />
 *
 * When it is time to execute the interceptors {@link #compute(PathAnimatorBase, AnimationContext)} may be called which takes
 * care of the rest. <strong>Keep in mind that the {@link AnimationContext} and {@link PathAnimatorBase} will be modified
 * on the compute method</strong>
 *
 * @param <T> The path animator which the interceptors will modify
 */
@SuppressWarnings("unused")
public class AnimationInterceptorDispatcher<T extends PathAnimatorBase<T>>
        extends InterceptorDispatcher<T, AnimationContext, AnimationInterceptor<T>> {

    public AnimationInterceptorDispatcher() {}

    /** Add a path animator interceptor without any predefined priority to the dispatcher
     *
     * @param animationInterceptor The path animator interceptor to add
     * @return The priority assigned to the path animator interceptor
     */
    public int addInterceptor(AnimationInterceptor<T> animationInterceptor) {
        return super.addInterceptor(animationInterceptor);
    }

    /** Add a path animator interceptor with a predefined priority to the dispatcher
     *
     * @param animationInterceptor The path animation interceptor to add
     * @param priority The priority of the path animation interceptor (if there are more than one interceptor with
     * the same priority then the one that was added the earliest will first execute)
     * @return If there were any other interceptors in that priority
     */
    public boolean addInterceptor(int priority, AnimationInterceptor<T> animationInterceptor) {
        return super.addInterceptor(priority, animationInterceptor);
    }

    /** Trigger a computation in the dispatcher whereby the interceptors with the highest priority will be executed
     * first (in case there are more than one interceptor with the same priority then the one added the earliest will execute
     * first). <strong>Keep in mind this modifies the path animator and the animation context</strong>
     *
     * @param pathAnimator The path animator to use in the computations
     * @param animationContext The animation context to use in the computations
     */
    public void compute(T pathAnimator, AnimationContext animationContext) {
        super.compute(pathAnimator, animationContext);
    }
}
