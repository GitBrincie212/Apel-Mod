package net.mcbrincie.apel.lib.util.interceptor;

public class InterceptedResult<T> {
    public InterceptData interceptData;
    public T object;

    public InterceptedResult(InterceptData data, T obj) {
        this.object = obj;
        this.interceptData = data;
    }
}
