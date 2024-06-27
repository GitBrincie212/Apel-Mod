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
}