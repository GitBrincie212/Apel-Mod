package net.mcbrincie.apel.lib.util.scheduler;

import java.util.Stack;

public class ScheduledSequence {
    private final Stack<ScheduledStep> scheduledSteps;

    public ScheduledSequence() {
        this.scheduledSteps = new Stack<>();
    }

    public void allocateStep(ScheduledStep step) {
        this.scheduledSteps.push(step);
    }

    public void deallocateStep() {
        this.scheduledSteps.pop();
    }

    public boolean isEmpty() {
        return this.scheduledSteps.isEmpty();
    }

    public ScheduledStep first() {
        return this.scheduledSteps.peek();
    }
}
