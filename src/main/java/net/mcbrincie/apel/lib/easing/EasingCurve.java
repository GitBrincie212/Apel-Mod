package net.mcbrincie.apel.lib.easing;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.*;

import java.lang.Math;

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
 * The {@link EasingCurve} class has multiple compute methods such as {@code compute(float t)},
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
 * <b>Note:</b> Stateless easing curves do not have an EasingType. See {@link StatefulEasingCurve}
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings("unused")
public abstract class EasingCurve<T> {
    protected T start;
    protected T end;
    protected float easeProgressFactor;


    public EasingCurve(T start, T end) {
        this.start = start;
        this.end = end;
        this.easeProgressFactor = 0;
    }

    /** Gets the starting point of the easing curve
     *
     * @return The starting point
     */
    public final T getStart() {
        return this.start;
    }

    /** Gets the ending point of the easing curve
     *
     * @return The ending point
     */
    public final T getEnd() {
        return this.start;
    }

    /** Gets the easing progress factor. This acts as an amplifier for
     * the t value and is a percentage. Positive percentages amplify the t
     * value whereas negative ones lower the t value
     *
     * @return The easing progress factor
     */
    public float getEaseProgressFactor() {
        return this.easeProgressFactor;
    }

    /** Sets the easing progress factor. This acts as an amplifier for
     * the t value and is a percentage. Positive percentages amplify the t
     * value whereas negative ones lower the t value
     *
     * @param newEasingProgress the new easing progress factor
     * @return The previous easing progress factor
     */
    public float setEaseProgressFactor(float newEasingProgress) {
        float prevEasingProgress= this.easeProgressFactor;
        this.easeProgressFactor = newEasingProgress;
        return prevEasingProgress;
    }

    /** Compute the easing curve and return back the computed value. Regardless of
     * any dimension. The same t value will be used in all
     *
     * @param t The "t" parameter for the easing curve
     * @return The computed value
     */
    public final T compute(float t) {
        return compute(new Vector3f(t));
    }

    /** Compute the easing curve and return back the computed value. However, each
     * field will use the corresponded field of the 2D Vector t
     *
     * @param t The 2D Vector "t" parameter for the easing curve
     * @return The computed value
     */
    public final T compute(Vector2f t) {
        if (this.start instanceof Vector3f
                || this.start instanceof Vector3i
                || this.start instanceof Vector3d
                || this.start instanceof Vector3L
                || this.start instanceof Vec3d
                || this.start instanceof Vec3i
        ) {
            throw new RuntimeException("Cannot supply a 2D Vector t parameter into computing a 3D Vector");
        }
        return compute(new Vector3f(t.x, t.y, 0));
    }

    /** Compute the easing curve and return back the computed value. However, each
     * field will use the corresponded field of the 3D Vector t
     *
     * @param t The 3D Vector "t" parameter for the easing curve
     * @return The computed value
     */
    @SuppressWarnings("unchecked")
    public final T compute(Vector3f t) {
        t = new Vector3f(
                Math.clamp(t.x * (1 + this.easeProgressFactor), 0, 1),
                Math.clamp(t.y * (1 + this.easeProgressFactor), 0, 1),
                Math.clamp(t.z * (1 + this.easeProgressFactor), 0, 1)
        );
        return switch (this.start) {
            case Vector2f castedStart -> {
                Vector2f castedEnd = (Vector2f) this.end;
                yield (T) new Vector2f(
                        interpolate(castedStart.x, castedEnd.x, t.x),
                        interpolate(castedStart.y, castedEnd.y, t.y)
                );
            }

            case Vector3f castedStart -> {
                Vector3f castedEnd = (Vector3f) this.end;
                yield (T) new Vector3f(
                        interpolate(castedStart.x, castedEnd.x, t.x),
                        interpolate(castedStart.y, castedEnd.y, t.y),
                        interpolate(castedStart.z, castedEnd.z, t.z)
                );
            }

            case Vector3i castedStart -> {
                Vector3f castedEnd = (Vector3f) this.end;
                yield (T) new Vector3i(
                        Math.round(interpolate(castedStart.x, castedEnd.x, t.x)),
                        Math.round(interpolate(castedStart.y, castedEnd.y, t.y)),
                        Math.round(interpolate(castedStart.z, castedEnd.z, t.z))
                );
            }

            case Vector2i castedStart -> {
                Vector2i castedEnd = (Vector2i) this.end;
                yield (T) new Vector2i(
                        Math.round(interpolate(castedStart.x, castedEnd.x, t.x)),
                        Math.round(interpolate(castedStart.y, castedEnd.y, t.y))
                );
            }

            case Vec3i castedStart -> {
                Vec3i castedEnd = (Vec3i) this.end;
                yield (T) new Vec3i(
                        Math.round(interpolate(castedStart.getX(), castedEnd.getX(), t.x)),
                        Math.round(interpolate(castedStart.getY(), castedEnd.getY(), t.y)),
                        Math.round(interpolate(castedStart.getZ(), castedEnd.getZ(), t.z))
                );
            }

            case Vec3d castedStart -> {
                Vec3d castedEnd = (Vec3d) this.end;
                yield (T) new Vec3d(
                        interpolate((float) castedStart.x, (float) castedEnd.x, t.x),
                        interpolate((float) castedStart.y, (float) castedEnd.y, t.y),
                        interpolate((float) castedStart.z, (float) castedEnd.z, t.z)
                );
            }

            case Vector2L castedStart -> {
                Vector2L castedEnd = (Vector2L) this.end;
                yield (T) new Vector2i(
                        Math.round(interpolate(castedStart.x, castedEnd.x, t.x)),
                        Math.round(interpolate(castedStart.y, castedEnd.y, t.y))
                );
            }

            case Vector3L castedStart -> {
                Vector3L castedEnd = (Vector3L) this.end;
                yield (T) new Vector3i(
                        Math.round(interpolate(castedStart.x, castedEnd.x, t.x)),
                        Math.round(interpolate(castedStart.y, castedEnd.y, t.y)),
                        Math.round(interpolate(castedStart.z, castedEnd.z, t.z))
                );
            }

            case Vector2d castedStart -> {
                Vector2d castedEnd = (Vector2d) this.end;
                yield (T) new Vector2i(
                        Math.round(interpolate((float) castedStart.x, (float) castedEnd.x, t.x)),
                        Math.round(interpolate((float) castedStart.y, (float) castedEnd.y, t.y))
                );
            }

            case Vector3d castedStart -> {
                Vector3d castedEnd = (Vector3d) this.end;
                yield (T) new Vector3d(
                        interpolate((float) castedStart.x, (float) castedEnd.x, t.x),
                        interpolate((float) castedStart.y, (float) castedEnd.y, t.y),
                        interpolate((float) castedStart.z, (float) castedEnd.z, t.z)
                );
            }

            case Float castedStart -> {
                float castedEnd = (float) this.end;
                yield (T) (Float) interpolate(castedStart, castedEnd, t.x);
            }

            case Integer castedStart -> {
                int castedEnd = (int) this.end;
                yield (T) (Integer) Math.round(interpolate(castedStart, castedEnd, t.x));
            }

            case Long castedStart -> {
                long castedEnd = (long) this.end;
                yield (T) (Long) (long) Math.round(interpolate(castedStart, castedEnd, t.x));
            }

            case Double castedStart -> {
                float castedEnd = (float) this.end;
                yield (T) (Float) interpolate(castedStart.floatValue(), castedEnd, t.x);
            }

            default -> throw new RuntimeException("This type is not supported for the easing curve computing");
        };
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
