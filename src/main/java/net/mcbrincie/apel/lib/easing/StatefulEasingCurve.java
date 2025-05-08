package net.mcbrincie.apel.lib.easing;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.*;

/** This is the base class for StatelessEasingCurve. It provides a lot of overloaded methods for interpolating
 * between a starting value and an ending value. Classes can extend from this StatelessEasingCurve and won't
 * have to deal with the other overloaded methods. They simply provide the math logic for the floating number overload,
 * and this base class takes care of translating it into the other data types. Supported datatypes are:
 * <p>
 * - {@link Integer}<br>
 * - {@link Double}<br>
 * - {@link Float}<br>
 * - {@link Long}<br>
 * - {@link Vector3i}<br>
 * - {@link Vector3f}<br>
 * - {@link Vector3d}<br>
 * - {@link Vec3d}<br>
 * - {@link Vec3i}<br>
 * - {@link Vector3L}<br>
 * - {@link Vector2i}<br>
 * - {@link Vector2f}<br>
 * - {@link Vector2d}<br>
 * - {@link Vector2L}<br>
 * <p>
 * The {@link StatefulEasingCurve} class has multiple compute methods such as {@code compute(float t)},
 * {@code computeVec2(Vector2f t)}, {@code computeVec3(float t)}... and so on. These compute methods are
 * meant to be used by outside developers. Depending on the vector's size or if it is just a number, there will
 * be available certain compute methods to use. Each compute method computes the dimension requested and returns
 * a value that has the same dimensions as the compute's method name suggests
 * </p>
 * <p>
 * You can supply different t values for each dimension or one t value for all dimensions when using a
 * compute method. All the Compute methods call internally {@code interporlate(float x, float y, float t)} which
 * is where the implementation of the easing curve lies in. <b>If you plan on inheriting from this StatelessEasingCurve. Be
 * sure to override ONLY the interpolate method</b>
 * </p>
 * <p>
 * Lastly, providing lower dimensions than what the compute method used for is built, will result in a
 * {@code RuntimeException} so be sure to know what vector type you are using and calling the appropriate
 * compute method
 * </p>
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public abstract class StatefulEasingCurve<T> extends EasingCurve<T> {
    protected EaseType easeType;


    public StatefulEasingCurve(T start, T end, EaseType easeType) {
        super(start, end);
        this.easeType = easeType;
    }


    /** This is the interpolate method. It is the implementation of all the easing logic,
     * it receives a start parameter, an end parameter and a t value. It then returns the computed value
     * based on the provided parameters
     *
     * @param a The starting value
     * @param b The ending value
     * @param t The time value(t)
     * @return The computed value out of all the 3 parameters from the implementation
     */
    protected abstract float interpolate(float a, float b, float t);
}
