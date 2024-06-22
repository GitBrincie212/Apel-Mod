package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.Apel;
import org.joml.Math;

/** The trigonometry table is for performance reasons, it sacrifices some accuracy & memory over speed.
 * It precomputes sin, cos and arc cosine functions for angles. Size is a parameter that dictates the precision of
 * the trigonometry functions
 */
@SuppressWarnings("unused")
public class TrigTable {
    private final float[] sinTable;
    private final float[] cosineTable;
    private final float[] arcCosineTable;
    private static final float TAU = 6.28318530f;
    public final float tableStep;
    private final int size;

    public TrigTable(int size) {
        this.size = size;
        this.tableStep = size / TAU;
        this.sinTable = new float[size];
        this.cosineTable = new float[size];
        this.arcCosineTable = new float[size];
    }

    public void generate() {
        float angle;
        float interval = TAU / this.size;
        for (int i = 0; i < this.size; i++) {
            angle = i * interval;
            this.sinTable[i] = Math.sin(angle);
            this.cosineTable[i] = Math.cos(angle);
            this.arcCosineTable[i] = Math.acos(angle);
        }
        Apel.LOGGER.info("Trigonometry Table Finished With Size {}", this.size);
    }

    public float getSine(float angle) {
        return this.sinTable[this.normalizeIndex(angle)];
    }

    public float getCosine(float angle) {
        return this.cosineTable[this.normalizeIndex(angle)];
    }

    public float getArcCosine(float angle) {
        return this.arcCosineTable[this.normalizeIndex(angle)];
    }

    private int normalizeIndex(float radians) {
        return (int) ((radians % TAU) * this.tableStep);
    }
}
