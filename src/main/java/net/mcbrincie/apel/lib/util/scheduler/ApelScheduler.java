package net.mcbrincie.apel.lib.util.scheduler;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.animators.PathAnimatorBase;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** The Scheduler. The scheduler runs on the world server ticks and handles
 *  delaying multiple functions requested by a path animator object by creating
 *  sequences for each object. It also does some security checks to verify
 */
@SuppressWarnings("unused")
public class ApelScheduler {
    private final List<ScheduledSequence> scheduledTasks = new ArrayList<>();
    private final List<PathAnimatorBase> animators = new ArrayList<>();

    /** Allocates a new sequence chunk to be used in the scheduler. It accepts the animator object
     *  as a parameter. It is crucial to allocate first if you don't have any chunk. The method
     *  throws a {@link  SeqDuplicateException} if it finds that the animator has allocated more
     *  than one sequence chunk
     *
     */
    public void allocateNewSequence(PathAnimatorBase object, int amount) throws SeqDuplicateException {
        this.scheduledTasks.add(new ScheduledSequence());
        this.animators.add(object);
    }

    /** Allocates a new delayed step. It accepts the animator object and the scheduled step.
     * If the method finds that the object hasn't allocated a sequence chunk then it throws
     * a {@link SeqMissingException}. The allocated step gets deleted once it is executed
     */
    public void allocateNewStep(
            PathAnimatorBase object, ScheduledStep step
    ) throws SeqMissingException {
        int index = this.animators.indexOf(object);
        if (index == -1) {
            throw new SeqMissingException("No sequence chunk is found belonging to this path animator");
        }
        this.scheduledTasks.get(index).allocateStep(step);
    }

    /** <strong>THIS METHOD IS NOT ADVISED TO BE USED</strong>
     * The method is used in the processing of the server which means it won't do
     * security checks. It is used for the de-allocation of the sequence
     *
     * @param index The index to remove at
     */
    public void deallocateSequence(int index) {
        this.scheduledTasks.remove(index);
        this.animators.remove(index);
    }

    /** <strong>THIS METHOD IS NOT ADVISED TO BE USED</strong>
     * The method is used in the processing of the server which means it won't do
     * security checks. It is used for the de-allocation of the object
     *
     * @param object The Path animator to remove
     */
    public void deallocateSequence(PathAnimatorBase object) {
        int index = this.animators.indexOf(object);
        this.deallocateSequence(index);
    }

    /** Returns whenever the scheduler has any work to do
     *
     * @return a boolean that indicates if the scheduler has work to do
     */
    public boolean isProcessing() {
        return !this.scheduledTasks.isEmpty();
    }

    public void runTick() {
        Iterator<ScheduledSequence> iterator = this.scheduledTasks.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            ScheduledSequence sequence = iterator.next();
            ScheduledStep firstElement = sequence.first();
            if (firstElement == null) {
                index++;
                continue;
            }
            firstElement.delay--;
            if (firstElement.delay == 0) {
                for (Runnable func : firstElement.func) {
                    Apel.drawThread.submit(func);
                }
                sequence.deallocateStep();
                if (sequence.isEmpty() && sequence.hasAllocatedOnce) {
                    iterator.remove();
                    this.animators.remove(index);
                    index--;
                }
            }
            index++;
        }
    }
}
