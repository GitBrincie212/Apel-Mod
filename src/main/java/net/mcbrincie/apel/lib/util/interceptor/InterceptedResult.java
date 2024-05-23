package net.mcbrincie.apel.lib.util.interceptor;

public class InterceptedResult<T> {
    public InterceptData interceptData;
    public T object;

    public InterceptedResult(T obj, InterceptData data) {
        this.object = obj;
        this.interceptData = data;
    }
}
