package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.ComputedEasingRPO;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ParticleTetrahedronTest {
    // Use this to prevent having to initialize all the Minecraft Server logic
    private static final ServerWorld NULL_WORLD = null;

    @Test
    void testValidTetrahedrons() {
        // Irregular tetrahedron, should work
        ParticleTetrahedron.builder()
                .vertex1(new Vector3f(-2, 2, 1))
                .vertex2(new Vector3f(2, 2, 1))
                .vertex3(new Vector3f(0, 2, 5))
                .vertex4(new Vector3f(1, 7, 4))
                .build();

        // All three base points at 3-4-5 triangle distances from origin, fourth point above, should work
        ParticleTetrahedron.builder()
                .vertex1(new Vector3f(-2.f, 2, -1.5f))
                .vertex2(new Vector3f(2.f, 2, -1.5f))
                .vertex3(new Vector3f(0.f, 2, 2.5f))
                .vertex4(new Vector3f(0, 7, 0))
                .build();
    }

    @Test
    void testTetrahedronWithCoplanarVertices() {
        // Four points of a square on the y=0 plane -- does not work because they're all co-planar
        assertThrows(IllegalArgumentException.class,
                () -> ParticleTetrahedron.builder()
                        .vertex1(new Vector3f(1.f, 0, -1f))
                        .vertex2(new Vector3f(1.f, 0, 1f))
                        .vertex3(new Vector3f(-1.f, 0, 1f))
                        .vertex4(new Vector3f(-1.f, 0, -1.f))
                        .build().doDraw(ApelServerRenderer.create(NULL_WORLD), 0, new Vector3f(0), 0, 0));
    }
}