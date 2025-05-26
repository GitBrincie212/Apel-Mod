package net.mcbrincie.apel.lib.util.interceptor;

import java.util.*;

/** This is a base class for the interceptor dispatcher where it can host multiple {@link BaseInterceptor} to be executed on a specific code section,
 * it executes the interceptors based on their priority whereby interceptors with higher priority will be executed earlier
 * than those with lower priority. If in any case, there are interceptors that happen to have the same priority, the interceptor
 * that was added the earliest will be executed as first and then the others. You can add interceptors with a specific priority
 * via {@link #addInterceptor(int, BaseInterceptor)} or without using {@link #addInterceptor(BaseInterceptor)}. In the
 * case where you don't add a specific priority, there is a hidden priority counter that increases by 1 per new object interceptor
 * assigned; it will always create a new priority entry (this is where interceptors of the same priority are grouped)
 * <br /> <br />
 * In the case where you have specified a priority. The dispatcher will check if the priority exists, and in that case it
 * will add the interceptor to the final backline of the interceptor list, otherwise it creates a new priority entry. The
 * priority counter takes the priority assigned if it is larger than it
 * <br /> <br />
 *
 * When it is time to execute the interceptors {@link #compute(Object, Object)} may be called which takes
 * care of the rest. <strong>Keep in mind that the context and object will be modified on the compute method</strong>
 *
 * @param <T> The object which the interceptors will modify
 * @param <C> The context which the interceptor will modify
 * @param <I> The type of the interceptor used
 */
@SuppressWarnings("unused")
public abstract class InterceptorDispatcher<T, C, I extends BaseInterceptor<C, T>> {
    private final Map<Integer, List<I>> priorityMap = new HashMap<>();
    private int priorityCounter = 0;
    private final PriorityQueue<Integer> prioritiesQueue = new PriorityQueue<>();

    protected InterceptorDispatcher() {}

    /** Add a particle object interceptor without any predefined priority to the dispatcher
     *
     * @param objectInterceptor The object interceptor to add
     * @return The priority assigned to the object interceptor
     */
    public int addInterceptor(I objectInterceptor) {
        this.priorityMap.put(this.priorityCounter, new ArrayList<>(List.of(objectInterceptor)));
        prioritiesQueue.add(this.priorityCounter);
        this.priorityCounter += 1;
        return this.priorityCounter - 1;
    }

    /** Add a particle object interceptor with a predefined priority to the dispatcher
     *
     * @param objectInterceptor The object interceptor to add
     * @param priority The priority of the object interceptor (if there are more than one interceptor with
     * the same priority then the one that was added the earliest will first execute)
     * @return If there were any other interceptors in that priority
     */
    public boolean addInterceptor(int priority, I objectInterceptor) {
        if (this.priorityMap.containsKey(priority)) {
            List<I> objectInterceptorList = this.priorityMap.get(priority);
            objectInterceptorList.add(objectInterceptor);
            this.priorityMap.replace(priority, objectInterceptorList);
            return true;
        }
        this.priorityMap.put(priority, new ArrayList<>(List.of(objectInterceptor)));
        prioritiesQueue.add(priority);
        if (this.priorityCounter < priority) {
            this.priorityCounter = priority;
        }
        return false;
    }

    /** Trigger a computation in the dispatcher whereby the interceptors with the highest priority will be executed
     * first (in case there are more than one interceptor with the same priority then the one added the earliest will execute
     * first). <strong>Keep in mind this modifies the particle object and the draw context</strong>
     *
     * @param object The particle object to use in the computations
     * @param context The context to use in the computations
     */
    public void compute(T object, C context) {
        while (!prioritiesQueue.isEmpty()) {
            int priorityNum = prioritiesQueue.peek();
            List<I> objectInterceptors = this.priorityMap.get(priorityNum);
            for (I interceptor : objectInterceptors) {
                interceptor.apply(context, object);
            }
        }
    }
}
