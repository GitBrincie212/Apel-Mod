package net.mcbrincie.apel.lib.util;

/** The path animation trimming, which is a basic container that holds the trimming data for
 * the path animator to use.  Trimming data varies among path animators, for example, the circular
 * path animator requires radian values when trimming, whereas the linear path animator requires
 * the steps for trimming.
 *
 * @param <T> The type to use for the trimming (look up what type the path animator you target uses)
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class AnimationTrimming<T> {
    protected T start;
    protected T end;

    /** The constructor for the animation trimming container.
     * It accepts as parameters the starting trim and the ending trim
     *
     * @param start The starting trimming
     * @param end The ending trimming
     * @see AnimationTrimming#AnimationTrimming(Object)
     */
    public AnimationTrimming(T start, T end) {
        this.start = start;
        this.end = end;
    }

    /** The constructor for the animation trimming container.
     * It accepts as parameters the starting trim only
     *
     * @param start The starting trimming
     * @see AnimationTrimming#AnimationTrimming(Object, Object)
    */
    public AnimationTrimming(T start) {
        this.start = start;
    }

    /** Sets the starting trimming to a new value and returns the previous value used
     *
     * @param newStart the new start trimming value
     * @return The previous start trimming value
     */
    public T setStart(T newStart) {
        T prev = this.start;
        this.start = newStart;
        return prev;
    }

    /** Sets the ending trimming to a new value and returns the previous value used
     *
     * @param newEnd The new ending trimming value
     * @return The previous ending trimming value
     */
    public T setEnd(T newEnd) {
        T prev = this.end;
        this.end = newEnd;
        return prev;
    }

    /** Gets the starting trimming value
     *
     * @return The starting trimming value
    */
    public T getStart() {return this.start;}

    /** Gets the ending trimming value
     *
     * @return The ending trimming value
    */
    public T getEnd() {return this.end;}
}
