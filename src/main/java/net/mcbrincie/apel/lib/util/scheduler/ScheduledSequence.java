package net.mcbrincie.apel.lib.util.scheduler;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public class ScheduledSequence implements Iterable<ScheduledStep> {
    private final ScheduledStep[] scheduledSteps;
    private int storeIndex = -1;

    public ScheduledSequence(int amount) {
        this.scheduledSteps = new ScheduledStep[amount];
    }

    public void allocateStep(ScheduledStep step) {
        int index = this.storeIndex + 1;
        if (index >= this.scheduledSteps.length) {
            throw new IndexOutOfBoundsException("Stored index got out of bounds");
        }
        this.scheduledSteps[index] = step;
        this.storeIndex++;
    }

    public void deallocateStep() {
        this.scheduledSteps[this.storeIndex] = null;
        this.storeIndex--;
    }

    public boolean isEmpty() {
        return this.storeIndex == -1;
    }

    public ScheduledStep first() {
        return this.scheduledSteps[Math.max(0, storeIndex)];
    }

    @NotNull
    @Override
    public Iterator<ScheduledStep> iterator() {
        return Arrays.stream(this.scheduledSteps).iterator();
    }
}
