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
}
