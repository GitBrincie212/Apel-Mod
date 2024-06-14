package net.mcbrincie.apel.lib.util.scheduler;

import java.util.LinkedList;
import java.util.Queue;

public class ScheduledSequence {
    private final Queue<ScheduledStep> scheduledSteps;
    public boolean hasAllocatedOnce = false;

    public ScheduledSequence() {
        this.scheduledSteps = new LinkedList<>();
    }

    public void allocateStep(ScheduledStep step) {
        this.hasAllocatedOnce = true;
        this.scheduledSteps.add(step);
    }

    public void deallocateStep() {
        this.scheduledSteps.remove();
    }

    public boolean isEmpty() {
        return this.scheduledSteps.isEmpty();
    }

    public ScheduledStep first() {
        return this.scheduledSteps.peek();
    }
}
