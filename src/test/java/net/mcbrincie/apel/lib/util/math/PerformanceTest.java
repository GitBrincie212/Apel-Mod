package net.mcbrincie.apel.lib.util.math;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.mcbrincie.apel.lib.renderers.ApelRenderer.trigTable;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PerformanceTest {

    @Test
    void testMatrix4fTransformations() {
        Vector3f pos = new Vector3f(1.1f, 1.3f, 1.5f);
        Vector3f scale = new Vector3f(2.1f, 1.6f, 1.4f);
        Quaternionfc rotate = new Quaternionf().rotateZ((float) (Math.PI / 2))
                                               .rotateY((float) (Math.PI / 3))
                                               .rotateX((float) (Math.PI / 4));
        Vector3f offset = new Vector3f(35f, 70f, -20f);

        // Compute updated position
        var newPos = new Vector3f(pos).mul(scale).rotate(rotate).add(offset);

        // Make a matrix
        Matrix4f trs = new Matrix4f().translationRotateScale(offset, rotate, scale);

        var matrixPos = new Vector3f(pos);
        trs.transformPosition(matrixPos);

        assertEquals(newPos, matrixPos);
    }

    @Test
    void compareMatrixVsVectorOps() {
        final double sqrt5Plus1 = 3.23606;
        int amount = 1000000;
        Vector3f[] vectorOps = new Vector3f[amount];
        Vector3f[] matrixOps = new Vector3f[amount];
        for (int i = 0; i < amount; i++) {
            float k = i + .5f;
            float phi = trigTable.getArcCosine(1f - ((2f * k) / amount));
            float theta = (float) (Math.PI * k * sqrt5Plus1);
            float sinPhi = trigTable.getSine(phi);
            float x = trigTable.getCosine(theta) * sinPhi;
            float y = trigTable.getSine(theta) * sinPhi;
            float z = trigTable.getCosine(phi);
            vectorOps[i] = new Vector3f(x, y, z);
            matrixOps[i] = new Vector3f(x, y, z);
        }

        // Set up scale, rotate, translate
        Vector3f scale = new Vector3f(1.1f, 1.1f, 1.1f);
        Quaternionfc rotate = new Quaternionf().rotateZ((float) (Math.PI / 2))
                                               .rotateY((float) (Math.PI / 3))
                                               .rotateX((float) (Math.PI / 4));
        Vector3f offset = new Vector3f(3f, 7f, -2f);
        // Make a matrix combining the three
        Matrix4f trs = new Matrix4f().translationRotateScale(offset, rotate, scale);

        // Pre-run the methods once
        Vector3f pos = new Vector3f(1.1f, 1.3f, 1.5f);
        var newPos = new Vector3f(pos).mul(scale).rotate(rotate).add(offset);
        System.out.println("vector ops prewarmed: " + newPos);

        var matrixPos = new Vector3f(pos);
        trs.transformPosition(matrixPos);
        System.out.println("matrix ops prewarmed: " + matrixPos);

        final int trials = 200;
        List<Float> vectorTimes = new ArrayList<>(trials);
        List<Float> matrixTimes = new ArrayList<>(trials);
        for (int times = 0; times < trials; times++) {
            long vectorStartNanos = System.nanoTime();
            for (int j = 0; j < amount; j++) {
                // Quaternions:
                vectorOps[j].mul(scale).rotate(rotate).add(offset);
                // Euler angles:
                // vectorOps[j].mul(scale).rotateZ(1.57f).rotateY(1.04f).rotateX(0.78f).add(offset);
            }
            long vectorEndNanos = System.nanoTime();

            long matrixStartNanos = System.nanoTime();

            for (int k = 0; k < amount; k++) {
                trs.transformPosition(matrixOps[k]);
            }
            long matrixEndNanos = System.nanoTime();

            // Don't let the runtime optimize out the loops
            System.out.println(vectorOps[(int) Math.floor(Math.random() * amount)]);
            System.out.println(matrixOps[(int) Math.floor(Math.random() * amount)]);

            float v = (vectorEndNanos - vectorStartNanos) / 1e9f;
            System.out.printf("Vector ops from %d to %d took %.4f seconds %n", vectorStartNanos, vectorEndNanos, v);
            vectorTimes.add(v);
            float m = (matrixEndNanos - matrixStartNanos) / 1e9f;
            System.out.printf("Matrix ops from %d to %d took %.4f seconds %n", matrixStartNanos, matrixEndNanos, m);
            matrixTimes.add(m);
        }

        System.out.println("Vector stats: " + vectorTimes.stream().collect(Collectors.summarizingDouble(Float::doubleValue)));
        System.out.println("Matrix stats: " + matrixTimes.stream().collect(Collectors.summarizingDouble(Float::doubleValue)));
    }
}
