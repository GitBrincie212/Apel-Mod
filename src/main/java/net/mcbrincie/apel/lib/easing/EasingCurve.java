package net.mcbrincie.apel.lib.easing;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.*;

import java.lang.Math;

/** This is the base class for {@link EasingCurve}. It provides a lot of methods for interpolating
 * between a starting value and an ending value. Classes can extend from this {@link EasingCurve} and won't
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
 * The {@link EasingCurve} class has 2 primary methods for computing values using the easing curve. The first is by
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
 * the corresponding values to compute and not the higher dimensional values
 * </p>

 * <b>Note:</b> Stateless easing curves do not have an EasingType. See {@link StatefulEasingCurve}
 *
 * @param <T> The type to use for the computation (can be either a vector or a scalar value)
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class EasingCurve<T> {
    protected float easeProgressFactor;
    protected T start;
    protected T end;

    public EasingCurve(T start, T end) {
        this(start, end, 0);
    }

    public EasingCurve(T start, T end, float easeProgressFactor) {
        this.start = start;
        this.end = end;
        this.easeProgressFactor = easeProgressFactor;
    }

    public T getEnd() {
        return this.end;
    }

    public T getStart() {
        return this.start;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean isVector2D(T val) {
        return val instanceof Vector2f
                || val instanceof Vector2d
                || val instanceof Vector2i
                || val instanceof Vector2L
                || val instanceof Vec2f;
    }

    protected boolean isVector3D(T val) {
        return val instanceof Vector3f
                || val instanceof Vector3d
                || val instanceof Vector3i
                || val instanceof Vector3L
                || val instanceof Vec3i
                || val instanceof Vec3d;
    }

    @SuppressWarnings("unchecked")
    protected final T calculateUnified(T val1, T val2, UnifiedCalcFunc func) {
        return switch (val1) {
            case Vector2f castedStart -> {
                Vector2f castedEnd = (Vector2f) val2;
                yield (T) new Vector2f(
                        func.apply(castedStart.x, castedEnd.x, 0),
                        func.apply(castedStart.y, castedEnd.y, 1)
                );
            }

            case Vector3f castedStart -> {
                Vector3f castedEnd = (Vector3f) val2;
                yield (T) new Vector3f(
                        func.apply(castedStart.x, castedEnd.x, 0),
                        func.apply(castedStart.y, castedEnd.y, 1),
                        func.apply(castedStart.z, castedEnd.z, 2)
                );
            }

            case Vector3i castedStart -> {
                Vector3f castedEnd = (Vector3f) val2;
                yield (T) new Vector3i(
                        Math.round(func.apply((float) castedStart.x, castedEnd.x, 0)),
                        Math.round(func.apply((float) castedStart.y, castedEnd.y, 1)),
                        Math.round(func.apply((float) castedStart.z, castedEnd.z, 2))
                );
            }

            case Vector2i castedStart -> {
                Vector2i castedEnd = (Vector2i) val2;
                yield (T) new Vector2i(
                        Math.round(func.apply((float) castedStart.x, (float) castedEnd.x, 0)),
                        Math.round(func.apply((float) castedStart.y, (float) castedEnd.y, 1))
                );
            }

            case Vec3i castedStart -> {
                Vec3i castedEnd = (Vec3i) val2;
                yield (T) new Vec3i(
                        Math.round(func.apply((float) castedStart.getX(), (float) castedEnd.getX(), 0)),
                        Math.round(func.apply((float) castedStart.getY(), (float) castedEnd.getY(), 1)),
                        Math.round(func.apply((float) castedStart.getZ(), (float) castedEnd.getZ(), 2))
                );
            }

            case Vec3d castedStart -> {
                Vec3d castedEnd = (Vec3d) val2;
                yield (T) new Vec3d(
                        func.apply((float) castedStart.x, (float) castedEnd.x, 0),
                        func.apply((float) castedStart.y, (float) castedEnd.y, 1),
                        func.apply((float) castedStart.z, (float) castedEnd.z, 2)
                );
            }

            case Vector2L castedStart -> {
                Vector2L castedEnd = (Vector2L) val2;
                yield (T) new Vector2L(
                        Math.round(func.apply((float) castedStart.x, (float) castedEnd.x, 0)),
                        Math.round(func.apply((float) castedStart.y, (float) castedEnd.y, 1))
                );
            }

            case Vector3L castedStart -> {
                Vector3L castedEnd = (Vector3L) val2;
                yield (T) new Vector3L(
                        Math.round(func.apply((float) castedStart.x, (float) castedEnd.x, 0)),
                        Math.round(func.apply((float) castedStart.y, (float) castedEnd.y, 1)),
                        Math.round(func.apply((float) castedStart.z, (float) castedEnd.z, 2))
                );
            }

            case Vector2d castedStart -> {
                Vector2d castedEnd = (Vector2d) val2;
                yield (T) new Vector2d(
                        Math.round(func.apply((float) castedStart.x, (float) castedEnd.x, 0)),
                        Math.round(func.apply((float) castedStart.y, (float) castedEnd.y, 1))
                );
            }

            case Vector3d castedStart -> {
                Vector3d castedEnd = (Vector3d) val2;
                yield (T) new Vector3d(
                        func.apply((float) castedStart.x, (float) castedEnd.x, 0),
                        func.apply((float) castedStart.y, (float) castedEnd.y, 1),
                        func.apply((float) castedStart.z, (float) castedEnd.z, 2)
                );
            }

            case Float castedStart -> {
                float castedEnd = ((Number) val2).floatValue();
                yield (T) (Number) func.apply(castedStart, castedEnd, 0);
            }

            case Integer castedStart -> {
                float castedEnd = ((Number) val2).floatValue();
                yield (T) (Integer) Math.round(func.apply((float) castedStart, castedEnd, 0));
            }

            case Long castedStart -> {
                float castedEnd = ((Number) val2).floatValue();
                yield (T) (Long) (long) Math.round(func.apply((float) castedStart, castedEnd, 0));
            }

            case Double castedStart -> {
                float castedEnd = ((Number) val2).floatValue();
                yield (T) (Number) func.apply(castedStart.floatValue(), castedEnd, 0);
            }

            default -> throw new RuntimeException("This type is not supported for the easing curve computing");
        };
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
    public final float compute(float t) {
        return compute(new float[]{t})[0];
    }

    /** Compute the easing curve and return back the computed value. However, each
     * field will use the corresponded field of the 2D Vector t
     *
     * @param t The 2D Vector "t" parameter for the easing curve
     * @return The computed value
     */
    public final Vector2f compute(Vector2f t) {
        float[] val = compute(new float[]{t.x, t.y});
        return new Vector2f(val[0], val[1]);
    }

    /** Compute the easing curve and return back the computed value. However, each
     * field will use the corresponded field of the 2D Vector t
     *
     * @param t The 2D Vector "t" parameter for the easing curve
     * @return The computed value
     */
    public final Vector3f compute(Vector3f t) {
        float[] val = compute(new float[]{t.x, t.y, t.z});
        return new Vector3f(val[0], val[1], val[2]);
    }

    protected float computeValueT(float t) {
        return Math.clamp(t * (1 + this.easeProgressFactor), 0, 1);
    }

    /** Compute the t parameters for the easing curve and return the computed results
     *
     * @param t The list of t values to compute
     * @return The computed values
     */
    protected float[] compute(float[] t) {
        float[] tCopy = new float[t.length];
        for (int i = 0; i < t.length; i++) {
            float t2 = computeValueT(t[i]);
            tCopy[i] = this.interpolate(t2);
        }
        return tCopy;
    }

    /** Compute the easing curve and get an intermediate value from the start to end based on the
     * t parameter supplied
     *
     * @param t  The t parameter
     * @return The computed intermediate value
     */
    public final T getValue(float t) {
        return this.getValue(new Vector3f(t));
    }

    /** Compute the easing curve and get an intermediate value from the start to end based on the
     * t parameter supplied. However, depending on the field computed, the corresponding t field
     * value will be picked
     *
     * @param t  The 2D Vector t parameter
     * @return The computed intermediate value
     */
    public final T getValue(Vector2f t) {
        if (!this.isVector2D(this.start) || !this.isVector2D(this.end)) {
            throw new RuntimeException("The start & end parameters have to be a 2D Vector");
        }
        return this.getValue(new Vector3f(t.x, t.y, 0));
    }

    /** Compute the easing curve and get an intermediate value from the start to end based on the
     * t parameter supplied. However, depending on the field computed, the corresponding t field value
     * will be picked
     *
     * @param t  The 3D Vector t parameter
     * @return The computed intermediate value
     */
    public T getValue(Vector3f t) {
        Vector3f computedT = this.compute(t);
        return this.calculateUnified(
                this.start,
                this.end,
                (float a, float b, int index) -> switch (index) {
                    case 0 -> ((1f - computedT.x) * a) + (computedT.x * b);
                    case 1 -> ((1f - computedT.y) * a) + (computedT.y * b);
                    case 2 -> ((1f - computedT.z) * a) + (computedT.z * b);
                    default -> throw new RuntimeException("Unexpected index value");
                }
        );
    }


    /** This is the interpolate method. It is the implementation of all the easing logic,
     * it receives a t value. It then returns the new computed t value based on the provided
     * t value
     *
     * @param t The time value(t)
     * @return The computed value out of all the 3 parameters from the implementation
     */
    protected abstract float interpolate(float t);
}
