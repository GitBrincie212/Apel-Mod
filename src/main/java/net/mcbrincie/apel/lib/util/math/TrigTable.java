package net.mcbrincie.apel.lib.util.math;

import net.mcbrincie.apel.Apel;
import org.joml.Math;

/**
 * The trigonometry table is for performance reasons, it sacrifices some accuracy & memory over speed.
 * It precomputes sin, cos and arc cosine functions for angles. Size is a parameter that dictates the precision of
 * the trigonometry functions
 */
@SuppressWarnings("unused")
public class TrigTable {
    private static final float TAU = 6.28318530f;

    private final float[] sinTable;
    private final float[] cosineTable;
    private final float[] arcCosineTable;
    private final float tableStep;
    private final int size;

    /**
     * Creates and generates trigonometry tables for <em>sin</em>, <em>cos</em>, and <em>cos<sup>-1</sup></em>.  It is
     * recommended to have the {@code size} be an even number so the tables are symmetrical about their midpoints.
     *
     * @param size number of entries in the tables
     */
    public TrigTable(int size) {
        this.size = size;
        this.tableStep = size / TAU;
        this.sinTable = new float[size + 1];
        this.cosineTable = new float[size + 1];
        this.arcCosineTable = new float[size + 1];
        this.generate();
    }

    private void generate() {
        float angle;
        float interval = TAU / this.size;
        // arccosine domain is [-1, 1], so make "TRIG_TABLE_SIZE" points from -1 to 1
        float arccosineInterval = 2f / this.size;
        for (int i = 0; i <= this.size; i++) {
            angle = i * interval;
            this.sinTable[i] = Math.sin(angle);
            this.cosineTable[i] = Math.cos(angle);
            this.arcCosineTable[i] = Math.acos(-1 + i * arccosineInterval);
        }
        Apel.LOGGER.info("Trigonometry Table Finished With Size {}", this.size);
    }

    /**
     * Returns the trigonometric sine of an angle.  If the argument is NaN or an infinity, then the result is NaN.
     *
     * @param angle an angle, in radians.
     * @return the sine of the argument.
     */
    public float getSine(float angle) {
        return this.sinTable[this.normalizeIndex(angle)];
    }

    /**
     * Returns the trigonometric cosine of an angle. If the argument is NaN or an infinity, then the result is NaN.
     *
     * @param angle an angle, in radians.
     * @return the cosine of the argument.
     */
    public float getCosine(float angle) {
        return this.cosineTable[this.normalizeIndex(angle)];
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the range 0.0 through <i>pi</i>.  Special case:
     * <ul>
     *     <li>If the argument is NaN or its absolute value is greater than 1, then the result is NaN.
     *     <li>If the argument is {@code 1.0}, the result is positive zero.
     * </ul>
     *
     * @param value the value whose arc cosine is to be returned.
     * @return the arc cosine of the argument.
     */
    public float getArcCosine(float value) {
        int normalizedIndex = Math.round((value + 1) * this.size / 2);
        return this.arcCosineTable[normalizedIndex];
    }

    private int normalizeIndex(float radians) {
        return Math.round((radians % TAU) * this.tableStep);
    }
}
