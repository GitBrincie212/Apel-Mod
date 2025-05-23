package net.mcbrincie.apel.lib.util;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import org.joml.Vector3f;

import java.util.function.Function;

@SuppressWarnings("unused")
public class ApelUtils {
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

    /**
     * Extracts the value from an easing curve if it is constant.
     * Otherwise, there will be a fallback function applied to the easing curve and
     * returned to the developer
     *
     * @param curve The easing curve to extract from
     * @param fallbackFunction The fallback function to apply, in case there it is not a constant easing curve
     * @return Either the constant value or the fallback function applied
     * @param <T> The type of the easing curve to be extracted from
     */
    public static <T> T getValueFromEasingCurve(EasingCurve<T> curve, Function<EasingCurve<T>, T> fallbackFunction) {
        if (curve instanceof ConstantEasingCurve<T> constantEasingCurve) {
            return constantEasingCurve.getStart();
        }
        return fallbackFunction.apply(curve);
    }

    /**
     * Extracts the value from an easing curve if it is constant.
     * Otherwise, there will be a fallback value returned to the developer instead
     *
     * @param curve The easing curve to extract from
     * @param fallbackValue The fallback value in case it is not a constant easing curve
     * @return Either the constant value or the fallback value
     * @param <T> The type of the easing curve to be extracted from
     */
    public static <T> T getValueFromEasingCurve(EasingCurve<T> curve, T fallbackValue) {
        return ApelUtils.getValueFromEasingCurve(curve, (Function<EasingCurve<T>, T>) (_curve) -> fallbackValue);
    }

    /**
     * Extracts the value from an easing curve if it is constant.
     * Otherwise, it will return null
     *
     * @param curve The easing curve to extract from
     * @return Either the constant value or null if it can't be extracted
     * @param <T> The type of the easing curve to be extracted from
     */
    public static <T> T getValueFromEasingCurve(EasingCurve<T> curve) {
        return ApelUtils.getValueFromEasingCurve(curve, (Function<EasingCurve<T>, T>) (_curve) -> null);
    }
}
