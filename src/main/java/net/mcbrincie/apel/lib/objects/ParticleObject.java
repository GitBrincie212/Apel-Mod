package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.ObjectInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.ObjectInterceptorDispatcher;
import net.mcbrincie.apel.lib.util.interceptor.context.DrawContext;
import net.mcbrincie.apel.lib.util.interceptor.context.Key;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ParticleObject is the base class for all particle-based constructions that are animated by APEL.  Particle objects
 * are rendered in the client using particles registered with the particle registry.  APEL provides several common
 * 2D and 3D shapes as well as utility ones that are ready for immediate use.
 *
 * <p>All particle objects share some common properties, and those are provided on the base class. The
 * {@link #rotation} and {@link #offset}. These will be applied before translating the object to the {@code drawPos}
 * passed to {@link #display(ApelServerRenderer, DrawContext, Vector3f)}
 *
 * <p>The provided subclasses include interceptors that allow for modification before and after each call to
 * {@code draw}.
 *
 * <p><strong>Note:</strong> Rotation calculations are in radians and not in degrees.
 * When rotation values exceed the (-2π, 2π), they are wrapped using modulo to remain in the range (-2π, 2π).
 *
 * <h2>Builders</h2>
 * <p>ParticleObject and its subclasses use a parallel hierarchy of nested classes to provide a fluent approach to
 * constructing instances of ParticleObject subclasses.  The builders are typed to allow specifying properties in any
 * order, regardless of whether they are on a superclass or subclass.  Each builder is templated with a builder that
 * extends itself, using the <a href="https://nuah.livejournal.com/328187.html">Curiously Recurring Template Pattern
 * (CRTP)</a>, and extends the Builder class from the outer class' parent.
 *
 * <h2>Subclassing</h2>
 * <p>Custom shapes only need to implement the {@code draw} method, and it should perform the necessary transformations
 * (scaling, rotation, translation) on the object before calling methods on the
 * {@link ApelServerRenderer renderer} to cause particles to be rendered.  The renderer supports several basic
 * geometries which are documented there.
 *
 * <p>Subclasses should consider mimicking the interceptor behavior so developers using the subclasses have the
 * opportunity to modify the object between each rendering.  These modifications may change rotation, translation,
 * scaling, vertex positions, particle amounts, or any other property of the subclass.
 *
 * <p>Subclasses should also provide a builder that mimics those provided by APEL-native ParticleObject subclasses.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class ParticleObject<T extends ParticleObject<T>> {
    protected EasingCurve<Vector3f> rotation;
    protected EasingCurve<Vector3f> scale = new ConstantEasingCurve<>(new Vector3f(1));
    protected EasingCurve<Vector3f> offset = new ConstantEasingCurve<>(new Vector3f(0, 0, 0));
    protected final ObjectInterceptorDispatcher<T> afterDrawEvent;
    protected final ObjectInterceptorDispatcher<T> beforeDrawEvent;

    /**
     * Used by subclasses to when constructing themselves to set the properties shared by all ParticleObjects.
     *
     * @param rotation The rotation to apply
     * @param offset The offset to apply
     * @param beforeDraw The interceptor to call before drawing the object
     * @param afterDraw The interceptor to call after drawing the object
     */
    protected ParticleObject(EasingCurve<Vector3f> rotation, EasingCurve<Vector3f> offset,
                             ObjectInterceptor<T> beforeDraw, ObjectInterceptor<T> afterDraw
    ) {
        this.setRotation(rotation);
        this.setOffset(offset);
        this.beforeDrawEvent = new ObjectInterceptorDispatcher<>();
        this.afterDrawEvent = new ObjectInterceptorDispatcher<>();
    }

    /**
     * Used by subclasses when their copy constructors are invoked.  Rotation and offset are copied to new vectors to
     * prevent inadvertent modification impacting multiple objects.
     *
     * @param object The particle object to copy from
     */
    protected ParticleObject(ParticleObject<T> object) {
        this.rotation = object.rotation;
        this.offset = object.offset;
        this.beforeDrawEvent = object.beforeDrawEvent;
        this.afterDrawEvent = object.afterDrawEvent;
        this.scale = object.scale;
    }

    /** This is a placeholder constructor */
    protected ParticleObject() {
        this.beforeDrawEvent = new ObjectInterceptorDispatcher<>();
        this.afterDrawEvent = new ObjectInterceptorDispatcher<>();
    }

    /** Gets the rotation which is currently in use and returns it.
     *
     * @return The currently used rotation
     */
    public EasingCurve<Vector3f> getRotation() {
        return this.rotation;
    }

    /**
     * Sets the rotation to a new value. The rotation is calculated in radians and
     * when setting it wraps the rotation to be in the range of (-2π, 2π). The rotation components will have the same
     * signs as they do in the parameter. It returns the previous rotation used. This is an overload for specifying
     * an ease property when using rotation, this is an overload for specifying an ease curve property when using rotation
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param rotation The new rotation (IN RADIANS)
     * @return the previously used rotation
     */
    public final EasingCurve<Vector3f> setRotation(EasingCurve<Vector3f> rotation) {
        EasingCurve<Vector3f> prevRotation = this.rotation;
        this.rotation = rotation;
        return prevRotation;
    }

    /**
     * Sets the rotation to a new value. The rotation is calculated in radians and
     * when setting it wraps the rotation to be in the range of (-2π, 2π).  The rotation components will have the same
     * signs as they do in the parameter. It returns the previous rotation used. This is an overload for specifying
     * a constant value of rotation, this is an overload for specifying a constant property when using rotation
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param rotation The new rotation (IN RADIANS)
     * @return the previously used rotation
     */
    public final EasingCurve<Vector3f> setRotation(Vector3f rotation) {
        return this.setRotation(new ConstantEasingCurve<>(rotation));
    }

    /** Gets the scale which is currently in use and returns it.
     *
     * @return The currently used scale
     */
    public EasingCurve<Vector3f> getScale() {
        return this.scale;
    }

    /**
     * Sets the scale to a new value. The scale acts as a multiplier to the already defined size of the particle object
     * It returns the previous scale used. This is an overload for specifying an ease property when using scale. Negative
     * scales flip the object and when the scale is zero, it isn't rendered, lower than one and greater than zero will shrink
     * the particle object and any value greater than one will enlarge the particle object
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param scale The new scale
     * @return the previously used scale
     */
    public final EasingCurve<Vector3f> setScale(EasingCurve<Vector3f> scale) {
        EasingCurve<Vector3f> prevScale = this.scale;
        this.scale = scale;
        return prevScale;
    }

    /**
     *  Sets the scale to a new value. The scale acts as a multiplier to the already defined size of the particle object
     *  It returns the previous scale used. This is an overload for specifying an ease property when using scale. Negative
     *  scales flip the object and when the scale is zero, it isn't rendered, lower than one and greater than zero will shrink
     *  the particle object and any value greater than one will enlarge the particle object. This is an overload for specifying
     *  a constant property when using rotation
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param scale The new scale
     * @return the previously used scale
     */
    public final EasingCurve<Vector3f> setScale(Vector3f scale) {
        return this.setScale(new ConstantEasingCurve<>(scale));
    }

    /** Gets the current offset value used. The offset position is added with the drawing position.
     *
     * @return The offset
     */
    public EasingCurve<Vector3f> getOffset() {
        return this.offset;
    }

    /**
     * Sets the offset to a new value. The offset position is added with the drawing position.
     * Returns the previous offset that was used. This is an overload for specifying a ease curve
     * for the offset value
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param offset The new offset value
     * @return The previous offset
     */
    public final EasingCurve<Vector3f> setOffset(EasingCurve<Vector3f> offset) {
        EasingCurve<Vector3f> prevOffset = this.offset;
        this.offset = offset;
        return prevOffset;
    }

    /**
     * Sets the offset to a new value. The offset position is added with the drawing position.
     * Returns the previous offset that was used. This is an overload for specifying a constant
     * value of offset
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param offset The new offset value
     * @return The previous offset
     */
    public final EasingCurve<Vector3f> setOffset(Vector3f offset) {
        EasingCurve<Vector3f> prevOffset = this.offset;
        this.offset = new ConstantEasingCurve<>(offset);
        return prevOffset;
    }

    /** Subscribes an interceptor to run prior to drawing the object. The interceptor will be provided with references to the
     * {@link ServerWorld}, an "origin" point from which the object should be drawn, the step number of the animation... etc,
     * and including any metadata defined by {@link Key} defined in the specific subclass.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param beforeDraw the new interceptor to execute before drawing each particle
     */
    public final void subscribeToBeforeDraw(@NotNull ObjectInterceptor<T> beforeDraw) {
        this.beforeDrawEvent.addInterceptor(beforeDraw);
    }

    /** Subscribes an interceptor to run after drawing the object. The interceptor will be provided with references to the
     * {@link ServerWorld}, an "origin" point from which the object should be drawn, the step number of the animation... etc,
     * and including any metadata defined by {@link Key}s defined in the specific subclass.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param afterDraw the new interceptor to execute after drawing each particle
     */
    public final void subscribeToAfterDraw(@NotNull ObjectInterceptor<T> afterDraw) {
        this.afterDrawEvent.addInterceptor(afterDraw);
    }

    /**
     * This method allows for drawing a particle object using the provided {@link ApelServerRenderer}, the current
     * step, and the drawing position.
     *
     * <p><b>The method should not be called directly.</b>  It will be called via
     * {@link #doDraw(ApelServerRenderer, int, Vector3f, int, float, Vector3f)}  by {@code PathAnimatorBase} subclasses to draw objects along
     * the animation path or at an animation point.  These animators will provide the renderer and calculate the
     * current {@code step} and the {@code drawPos}.  The renderer will have access to the {@code ServerWorld}.
     * <p>
     * <strong>Implementation Notes:</strong>
     * <ul>
     *     <li>Implementations should call methods on the {@code renderer} that cause drawing to occur.</li>
     *     <li><b>Important:</b> Implementations must also take care <em>not</em> to modify the {@code drawPos}, as many operations on {@link Vector3f} instances are in-place.</li>
     *     <li>The provided subclasses include interceptors that allow developers to perform actions both before and after drawing the object.</li>
     * </ul>
     *
     * @param renderer The server world instance
     * @param data The draw context data
     */
    public abstract void display(ApelServerRenderer renderer, DrawContext<?> data, Vector3f actualSize);

    /** Computes some additional easing properties. */
    protected ComputedEasingPO computeAdditionalEasings(ComputedEasingPO container) {
        return container;
    }

    public void doDraw(ApelServerRenderer renderer, int step, Vector3f drawPos,
                       int numberOfSteps, float deltaTickTime, Vector3f actualSize) {
        doDraw(() -> new ComputedEasingPO(this, step, numberOfSteps), this::computeAdditionalEasings,
                renderer, step, drawPos, numberOfSteps, deltaTickTime, actualSize);
    }

    public <TC extends ComputedEasingPO> void doDraw(
            Supplier<TC> factory, Function<TC, TC> computeMethod,
            ApelServerRenderer renderer, int step, Vector3f drawPos, int numberOfSteps,
            float deltaTickTime, Vector3f actualSize
    ) {
        TC computedEasingPO = computeMethod.apply(factory.get());
        actualSize = actualSize.mul(computedEasingPO.computedScale);
        DrawContext<?> drawContext = new DrawContext<>(
                renderer.getServerWorld(), drawPos,
                step, numberOfSteps, deltaTickTime,
                computedEasingPO
        );
        this.prepareContext(drawContext);
        //noinspection unchecked
        this.beforeDrawEvent.compute((T) this, drawContext);
        this.display(renderer, drawContext, actualSize);
        //noinspection unchecked
        this.afterDrawEvent.compute((T) this, drawContext);
    }

    /**
     * Subclasses should override to provide metadata into the {@code interceptData}.  The default implementation does
     * nothing.
     *
     * @param drawContext the data holder to modify
     */
    protected void prepareContext(DrawContext<?> drawContext) {
        // Default implementation does nothing
    }

    /**
     * Provides a base for ParticleObject subclasses to extend when creating their builders.
     * <p>
     * Properties in this class are protected: this allows easy access from subclasses, but subclasses should take care
     * not to hide these fields by reusing fields of the same name.
     * <p>
     * Methods in this class are final: these are specifically setting properties for the ParticleObject class, so
     * they must maintain the invariants of ParticleObject.
     *
     * @param <B> the type being built, uses the curiously recurring type pattern
     * @param <T> The type of the particle object used
     */
    public static abstract class Builder<B extends Builder<B, T>, T extends ParticleObject<T>> {
        protected EasingCurve<Vector3f> rotation = new ConstantEasingCurve<>(new Vector3f(0));
        protected EasingCurve<Vector3f> offset = new ConstantEasingCurve<>(new Vector3f(0));
        protected EasingCurve<Vector3f> scale = new ConstantEasingCurve<>(new Vector3f(1));
        protected ObjectInterceptor<T> beforeDraw;
        protected ObjectInterceptor<T> afterDraw;

        @SuppressWarnings({"unchecked"})
        public final B self() {
            return (B) this;
        }

        /**
         * Set a constant rotation on the builder.
         * This method is not cumulative; repeated calls will overwrite the value.
         */
        public final B rotation(Vector3f rotation) {
            this.rotation = new ConstantEasingCurve<>(rotation);
            return self();
        }

        /**
         * Set a constant offset on the builder.
         * This method is not cumulative; repeated calls will overwrite the value.
         */
        public final B offset(Vector3f offset) {
            this.offset = new ConstantEasingCurve<>(offset);
            return self();
        }

        /**
         * Set the rotation on the builder using an ease function.
         * This method is not cumulative; repeated calls will overwrite the value.
         */
        public final B rotation(EasingCurve<Vector3f> rotation) {
            this.rotation = rotation;
            return self();
        }

        /**
         * Set the offset on the builder using an ease function.
         * This method is not cumulative; repeated calls will overwrite the value.
         */
        public final B offset(EasingCurve<Vector3f> offset) {
            this.offset = offset;
            return self();
        }

        /**
         * Set the scale on the builder using an easing curve with a vector type.
         * This method is not cumulative; repeated calls will overwrite the value.
         */
        public final B scale(EasingCurve<Vector3f> scale) {
            this.scale = scale;
            return self();
        }

        /**
         * Set the scale on the builder using a constant vector value.
         * This method is not cumulative; repeated calls will overwrite the value.
         */
        public final B scale(Vector3f scale) {
            this.scale = new ConstantEasingCurve<>(scale);
            return self();
        }

        /**
         * Set the scale on the builder using a constant value.
         * This method is not cumulative; repeated calls will overwrite the value.
         */
        public final B scale(float scale) {
            this.scale = new ConstantEasingCurve<>(new Vector3f(scale));
            return self();
        }

        /**
         * Sets the interceptor to run before drawing.
         * This method is not cumulative; repeated calls will overwrite
         * the value.
         *
         * @see ParticleObject#subscribeToBeforeDraw(ObjectInterceptor)
         */
        public final B beforeDraw(ObjectInterceptor<T> beforeDraw) {
            this.beforeDraw = beforeDraw;
            return self();
        }

        /**
         * Sets the interceptor to run after drawing.
         * This method is not cumulative; repeated calls will overwrite the value.
         *
         * @see ParticleObject#subscribeToAfterDraw(ObjectInterceptor)
         */
        public final B afterDraw(ObjectInterceptor<T> afterDraw) {
            this.afterDraw = afterDraw;
            return self();
        }

        public abstract ParticleObject<T> build();
    }
}
