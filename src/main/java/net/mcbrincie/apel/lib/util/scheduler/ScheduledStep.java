package net.mcbrincie.apel.lib.util.scheduler;

import net.mcbrincie.apel.Apel;

public class ScheduledStep {
    private final Runnable[] actions;
    private int delay;

    public ScheduledStep(Integer delay, Runnable[] actions) {
        this.delay = delay;
        this.actions = actions;
    }

    public boolean tick() {
        this.delay--;
        if (this.delay == 0) {
            for (Runnable action : this.actions) {
                Apel.drawThread.submit(action);
            }
            return true;
        }
        return false;
    }
}
