package net.mcbrincie.apel.lib.util.math;

import org.junit.jupiter.api.Test;

import static net.mcbrincie.apel.lib.renderers.ApelRenderer.trigTable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TrigTableTest {

    private static final float EPSILON = 1e-2f;
    private static final int TRIG_TABLE_SIZE = 500;
    private final TrigTable tt = new TrigTable(TRIG_TABLE_SIZE);

    @Test
    void getSine() {
        // When various common values are calculated
        assertEquals(0.0f, tt.getSine(0), EPSILON);
        assertEquals(0.5f, tt.getSine((float) (Math.PI / 6f)), EPSILON);
        assertEquals(1.0f / Math.sqrt(2), tt.getSine((float) (Math.PI / 4f)), EPSILON);
        assertEquals(Math.sqrt(3) / 2.0f, tt.getSine((float) (Math.PI / 3f)), EPSILON);
        assertEquals(1.0f, tt.getSine((float) (Math.PI / 2f)), EPSILON);
        assertEquals(Math.sqrt(3) / 2.0f, tt.getSine((float) (2f * Math.PI / 3f)), EPSILON);
        assertEquals(1.0f / Math.sqrt(2), tt.getSine((float) (3f * Math.PI / 4f)), EPSILON);
        assertEquals(0.5f, tt.getSine((float) (5f * Math.PI / 6f)), EPSILON);
        assertEquals(0.0f, tt.getSine((float) Math.PI), EPSILON);
        assertEquals(-1.0f, tt.getSine((float) (3f * Math.PI / 2f)), EPSILON);
        assertEquals(0.0f, tt.getSine((float) (2f * Math.PI)), EPSILON);

        // Verify all discretized values are close to their actual values
        float interval = (float) (Math.TAU / TRIG_TABLE_SIZE);
        for (int i = 0; i < TRIG_TABLE_SIZE; i++) {
            float angle = i * interval;
            assertEquals(Math.sin(angle), tt.getSine(angle), EPSILON);
        }
    }

    @Test
    void getCosine() {
        // When various common values are calculated
        assertEquals(1.0f, tt.getCosine(0f), EPSILON);
        assertEquals(Math.sqrt(3) / 2.0f, tt.getCosine((float) (Math.PI / 6f)), EPSILON);
        assertEquals(1.0f / Math.sqrt(2), tt.getCosine((float) (Math.PI / 4f)), EPSILON);
        assertEquals(0.5f, tt.getCosine((float) (Math.PI / 3f)), EPSILON);
        assertEquals(0.0f, tt.getCosine((float) (Math.PI / 2f)), EPSILON);
        assertEquals(-0.5f, tt.getCosine((float) (2f * Math.PI / 3f)), EPSILON);
        assertEquals(-1.0f / Math.sqrt(2), tt.getCosine((float) (3f * Math.PI / 4f)), EPSILON);
        assertEquals(-Math.sqrt(3) / 2.0f, tt.getCosine((float) (5f * Math.PI / 6f)), EPSILON);
        assertEquals(-1.0f, tt.getCosine((float) Math.PI), EPSILON);
        assertEquals(0.0f, tt.getCosine((float) (3f * Math.PI / 2f)), EPSILON);
        assertEquals(1.0f, tt.getCosine((float) (2f * Math.PI)), EPSILON);

        // Verify all discretized values are close to their actual values
        float interval = (float) (Math.TAU / TRIG_TABLE_SIZE);
        for (int i = 0; i < TRIG_TABLE_SIZE; i++) {
            float angle = i * interval;
            assertEquals(Math.cos(angle), tt.getCosine(angle), EPSILON);
        }
    }

    @Test
    void getArcCosine() {
        // When various common values are calculated
        assertEquals((float) Math.PI, tt.getArcCosine(-1.0f), EPSILON);
        assertEquals((float) (5f * Math.PI / 6f), tt.getArcCosine((float) (-Math.sqrt(3) / 2.0f)), EPSILON);
        assertEquals((float) (3f * Math.PI / 4f), tt.getArcCosine((float) (-1.0f / Math.sqrt(2))), EPSILON);
        assertEquals((float) (2f * Math.PI / 3f), tt.getArcCosine(-0.5f), EPSILON);
        assertEquals((float) (Math.PI / 2f), tt.getArcCosine(0.0f), EPSILON);
        assertEquals((float) (Math.PI / 3f), tt.getArcCosine(0.5f), EPSILON);
        assertEquals((float) (Math.PI / 4f), tt.getArcCosine((float) (1.0f / Math.sqrt(2))), EPSILON);
        assertEquals((float) (Math.PI / 6f), tt.getArcCosine((float) (Math.sqrt(3) / 2.0f)), EPSILON);
        assertEquals(0f, tt.getArcCosine(1.0f), EPSILON);

        // Verify all discretized values are close to their actual values
        float interval = 2f / (TRIG_TABLE_SIZE - 1);
        for (int i = 0; i < TRIG_TABLE_SIZE; i++) {
            float value = -1 + (i * interval);
            assertEquals(Math.acos(value), tt.getArcCosine(value), EPSILON);
        }
    }

    @Test
    void testBoundaries() {
        float result = 0f;
        final double sqrt5Plus1 = 3.23606;
        int amount = 10000000;
        for (int i = 0; i < amount; i++) {
            float k = i + .5f;
            float phi = trigTable.getArcCosine(1f - ((2f * k) / amount));
            float theta = (float) (Math.PI * k * sqrt5Plus1);
            float sinPhi = trigTable.getSine(phi);
            float x = trigTable.getCosine(theta) * sinPhi;
            float y = trigTable.getSine(theta) * sinPhi;
            float z = trigTable.getCosine(phi);
            result = (result + x + y + z) % 10;
        }
        // Do something with `result` so it isn't optimized away
        assertNotEquals(-100f, result);
    }
}