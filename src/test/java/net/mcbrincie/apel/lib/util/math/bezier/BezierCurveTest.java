package net.mcbrincie.apel.lib.util.math.bezier;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BezierCurveTest {

    @Test
    void testHashCode_withEmptyVectors() {
        BezierCurve linear1 = BezierCurve.of(new Vector3f(), new Vector3f(), List.of());
        BezierCurve linear2 = BezierCurve.of(new Vector3f(), new Vector3f(), List.of());
        BezierCurve quadratic1 = BezierCurve.of(new Vector3f(), new Vector3f(), List.of(new Vector3f()));
        BezierCurve quadratic2 = BezierCurve.of(new Vector3f(), new Vector3f(), List.of(new Vector3f()));
        BezierCurve cubic1 = BezierCurve.of(new Vector3f(), new Vector3f(), List.of(new Vector3f(), new Vector3f()));
        BezierCurve cubic2 = BezierCurve.of(new Vector3f(), new Vector3f(), List.of(new Vector3f(), new Vector3f()));

        assertEquals(linear1.hashCode(), linear2.hashCode());
        assertEquals(quadratic1.hashCode(), quadratic2.hashCode());
        assertEquals(cubic1.hashCode(), cubic2.hashCode());
    }

    @Test
    void testHashCode_withPopulatedVectors() {

        BezierCurve bc1 = BezierCurve.of(new Vector3f(), new Vector3f(0.0f, 5.0f, 0.0f),
                                         List.of(new Vector3f(2.0f, 0.0f, 2.0f), new Vector3f(2.0f, -1.0f, 8.0f))
        );
        BezierCurve bc2 = BezierCurve.of(new Vector3f(), new Vector3f(0.0f, 5.0f, 0.0f),
                                         List.of(new Vector3f(2.0f, 0.0f, 2.0f), new Vector3f(2.0f, -1.0f, 8.0f))
        );

        assertEquals(bc1, bc2);
        assertEquals(bc1.hashCode(), bc2.hashCode());
    }

    @Test
    void testCubicBezierPoints() {
        // Given a cubic Bézier curve
        BezierCurve bc = BezierCurve.of(new Vector3f(0, 0, 5), new Vector3f(0, 5, 5),
                                        List.of(new Vector3f(3, 2, 8), new Vector3f(-3, 3, 8))
        );

        // Verify points are within one-thousandth
        assertTrue(new Vector3f(0f, 0f, 5f).equals(bc.compute(0.0f), 0.001f));
        assertTrue(new Vector3f(0.648f, 0.572f, 5.810f).equals(bc.compute(0.1f), 0.001f));
        assertTrue(new Vector3f(0.864f, 1.096f, 6.44f).equals(bc.compute(0.2f), 0.001f));
        assertTrue(new Vector3f(0.756f, 1.584f, 6.890f).equals(bc.compute(0.3f), 0.001f));
        assertTrue(new Vector3f(0.432f, 2.048f, 7.160f).equals(bc.compute(0.4f), 0.001f));
        assertTrue(new Vector3f(0.0f, 2.5f, 7.25f).equals(bc.compute(0.5f), 0.001f));
        assertTrue(new Vector3f(-0.432f, 2.952f, 7.160f).equals(bc.compute(0.6f), 0.001f));
        assertTrue(new Vector3f(-0.756f, 3.416f, 6.890f).equals(bc.compute(0.7f), 0.001f));
        assertTrue(new Vector3f(-0.864f, 3.904f, 6.440f).equals(bc.compute(0.8f), 0.001f));
        assertTrue(new Vector3f(-0.648f, 4.428f, 5.810f).equals(bc.compute(0.9f), 0.001f));
        assertTrue(new Vector3f(0.0f, 5.0f, 5.0f).equals(bc.compute(1.0f), 0.001f));
    }
}