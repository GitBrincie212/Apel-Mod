package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParticleCombinerTest {
    // Use this to prevent having to initialize all the Minecraft Server logic
    private static final ParticlePoint.Builder<?> NULL_POINT_BUILDER = ParticlePoint.builder().particleEffect(null);
    private static final float EPSILON = 1e-3f;

    private static <T> boolean isConstantEasingOfValue(EasingCurve<T> easing, T value) {
        if (value instanceof Vector3f) {
            return easing instanceof ConstantEasingCurve<T>
                    && ((Vector3f) easing.getStart()).equals((Vector3f) easing.getEnd(), EPSILON)
                    && ((Vector3f) easing.getStart()).equals((Vector3f) value, EPSILON);
        }
        return easing instanceof ConstantEasingCurve<T>
                && easing.getStart() == easing.getEnd() && easing.getStart() == value;
    }

    @Test
    void setObjectsViaList() {
        // Given a couple ParticlePoints
        ParticlePoint p1 = NULL_POINT_BUILDER.build();
        ParticlePoint p2 = NULL_POINT_BUILDER.build();

        // Given a ParticleCombiner
        ParticleCombiner combiner = ParticleCombiner.builder().object(p1).object(p2).build();

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
        ParticleCombiner combiner = ParticleCombiner.builder().object(p1).object(p2).build();

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
        ParticleCombiner combiner = ParticleCombiner.builder().object(p1).object(p2).build();

        // Given points to append
        ParticlePoint p3 = NULL_POINT_BUILDER.build();
        ParticlePoint p4 = NULL_POINT_BUILDER.build();

        // When the points are appended
        combiner.appendObjects(List.of(p3, p4));

        // Then the combiner has four objects
        assertEquals(4, combiner.getObjects().size());
    }

    @Test
    void testSetRotations() {
        // Given a couple ParticlePoints
        ParticlePoint p1 = NULL_POINT_BUILDER.build();
        ParticlePoint p2 = NULL_POINT_BUILDER.build();
        ParticlePoint p3 = NULL_POINT_BUILDER.build();

        // Given a ParticleCombiner
        ParticleCombiner combiner = ParticleCombiner.builder().object(p1).object(p2).object(p3).build();

        // Given a rotation
        Vector3f rotation = new Vector3f(0.1f);

        // When the children's rotation is set
        combiner.setRotations(rotation);

        // Then each child object has the same rotation
        EasingCurve<Vector3f> firstChildRot = combiner.getObject(0).getRotation();
        EasingCurve<Vector3f> secondChildRot = combiner.getObject(1).getRotation();
        EasingCurve<Vector3f> thirdChildRot = combiner.getObject(2).getRotation();
        assertTrue(isConstantEasingOfValue(firstChildRot, rotation));
        assertTrue(isConstantEasingOfValue(secondChildRot, rotation));
        assertTrue(isConstantEasingOfValue(thirdChildRot, rotation));
    }

    @Test
    void testSetRotationsWithOffset() {
        // Given a couple ParticlePoints
        ParticlePoint p1 = NULL_POINT_BUILDER.build();
        ParticlePoint p2 = NULL_POINT_BUILDER.build();
        ParticlePoint p3 = NULL_POINT_BUILDER.build();

        // Given a ParticleCombiner
        ParticleCombiner combiner = ParticleCombiner.builder().object(p1).object(p2).object(p3).build();

        // Given a rotation
        Vector3f rotation = new Vector3f(0.1f);

        // When the children's rotation is set, with varying rotations per axis (to verify independence)
        combiner.setRotations(rotation, 0.1f, 0.2f, 0.3f);

        // Then each child object has the same rotation
        EasingCurve<Vector3f> firstChildRot = combiner.getObject(0).getRotation();
        EasingCurve<Vector3f> secondChildRot = combiner.getObject(1).getRotation();
        EasingCurve<Vector3f> thirdChildRot = combiner.getObject(2).getRotation();
        assertTrue(isConstantEasingOfValue(firstChildRot, rotation));
        assertTrue(isConstantEasingOfValue(secondChildRot, new Vector3f(rotation).add(0.1f, 0.2f, 0.3f)));
        assertTrue(isConstantEasingOfValue(thirdChildRot, new Vector3f(rotation).add(0.2f, 0.4f, 0.6f)));
    }
}