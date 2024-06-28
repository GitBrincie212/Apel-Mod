package net.mcbrincie.apel.lib.util.math.bezier;

import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

/** The Bézier curve which is a family of curves that are defined by control points.
 * The first point is the starting position, the last one is the ending position,
 * and all the rest are the control points that bend the curve (not connect it) towards that
 * control point's position
 */
@SuppressWarnings("unused")
public abstract class BezierCurve {
    protected Vector3f start;
    protected Vector3f end;

    /**
     * Factory method to simplify construction of a BezierCurve implementation based on the number of control points.
     * @param start The start point of the Bézier curve
     * @param end The end point of the Bézier curve
     * @param controlPoints A list (can be empty) of control points
     * @return A BezierCurve implementation
     */
    public static BezierCurve of(Vector3f start, Vector3f end, List<Vector3f> controlPoints) {
        return switch (controlPoints.size()) {
            case 0 -> new LinearBezierCurve(start, end);
            case 1 -> new QuadraticBezierCurve(start, end, controlPoints.getFirst());
            case 2 -> new CubicBezierCurve(start, end, controlPoints.get(0), controlPoints.get(1));
            default -> new ParameterizedBezierCurve(start, end, controlPoints);
        };
    }

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

    /** Sets the starting position to a new value and returns the previous start value used
     *
     * @param start The new starting position
     * @return The previous starting position
     */
    public Vector3f setStart(Vector3f start) {
        Vector3f prev = this.start;
        this.start = start;
        return prev;
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

    /**
     * Gets the list of control points, if any, and returns it.
     *
     * @return The list of control points
     */
    public abstract List<Vector3f> getControlPoints();

    /** Returns the length of the Bézier curve (how long is it, not the distance between control points).
     * The length calculations are different for each Bézier curve (since they are composed differently).
     * The {@code amount} dictates the number of points along the curve used to calculate the distance; a higher
     * value will be more accurate, but will take correspondingly longer to calculate.
     *
     * @param amount The number of points
     * @return The length of the curve
    */
    public abstract float length(int amount);

    /** Compute the point along the curve at {@code t}, with {@code t} in the range {@code [0, 1]}.
     *
     * @param t The interpolation value, between 0 and 1, inclusive
     * @return The 3D coordinates of the point
     */
    public abstract Vector3f compute(float t);

    /**
     * Two Bézier curves are equal if they are the same degree and have the same start, end, and control points (if
     * any).
     *
     * @param o The other Bézier curve
     * @return Boolean indicating equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BezierCurve that = (BezierCurve) o;
        return Objects.equals(getStart(), that.getStart()) && Objects.equals(getEnd(), that.getEnd()) && Objects.equals(
                getControlPoints(), that.getControlPoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStart(), getEnd(), getControlPoints());
    }
}
