package net.mcbrincie.apel.lib.util.math.bezier;

import org.joml.Vector3f;

import java.util.List;

/** The cubic Bézier curve is another popular choice of use. It has 2 control points
 * which define how the curve should bend, which means the user has more control of
 * the curve's shape. It is a second popular choice to use alongside the quadratic Bézier curve.
 * Note that it is best to use the method instead of the parameterized Bézier curve.
 */
@SuppressWarnings("unused")
public class CubicBezierCurve extends QuadraticBezierCurve {
    protected final Vector3f controlPoint2;

    /** The constructor for the cubic Bézier curve, which is a curve that consists of
     * a starting and an ending point and two control point defining the curvature.
     * It is more advanced than the linear & a bit more than the quadratic Bézier curve
     * but has more capabilities.
     *
     * @param start The starting position
     * @param end The ending position
     * @param controlPoint1 The first control point
     * @param controlPoint2 The second control point
    */
    public CubicBezierCurve(Vector3f start, Vector3f end, Vector3f controlPoint1, Vector3f controlPoint2) {
        super(start, end, controlPoint1);
        this.controlPoint2 = controlPoint2;
    }

    @Override
    public List<Vector3f> getControlPoints() {
        return List.of(this.controlPoint, this.controlPoint2);
    }

    @Override
    public Vector3f compute(float t) {
        // Basic formula for cubic curve is this:
        // (1-t)^3 * x0 + t(1-t)^2 * x1 + (t^2)(1-t) * x2 + t^3 * x3
        float oneMinusT = 1 - t;
        float oneMinusTSquared = oneMinusT * oneMinusT;
        float oneMinusTCubed = oneMinusTSquared * oneMinusT;
        float tSquared = t * t;
        float tCubed = tSquared * t;
        float x = oneMinusTCubed * this.start.x + 3 * oneMinusTSquared * t * this.controlPoint.x + 3
                * oneMinusT * tSquared * this.controlPoint2.x + tCubed * this.end.x;
        float y = oneMinusTCubed * this.start.y + 3 * oneMinusTSquared * t * this.controlPoint.y + 3
                * oneMinusT * tSquared * this.controlPoint2.y + tCubed * this.end.y;
        float z = oneMinusTCubed * this.start.z + 3 * oneMinusTSquared * t * this.controlPoint.z + 3
                * oneMinusT * tSquared * this.controlPoint2.z + tCubed * this.end.z;
        return new Vector3f(x, y, z);
    }
}
