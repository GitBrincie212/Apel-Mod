package net.mcbrincie.apel.lib.objects;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ParticleTriangleTest {

    @Test
    void testValidTriangles() {
        // Triangle with point at origin, one-point on y-axis, and one point on x-axis
        ParticleTriangle.builder()
                .vertex1(new Vector3f(0))
                .vertex2(new Vector3f(0, 1, 0))
                .vertex3(new Vector3f(1, 0, 0))
                .build();

        // Triangle with less obvious points that are OK (this broke before the code fix)
        ParticleTriangle.builder()
                .vertex1(new Vector3f(2f))
                .vertex2(new Vector3f(3f))
                .vertex3(new Vector3f(1f, 4f, 6f))
                .build();
    }

    @Test
    void testCollinearPointsAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> ParticleTriangle.builder()
                .vertex1(new Vector3f(0))
                .vertex2(new Vector3f(1))
                .vertex3(new Vector3f(4))
                .build());
    }
}