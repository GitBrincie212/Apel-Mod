package net.mcbrincie.apel.lib.util.scheduler;

import net.mcbrincie.apel.lib.animators.PathAnimatorBase;
import net.mcbrincie.apel.lib.exceptions.SeqDuplicateException;
import net.mcbrincie.apel.lib.exceptions.SeqMissingException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** The Scheduler. The scheduler runs on the world server ticks and handles
 *  delaying multiple functions requested by a path animator object by creating
 *  sequences for each object. It also does some security checks to verify
 */
public class ApelScheduler implements Iterable<ScheduledSequence> {
    private final List<ScheduledSequence> scheduledTasks = new ArrayList<>();
    private final List<PathAnimatorBase> authorisedObjects = new ArrayList<>();

    /** Allocates a new sequence chunk to be used in the scheduler. It accepts the animator object
     *  as a parameter. It is crucial to allocate first if you don't have any chunk. The method
     *  throws a {@link  SeqDuplicateException} if it finds that the animator has allocated more
     *  than one sequence chunk
     *
     */
    public void allocateNewSequence(PathAnimatorBase object, int amount) throws SeqDuplicateException {
        if (authorisedObjects.contains(object)) {
            throw new SeqDuplicateException("Cannot allocate more than 1 chunk at a time");
        }
        this.scheduledTasks.add(new ScheduledSequence(amount));
        this.authorisedObjects.add(object);
    }

    /** Allocates a new delayed step. It accepts the animator object and the scheduled step.
     * If the method finds that the object hasn't allocated a sequence chunk then it throws
     * a {@link SeqMissingException}. The allocated step gets deleted once it is executed
     */
    public void allocateNewStep(
            PathAnimatorBase object, ScheduledStep step
    ) throws SeqMissingException {
        int index = this.authorisedObjects.indexOf(object);
        if (index == -1) {
            throw new SeqMissingException("No sequence chunk is found belonging to this path animator");
        }
        this.scheduledTasks.get(index).allocateStep(step);
    }

    public boolean hasAllocated(PathAnimatorBase object) {
        return this.authorisedObjects.contains(object);
    }

    /** <strong>THIS METHOD IS NOT ADVISED TO BE USED</strong>
     * The method is used in the processing of the server which means it won't do
     * security checks. It is used for the de-allocation of the sequence
     *
     * @param index The index to remove at
     */
    @SuppressWarnings("unused")
    public void deallocateSequence(int index) {
        this.scheduledTasks.remove(index);
        this.authorisedObjects.remove(index);
    }

    /** <strong>THIS METHOD IS NOT ADVISED TO BE USED</strong>
     * The method is used in the processing of the server which means it won't do
     * security checks. It is used for the de-allocation of the sequence
     *
     * @param sequence The Sequence to remove
     */
    public void deallocateSequence(ScheduledSequence sequence) {
        int index = this.scheduledTasks.indexOf(sequence);
        this.authorisedObjects.remove(index);
        this.scheduledTasks.remove(index);
    }


    @Override
    @NotNull
    public Iterator<ScheduledSequence> iterator() {
        return this.scheduledTasks.iterator();
    }
}
