package net.mcbrincie.apel.lib.util.interceptor;

@SuppressWarnings("unused")
public class InterceptedResult<T, R extends Enum<R>> {
    public InterceptData<R> interceptData;
    public T object;

    public InterceptedResult(InterceptData<R> data, T obj) {
        this.object = obj;
        this.interceptData = data;
    }

    public void clearResult() {
        this.object = null;
        this.interceptData.clearMetadata();
    }
}
