package net.mcbrincie.apel.lib.objects;

import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ParticleTetrahedronTest {
    // Use this to prevent having to initialize all the Minecraft Server logic
    private static final ParticleEffect NULL_PARTICLE = null;

    @Test
    void testValidTetrahedrons() {
        // Irregular tetrahedron, should work
        Vector3f vertex1 = new Vector3f(-2, 2, 1);
        Vector3f vertex2 = new Vector3f(2, 2, 1);
        Vector3f vertex3 = new Vector3f(0, 2, 5);
        Vector3f vertex4 = new Vector3f(1, 7, 4);
        new ParticleTetrahedron(NULL_PARTICLE, new Vector3f[]{vertex1, vertex2, vertex3, vertex4}, 100);

        // All three base points at 3-4-5 triangle distances from origin, fourth point above, should work
        Vector3f vertex5 = new Vector3f(-2.f, 2, -1.5f);
        Vector3f vertex6 = new Vector3f(2.f, 2, -1.5f);
        Vector3f vertex7 = new Vector3f(0.f, 2, 2.5f);
        Vector3f vertex8 = new Vector3f(0, 7, 0);
        new ParticleTetrahedron(NULL_PARTICLE, new Vector3f[]{vertex5, vertex6, vertex7, vertex8}, 100);
    }

    @Test
    void testInvalidTetrahedron() {
        // Four points of a square on the y=0 plane
        Vector3f vertex1 = new Vector3f(1.f, 0, -1f);
        Vector3f vertex2 = new Vector3f(1.f, 0, 1f);
        Vector3f vertex3 = new Vector3f(-1.f, 0, 1f);
        Vector3f vertex4 = new Vector3f(-1.f, 0, -1.f);
        assertThrows(IllegalArgumentException.class, () -> {
            new ParticleTetrahedron(NULL_PARTICLE, new Vector3f[]{vertex1, vertex2, vertex3, vertex4}, 100);
        });
    }
}