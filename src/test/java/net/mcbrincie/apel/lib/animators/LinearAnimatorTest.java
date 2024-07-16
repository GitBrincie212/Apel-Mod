package net.mcbrincie.apel.lib.animators;

import net.mcbrincie.apel.lib.objects.ParticlePoint;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinearAnimatorTest {
    // null particle to avoid needing to load Minecraft
    private static final ParticlePoint POINT_WITH_NULL_PARTICLE = ParticlePoint.builder().particleEffect(null).build();

    @Test
    void testGetDistance() {
        // Given a LinearAnimator with a rendering interval
        LinearAnimator linearAnimator = new LinearAnimator(1, new Vector3f[]{
                new Vector3f(10, 0, 0), new Vector3f(-10, 0, 0), new Vector3f(9, 0, 0), new Vector3f(-9, 0, 0),
        }, POINT_WITH_NULL_PARTICLE, .04f);

        // When the distance is computed
        float distance = linearAnimator.getDistance();

        // Then it is 57: (10 to -10) + (-10 to 9) + (9 to -9), or 20 + 19 + 18 == 57.
        assertEquals(57f, distance);
    }

    @Test
    void testConvertIntervalToSteps() {
        // Given a LinearAnimator with a rendering interval
        LinearAnimator linearAnimator = new LinearAnimator(1, new Vector3f[]{
                new Vector3f(10, 0, 0), new Vector3f(-10, 0, 0), new Vector3f(9, 0, 0), new Vector3f(-9, 0, 0),
        }, POINT_WITH_NULL_PARTICLE, .04f);

        // When the distance is computed
        int steps = linearAnimator.convertIntervalToSteps();

        // Then it is 1425: (10 to -10) + (-10 to 9) + (9 to -9), or 20 + 19 + 18 == 57 / .04 == 1425.
        assertEquals(1425, steps);
    }

    @Test
    void testConvertIntervalToStepsWithUniqueIntervals() {
        // Given a LinearAnimator with a rendering interval
        LinearAnimator linearAnimator = new LinearAnimator(1, new Vector3f[]{
                new Vector3f(10, 0, 0), new Vector3f(-10, 0, 0), new Vector3f(9, 0, 0), new Vector3f(-9, 0, 0),
        }, POINT_WITH_NULL_PARTICLE, new float[]{.04f, .1f, .5f});

        // When the distance is computed
        int steps = linearAnimator.convertIntervalToSteps();

        // Then it is 726:
        // (10 to -10) * 25 == 500
        // (-10 to 9) * 10 == 190
        // (9 to -9) * 2 == 36
        assertEquals(726, steps);
    }

    @Test
    void testScheduleGetAmount() {
        // Given a LinearAnimator with a rendering step count
        LinearAnimator linearAnimator = new LinearAnimator(1, new Vector3f[]{
                new Vector3f(10, 0, 0), new Vector3f(-10, 0, 0), new Vector3f(9, 0, 0), new Vector3f(-9, 0, 0),
        }, POINT_WITH_NULL_PARTICLE, 400);

        // When the total step count is requested by the scheduler allocation
        int steps = linearAnimator.scheduleGetAmount();

        // Then it is 400 per segment, or 1200 total
        assertEquals(1200, steps);
    }

    @Test
    void testScheduleGetAmountWithUniqueStepCounts() {
        // Given a LinearAnimator with a rendering step count
        LinearAnimator linearAnimator = new LinearAnimator(1, new Vector3f[]{
                new Vector3f(10, 0, 0), new Vector3f(-10, 0, 0), new Vector3f(9, 0, 0), new Vector3f(-9, 0, 0),
        }, POINT_WITH_NULL_PARTICLE, new int[]{400, 200, 100});

        // When the total step count is requested by the scheduler allocation
        int steps = linearAnimator.scheduleGetAmount();

        // Then it is the sum of the values
        assertEquals(700, steps);
    }
}