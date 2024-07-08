package net.mcbrincie.apel.lib.objects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParticleCombinerTest {
    // Use this to prevent having to initialize all the Minecraft Server logic
    private static final ParticlePoint.Builder<?> NULL_POINT_BUILDER = ParticlePoint.builder().particleEffect(null);

    @Test
    void setObjectsViaList() {
        // Given a couple ParticlePoints
        ParticlePoint p1 = NULL_POINT_BUILDER.build();
        ParticlePoint p2 = NULL_POINT_BUILDER.build();

        // Given a ParticleCombiner
        ParticleCombiner<ParticlePoint> combiner = new ParticleCombiner<>(p1, p2);

        // Given points to append
        ParticlePoint p3 = NULL_POINT_BUILDER.build();
        ParticlePoint p4 = NULL_POINT_BUILDER.build();

        // When the points are set
        combiner.setObjects(List.of(p3, p4));
        // Then the combiner has two objects
        assertEquals(2, combiner.getObjects().size());
    }

    @Test
    void setObjectsLeavesAppendFunctional() {
        // Given a couple ParticlePoints
        ParticlePoint p1 = NULL_POINT_BUILDER.build();
        ParticlePoint p2 = NULL_POINT_BUILDER.build();

        // Given a ParticleCombiner
        ParticleCombiner<ParticlePoint> combiner = new ParticleCombiner<>(p1, p2);

        // Given a point to append
        ParticlePoint p3 = NULL_POINT_BUILDER.build();

        // When the points are set
        combiner.setObjects(List.of(p1, p2));
        combiner.appendObject(p3);

        // Then the combiner has three objects
        assertEquals(3, combiner.getObjects().size());
    }

    @Test
    void appendObjects() {
        // Given a couple ParticlePoints
        ParticlePoint p1 = NULL_POINT_BUILDER.build();
        ParticlePoint p2 = NULL_POINT_BUILDER.build();

        // Given a ParticleCombiner
        ParticleCombiner<ParticlePoint> combiner = new ParticleCombiner<>(p1, p2);

        // Given points to append
        ParticlePoint p3 = NULL_POINT_BUILDER.build();
        ParticlePoint p4 = NULL_POINT_BUILDER.build();

        // When the points are appended
        combiner.appendObjects(p3, p4);

        // Then the combiner has four objects
        assertEquals(4, combiner.getObjects().size());
    }
}