package net.mcbrincie.apel.lib.util.math.bezier;

import org.joml.Vector3f;

import java.util.List;

/** The quadratic bézier curve which is a bit more advanced curve that has only one control point
 * and allows for control over how the curve bends, although not as much control as the cubic one.
 * It still allows for flexible behavior, and is one of the most popular choices among graphics
 * enthusiasts
 */
@SuppressWarnings("unused")
public class QuadraticBezierCurve extends BezierCurve {
    protected final Vector3f controlPoint;

    /** The constructor for the quadratic bézier curve, which is a curve that consists of
     * a starting and an ending point and one control point defining the curvature.
     * It is a bit more advanced than the linear bézier curve but has more capabilities
     *
     * @param start The starting position
     * @param end The ending position
     * @param controlPoint The control point
     */
    public QuadraticBezierCurve(Vector3f start, Vector3f end, Vector3f controlPoint) {
        super(start, end);
        this.controlPoint = controlPoint;
    }

    @Override
    public List<Vector3f> getControlPoints() {
        return List.of(this.controlPoint);
    }

    @Override
    public float length(int amount) {
        float sumDistance = 0;
        Vector3f prevPoint = this.start;
        float interval = (float) 1 / amount;
        for (int i = 0; i < amount; i++) {
            Vector3f point = compute(interval * i);
            sumDistance += point.distance(prevPoint);
            prevPoint = point;
        }
        return sumDistance;
    }

    @Override
    public Vector3f compute(float t) {
        float oneMinusT = 1 - t;
        float oneMinusTSquared = oneMinusT * oneMinusT;
        float tSquared = t * t;

        float x = oneMinusTSquared * this.start.x + 2 * oneMinusT * t * this.controlPoint.x + tSquared * this.end.x;
        float y = oneMinusTSquared * this.start.y + 2 * oneMinusT * t * this.controlPoint.y + tSquared * this.end.y;
        float z = oneMinusTSquared * this.start.z + 2 * oneMinusT * t * this.controlPoint.z + tSquared * this.end.z;
        return new Vector3f(x, y, z);
    }
}
