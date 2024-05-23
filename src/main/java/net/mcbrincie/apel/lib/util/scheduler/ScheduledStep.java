package net.mcbrincie.apel.lib.util.scheduler;

public class ScheduledStep {
    public Runnable[] func;
    public int delay;

    public ScheduledStep(Integer delay, Runnable[] func) {
        this.delay = delay;
        this.func = func;
    }
}
