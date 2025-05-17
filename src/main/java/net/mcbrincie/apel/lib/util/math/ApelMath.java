package net.mcbrincie.apel.lib.util.math;

import org.joml.Vector3f;

public class ApelMath {
    /**
     * Removes full rotations from each component of the provided {@code rotation} vector such that each component
     * maintains its direction but has a magnitude in the range {@code (-2π, 0]} or {@code [0, 2π)}.  Returns a new
     * vector containing the resulting partial rotation components with the same signs as the parameter's components.
     *
     * @param rotation The existing rotation vector
     * @return A new vector with partial rotation components
     */
    public static Vector3f normalizeRotation(Vector3f rotation) {
        float x = (float) (rotation.x % Math.TAU);
        float y = (float) (rotation.y % Math.TAU);
        float z = (float) (rotation.z % Math.TAU);
        return new Vector3f(x, y, z);
    }

    /**
     * Gets from a 3D vector the axis value that corresponds to an index. For example, the x-axis
     * has an index of 0, the index value must be either 0, 1 or 2
     *
     * @param vec The vector to use
     * @param index What axis to access
     * @return The value that corresponds to the index
     */
    public static float getAxisFromIndex(Vector3f vec, int index) {
        return switch (index) {
            case 0 -> vec.x;
            case 1 -> vec.y;
            case 2 -> vec.z;
            default -> throw new IllegalArgumentException("The index value is not 1, 2 or even 3");
        };
    }
}
