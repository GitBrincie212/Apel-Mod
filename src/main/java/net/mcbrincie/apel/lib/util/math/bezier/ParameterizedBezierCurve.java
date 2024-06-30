package net.mcbrincie.apel.lib.util.math.bezier;

import com.google.common.collect.ImmutableList;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.List;


/** The parametrized Bézier curve, which is a custom curve that is defined by an (n) number
 * of control points, it is best to use this type of curve when no there are no already predefined.
 * If you have 1 control point, then it is best to use a linear Bézier curve. For 2, it is best to use
 * the quadratic Bézier curve, and for 3 control points it is best to use the cubic Bézier curve. For
 * any higher order Bézier curve, use this implementation.
 */
@SuppressWarnings("unused")
public class ParameterizedBezierCurve extends BezierCurve {
    private final List<Vector3f> controlPoints;
    private final List<Vector3f> deCasteljauPoints;

    /** The constructor for the parametrized Bézier curve, which is a curve that consists of
     * a starting and an ending point and (n) number of control points defining the curvature.
     * It is one of the most advanced ones to use, but it should only be used when needing 3 or
     * more control points for the curve control of the shape since it can cause performance issues.
     *
     * @param start The starting position
     * @param end The ending position
     * @param controlPoints The control points to use
     *
     * @see ParameterizedBezierCurve#ParameterizedBezierCurve(Vector3f, Vector3f, List)
    */
    public ParameterizedBezierCurve(Vector3f start, Vector3f end, Vector3f... controlPoints) {
        this(start, end, List.of(controlPoints));
    }

    /** The constructor for the parametrized Bézier curve, which is a curve that consists of
     * a starting and an ending point and (n) number of control points defining the curvature.
     * It is one of the most advanced ones to use, but it should only be used when needing 3 or
     * more control points for the curve control of the shape since it can cause performance issues.
     *
     * @param start The starting position
     * @param end The ending position
     * @param controlPoints The control points to use
     *
     * @see ParameterizedBezierCurve#ParameterizedBezierCurve(Vector3f, Vector3f, Vector3f...)
    */
    public ParameterizedBezierCurve(Vector3f start, Vector3f end, List<Vector3f> controlPoints) {
        super(start, end);
        this.controlPoints = List.copyOf(controlPoints);
        this.deCasteljauPoints = ImmutableList.<Vector3f>builder().add(start).addAll(controlPoints).add(end).build();
    }

    @Override
    public List<Vector3f> getControlPoints() {
        return controlPoints;
    }

    @Override
    public float length(int amount) {
        float sumDistance = 0;
        Vector3f prevPoint = this.start;
        float interval = (float) 1 / amount;
        // Start adding from the first non-starting point, since prevPoint == start
        for (int i = 1; i <= amount; i++) {
            Vector3f point = compute(interval * i);
            sumDistance += point.distance(prevPoint);
            prevPoint = point;
        }
        return sumDistance;
    }

    @Override
    public Vector3f compute(float t) {
        return this.deCasteljau(t, this.deCasteljauPoints);
    }

    private Vector3f deCasteljau(float t, List<Vector3f> controls) {
        if (controls.size() == 1) return controls.getFirst();
        LinkedList<Vector3f> new_points = new LinkedList<>();
        for (int i = 0; i < controls.size() - 1; i++) {
            Vector3f curr = controls.get(i);
            Vector3f next = controls.get(i + 1);
            float x = (1 - t) * curr.x + t * next.x;
            float y = (1 - t) * curr.y + t * next.y;
            float z = (1 - t) * curr.z + t * next.z;
            new_points.add(new Vector3f(x, y, z));
        }
        return this.deCasteljau(t, new_points);
    }
}
