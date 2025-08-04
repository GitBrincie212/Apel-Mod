package net.mcbrincie.apel.lib.easing;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.*;

/** This is the base class for {@link StatefulEasingCurve}. It provides a lot of methods for interpolating
 * between a starting value and an ending value. Classes can extend from this {@link StatefulEasingCurve} and won't
 * have to deal with the other methods. They simply provide the math logic for the {@code interpolate(float t)}
 * method, and this base class takes care of translating it into the other data types. Supported datatypes are:
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
 * The {@link StatefulEasingCurve} class has 2 primary methods for computing values using the easing curve. The first is by
 * using {@code compute(t)} which takes a t parameter and spits out a newly computed t parameter based on the implementation
 * of the easing curve. The Compute methods are a unified way of handling scalar and vector values; ideally they shouldn't be
 * overridden. The compute methods do not take into account the start and end parameters. The other method is {@code getValue(t)},
 * unlike compute method it does take into account the start and end parameters. It first calculates the t value using the
 * aforementioned compute methods and then linearly interpolates(lerp) between the starting and ending values to create
 * a new intermediate value to then return
 * </p>
 * <p>
 * When using either of the 2 methods, you can also supply a vector value as t, which returns a new vector with the
 * computed results. You can have up to 3D vectors (because of the types supported). <u>Do note however that if you decide
 * to supply a {@link Vector2f} and the start and end happen to not have the same dimensions. It will error out</u>
 * </p>
 * <p>
 * Lastly, providing any higher dimensions of t, when the start and end values have a lower dimension, will only use
 * the corresponding values to compute and not the higher dimensional values. This type of easing curve also has an EaseType
 * to control the shape of the easing curve which can be either<br>
 * - <b>Ease In</b> (Grows Slowly -> Then it ramps up the speed)<br>
 * - <b>Ease Out</b> (Grows Fast -> Then it slows down)<br>
 * - <b>Ease In Out</b> (Grows Slowly -> then it ramps up a bit of speed -> then slows down again)
 * </p>
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public abstract class StatefulEasingCurve<T> extends EasingCurve<T> {
    protected EaseType easeType;


    public StatefulEasingCurve(T start, T end, EaseType easeType, float easingProgressFactor) {
        super(start, end, easingProgressFactor);
        this.easeType = easeType;
    }

    public StatefulEasingCurve(T start, T end, EaseType easeType) {
        this(start, end, easeType, 0);
    }


    /** This is the interpolate method. It is the implementation of all the easing logic,
     * a t value. It then returns the computed intermediate value based on the start and end
     * as well as the provided t parameter
     *
     * @param t The time value(t)
     * @return The computed value out of all the 3 parameters from the implementation
     */
    protected abstract float interpolate(float t);
}
