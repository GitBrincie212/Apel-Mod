package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.mcbrincie.apel.lib.util.interceptor.ObjectInterceptor;
import org.joml.Vector3f;

/**
 * ParticleUtilityObject is the base class for all particle-based utility constructions present in APEL.  Particle utility
 * objects are rendered in the client using a particle object
 *
 * <p>All particle objects share some common properties, and those are provided on the base class. All particle utility
 * objects allow for specifying a {@link #rotation} and {@link #offset}. These will be applied before translating
 * the object to the {@code drawPos} passed to {@link #draw(ApelServerRenderer, DrawContext)}.
 *
 * <p>The provided subclasses include interceptors that allow for modification before and after each call to
 * {@code draw}.
 *
 * <p><strong>Note:</strong> Rotation calculations are in radians and not in degrees.
 * When rotation values exceed the (-2π, 2π), they are wrapped using modulo to remain in the range (-2π, 2π).
 *
 * <h2>Builders</h2>
 * <p>ParticleUtilityObject and its subclasses use a parallel hierarchy of nested classes to provide a fluent approach to
 * constructing instances of ParticleUtilityObject subclasses.  The builders are typed to allow specifying properties in any
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
public abstract class UtilityParticleObject<T extends UtilityParticleObject<T, O>, O extends ParticleObject<O>> extends ParticleObject<T> {
    protected O particleObject;

    /**
     * Used by subclasses to when constructing themselves to set the properties shared by all ParticleObjects.
     *
     * @param particleObject The particle object to use
     * @param rotation The rotation to apply
     * @param offset The offset to apply
     * @param beforeDraw The interceptor to call before drawing the object
     * @param afterDraw The interceptor to call after drawing the object
     */
    protected UtilityParticleObject(O particleObject, EasingCurve<Vector3f> rotation,
                                    EasingCurve<Vector3f> offset, ObjectInterceptor<T> beforeDraw,
                                    ObjectInterceptor<T> afterDraw
    ) {
        super(rotation, offset, beforeDraw, afterDraw);
    }

    /**
     * Used by subclasses when their copy constructors are invoked.  Rotation and offset are copied to new vectors to
     * prevent inadvertent modification impacting multiple objects.
     *
     * @param object The particle object to copy from
     */
    protected UtilityParticleObject(UtilityParticleObject<T, O> object) {
        super(object);
        this.particleObject = object.particleObject;
    }

    /** This is a placeholder constructor */
    protected UtilityParticleObject() {}

    public final void doDraw(ApelServerRenderer renderer, int step, Vector3f drawPos, int numberOfSteps, float deltaTickTime) {
        super.doDraw(renderer, step, drawPos, numberOfSteps, deltaTickTime);
    }

    /**
     * This method allows for drawing a particle object using the provided {@link ApelServerRenderer}, the current
     * step, and the drawing position.
     *
     * <p><b>The method should not be called directly.</b>  It will be called via
     * {@link #doDraw(ApelServerRenderer, int, Vector3f, int, float)}  by {@code PathAnimatorBase} subclasses to draw objects along
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
     * @param data The InterceptData
     */
    public abstract void draw(ApelServerRenderer renderer, DrawContext data);

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
     */
    public static abstract class Builder<B extends Builder<B, T, O>,
            T extends UtilityParticleObject<T, O>, O extends ParticleObject<O>> extends ParticleObject.Builder<B, T> {
        protected O particleObject;

        /**
         * Set the particle object on the builder.  This method is not cumulative; repeated calls will overwrite the
         * value.
         */
        public final B particleObject(ParticleObject<?> particleObject) {
            //noinspection unchecked
            this.particleObject = (O) particleObject;
            return self();
        }

        public UtilityParticleObject<T, O> build() {
            if (this.particleObject == null) {
                throw new IllegalStateException("Particle Object must be provided");
            }
            return null;
        }
    }
}
