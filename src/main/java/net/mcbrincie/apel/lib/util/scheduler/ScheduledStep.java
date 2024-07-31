package net.mcbrincie.apel.lib.util.scheduler;

import net.mcbrincie.apel.Apel;

public class ScheduledStep {
    private static final Runnable NOOP = () -> {};

    private final Runnable[] actions;
    private final Runnable delayFunc;

    private int delay;

    public ScheduledStep(int delay, Runnable[] actions) {
        this(delay, actions, NOOP);
    }

    public ScheduledStep(int delay, Runnable[] actions, Runnable delayFunc) {
        this.delay = delay;
        this.delayFunc = delayFunc;
        this.actions = actions;
    }

    public boolean tick() {
        this.delay--;
        if (this.delay == 0) {
            for (Runnable action : this.actions) {
                Apel.DRAW_EXECUTOR.submit(action);
            }
            return true;
        } else {
            Apel.DRAW_EXECUTOR.submit(this.delayFunc);
        }
        return false;
    }
}
