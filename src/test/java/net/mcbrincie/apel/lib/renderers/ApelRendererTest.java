package net.mcbrincie.apel.lib.renderers;

import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApelRendererTest {

    private static final Vector3f IGNORED_OFFSET = new Vector3f();
    private static final Vector3f IGNORED_ROTATION = new Vector3f();

    @Test
    void testBezierCurve_hashCodes() {

        BezierCurve bc1 = BezierCurve.of(new Vector3f(), new Vector3f(0.0f, 5.0f, 0.0f),
                                         List.of(new Vector3f(2.0f, 0.0f, 2.0f), new Vector3f(2.0f, -1.0f, 8.0f))
        );
        ApelRenderer.BezierCurve curve1 = new ApelRenderer.BezierCurve(IGNORED_OFFSET, bc1, IGNORED_ROTATION, 100);
        BezierCurve bc2 = BezierCurve.of(new Vector3f(), new Vector3f(0.0f, 5.0f, 0.0f),
                                         List.of(new Vector3f(2.0f, 0.0f, 2.0f), new Vector3f(2.0f, -1.0f, 8.0f))
        );
        ApelRenderer.BezierCurve curve2 = new ApelRenderer.BezierCurve(IGNORED_OFFSET, bc2, IGNORED_ROTATION, 100);

        assertEquals(curve1, curve2);
        assertEquals(curve1.hashCode(), curve2.hashCode());
    }
}