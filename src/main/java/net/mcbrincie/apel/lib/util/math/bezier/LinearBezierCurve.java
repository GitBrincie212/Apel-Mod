package net.mcbrincie.apel.lib.util.math.bezier;

import org.joml.Vector3f;

/** The linear bézier curve is the simplest of all bézier curves since it is a line.
 * It has no control points and is fast to compute (if you plan to use a lot of linear
 * bézier curves for the bézier path animator, then it is best to use the linear animator
 * instead)
 */
@SuppressWarnings("unused")
public class LinearBezierCurve extends BezierCurve {
    /** The constructor for the linear bézier curve which is a line that consists of
     * a starting and an ending point with no control points supplied in the constructor.
     * It is one of the most simple to use but does not allow for control over the shape
     * and how it bends
     *
     * @param start The starting position
     * @param end The ending position
     */
    public LinearBezierCurve(Vector3f start, Vector3f end) {
        super(start, end);
    }

    @Override
    public float length(int amount) {
        return this.start.distance(this.end);
    }

    @Override
    public Vector3f compute(float t) {
        float x = (t - 1) * this.start.x + t * this.end.x;
        float y = (t - 1) * this.start.y + t * this.end.y;
        float z = (t - 1) * this.start.z + t * this.end.z;
        return new Vector3f(x, y, z);
    }
}
