package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.objects.ParticlePoint;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinearAnimatorTest {
    // null particle to avoid needing to load Minecraft
    private static final ParticlePoint POINT_WITH_NULL_PARTICLE = ParticlePoint.builder().particleEffect(null).build();

    @Test
    void testConvertIntervalToSteps() {
        // Given a LinearAnimator with a rendering interval
        LinearAnimator linearAnimator = LinearAnimator.builder().delay(1).endpoints(
                List.of(new Vector3f(10, 0, 0), new Vector3f(-10, 0, 0), new Vector3f(9, 0, 0), new Vector3f(-9, 0, 0)))
                .particleObject(POINT_WITH_NULL_PARTICLE).intervalForAllSegments(.04f).build();

        // When the distance is computed
        int steps = linearAnimator.convertIntervalToSteps();

        // Then it is 1425: (10 to -10) + (-10 to 9) + (9 to -9), or 20 + 19 + 18 == 57 / .04 == 1425.
        assertEquals(1425, steps);
    }

    @Test
    void testConvertIntervalToStepsWithUniqueIntervals() {
        // Given a LinearAnimator with a rendering interval
        LinearAnimator linearAnimator = LinearAnimator.builder().delay(1).endpoints(
                        List.of(new Vector3f(10, 0, 0), new Vector3f(-10, 0, 0), new Vector3f(9, 0, 0), new Vector3f(-9, 0, 0)))
                .particleObject(POINT_WITH_NULL_PARTICLE).intervalsForSegments(List.of(.04f, .1f, .5f)).build();

        // When the distance is computed
        int steps = linearAnimator.convertIntervalToSteps();

        // Then it is 726:
        // (10 to -10) * 25 == 500
        // (-10 to 9) * 10 == 190
        // (9 to -9) * 2 == 36
        assertEquals(726, steps);
    }
}