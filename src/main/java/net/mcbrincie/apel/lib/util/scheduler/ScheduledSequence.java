package net.mcbrincie.apel.lib.util.scheduler;

import java.util.LinkedList;
import java.util.Queue;

public class ScheduledSequence {
    private final Queue<ScheduledStep> scheduledSteps;
    private boolean hasAllocatedOnce = false;

    public ScheduledSequence() {
        this.scheduledSteps = new LinkedList<>();
    }

    public void allocateStep(ScheduledStep step) {
        this.hasAllocatedOnce = true;
        this.scheduledSteps.add(step);
    }

    public boolean isEmpty() {
        return this.scheduledSteps.isEmpty();
    }

    public boolean isFinished() {
        return isEmpty() && this.hasAllocatedOnce;
    }

    public boolean tick() {
        ScheduledStep firstStep = this.scheduledSteps.peek();
        if (firstStep == null) {
            return false;
        }
        boolean stepExecuted = firstStep.tick();
        if (stepExecuted) {
            this.scheduledSteps.remove();
        }
        return stepExecuted;
    }
}
