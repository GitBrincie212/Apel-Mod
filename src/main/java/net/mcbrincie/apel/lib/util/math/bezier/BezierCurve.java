package net.mcbrincie.apel.lib.util.math.bezier;

import org.joml.Vector3f;

import java.util.List;

/** The Bézier curve which is a family of curves that are defined by control points.
 * The first control point is the starting position, the last one is the ending position,
 * and all the rest are the control points that bend the curve(not connect it) towards that
 * control point's position
 */
@SuppressWarnings("unused")
public abstract class BezierCurve {
    protected Vector3f start;
    protected Vector3f end;

    /** Constructor for the bézier curve which has a starting position and an ending position.
     * There are no control points involved in the constructor, and it is up to the user to define
     * their own number of control points (except the linear bézier curve which remains the same)
     *
     * @param start The starting position
     * @param end The ending position
     */
    public BezierCurve(Vector3f start, Vector3f end) {
        this.start = start;
        this.end = end;
    }

    /** Gets the starting position and returns it
     *
     * @return The starting position
     */
    public Vector3f getStart() {
        return this.start;
    }

    /** Gets the ending position and returns it
     *
     * @return The ending position
     */
    public Vector3f getEnd() {
        return this.end;
    }

    /** Sets the ending position to a new value and returns the previous end value used
     *
     * @param end The new ending position
     * @return The previous ending position
     */
    public Vector3f setEnd(Vector3f end) {
        Vector3f prev = this.end;
        this.end = end;
        return prev;
    }

    /** Sets the starting position to a new value and returns the previous start value used
     *
     * @param start The new starting position
     * @return The previous starting position
    */
    public Vector3f getStart(Vector3f start) {
        Vector3f prev = this.start;
        this.start = start;
        return prev;
    }

    public abstract List<Vector3f> getControlPoints();

    /** Returns the length of the bézier curve (how long is it, not the distance).
     * The length calculations are different for each bézier curve (since they are composed differently).
     * The "amount" param dictates the number of points in the curve (used to calculate the distance)
     *
     * @param amount The number of points
     * @return The length of the curve
    */
    public abstract float length(int amount);

    /** This is the compute method where given a number t that ranges from 0 to 1, the method returns
     *  the 3D coordinates of the point that lives within the bézier curve.
     *
     * @param t The interpolation value(0 -> 1)
     * @return The 3D coordinates of the point
     */
    public abstract Vector3f compute(float t);
}
