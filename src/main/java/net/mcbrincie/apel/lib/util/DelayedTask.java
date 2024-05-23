package net.mcbrincie.apel.lib.util;

public class DelayedTask<T extends Runnable> {
    public int delay;
    public T func;

    public DelayedTask(T func, int delay) {
        if (delay <= 0) {
            throw new IllegalArgumentException("Delayed task's delay cannot be negative or zero");
        }
        this.func = func;
        this.delay = delay;
    }
}
