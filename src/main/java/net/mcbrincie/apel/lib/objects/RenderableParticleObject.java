package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingRPO;
import net.mcbrincie.apel.lib.util.interceptor.ObjectInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.context.DrawContext;
import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;

/**
 * RenderableParticleObject is the base class for all particle-based constructions that render in APEL. Particle objects
 * are rendered in the client using particles registered with the particle registry.  APEL provides several common
 * 2D and 3D shapes that are ready for immediate use.
 *
 * <p>All renderable particle objects share some common properties, and those are provided on the base class. The
 * {@link #particleEffect} is used to render all particles in the object.  All objects allow for specifying a
 * {@link #rotation} and {@link #offset}. These will be applied before translating the object to the {@code drawPos}
 * passed to {@link #draw(ApelServerRenderer, DrawContext, Vector3f)}. Renderable Particle Objects also have an {@link #amount} that
 * indicates the number of particles to render. APEL native objects will spread these particles evenly throughout the shape
 * unless otherwise indicated on specific shapes.
 *
 * <p>The provided subclasses include interceptors that allow for modification before and after each call to
 * {@code draw}.
 *
 * <p><strong>Note:</strong> Rotation calculations are in radians and not in degrees.
 * When rotation values exceed the (-2π, 2π), they are wrapped using modulo to remain in the range (-2π, 2π).
 *
 * <h2>Builders</h2>
 * <p>ParticleRenderObject and its subclasses use a parallel hierarchy of nested classes to provide a fluent approach to
 * constructing instances of ParticleRenderObject subclasses.  The builders are typed to allow specifying properties in any
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
 * <p>Subclasses should also provide a builder that mimics those provided by APEL-native ParticleRenderObject subclasses.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class RenderableParticleObject<T extends RenderableParticleObject<T>> extends ParticleObject<T> {
    protected ParticleEffect particleEffect;
    protected EasingCurve<Integer> amount = new ConstantEasingCurve<>(1);

    /**
     * Used by subclasses to when constructing themselves to set the properties shared by all ParticleObjects.
     *
     * @param particleEffect The particle effect to use
     * @param rotation The rotation to apply
     * @param offset The offset to apply
     * @param amount The number of particles to use when rendering the object (subject to specific subclass
     *         usage)
     * @param beforeDraw The interceptor to call before drawing the object
     * @param afterDraw The interceptor to call after drawing the object
     */
    protected RenderableParticleObject(ParticleEffect particleEffect, EasingCurve<Vector3f> rotation,
                                       EasingCurve<Vector3f> offset, EasingCurve<Integer> amount,
                                       ObjectInterceptor<T> beforeDraw, ObjectInterceptor<T> afterDraw
    ) {
        super(rotation, offset, beforeDraw, afterDraw);
        this.setParticleEffect(particleEffect);
        this.setAmount(amount);
    }

    /**
     * Used by subclasses when their copy constructors are invoked.  Rotation and offset are copied to new vectors to
     * prevent inadvertent modification impacting multiple objects.
     *
     * @param object The particle object to copy from
     */
    protected RenderableParticleObject(RenderableParticleObject<T> object) {
        super(object);
        this.particleEffect = object.particleEffect;
        this.amount = object.amount;
    }

    /** This is a placeholder constructor */
    protected RenderableParticleObject() {}

    /** Gets the particle which is currently in use and returns it.
     *
     * @return The currently used particle
     */
    public ParticleEffect getParticleEffect() {
        return this.particleEffect;
    }

    /**
     * Sets the particle to use to a new value and returns the previous particle that was used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param particle The new particle
     * @return The previously used particle
     */
    public final ParticleEffect setParticleEffect(ParticleEffect particle) {
        ParticleEffect prevParticle = this.particleEffect;
        this.particleEffect = particle;
        return prevParticle;
    }

    /** Gets the number of particles that are currently in use and returns it.
     *
     * @return The currently used number of particles
     */
    public EasingCurve<Integer> getAmount() {
        return this.amount;
    }

    /**
     * Sets the number of particles to use for rendering the object. This has no effect on this class, but on shapes it
     * does have an effect. It returns the previously used number of particles.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload sets a constant value for the amount
     *
     * @param amount The new particle
     * @return The previously used amount
     */
    public final EasingCurve<Integer> setAmount(int amount) {
        return this.setAmount(new ConstantEasingCurve<>(amount));
    }

    /**
     * Sets the number of particles to use for rendering the object. This has no effect on this class, but on shapes it
     * does have an effect. It returns the previously used number of particles.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload sets an ease curve value for the amount
     *
     * @param amount The new particle
     * @return The previously used amount
     */
    public final EasingCurve<Integer> setAmount(EasingCurve<Integer> amount) {
        EasingCurve<Integer> prevAmount = this.amount;
        this.amount = amount;
        return prevAmount;
    }

    /** Computes some additional easing properties. */
    protected ComputedEasingRPO computeAdditionalEasings(ComputedEasingRPO container) {
        return container;
    }

    public final void doDraw(
            ApelServerRenderer renderer, int step, Vector3f drawPos, int numberOfSteps,
            float deltaTickTime, Vector3f actualSize
    ) {
        super.doDraw(() -> new ComputedEasingRPO(this, step, numberOfSteps), this::computeAdditionalEasings,
                renderer, step, drawPos, numberOfSteps, deltaTickTime, actualSize);
    }

    @Override
    public final void display(ApelServerRenderer renderer, DrawContext<?> data, Vector3f actualSize) {
        //noinspection unchecked
        this.draw(renderer, (DrawContext<ComputedEasingRPO>) data, actualSize);
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
     * @param data The InterceptData
     */
    public abstract void draw(ApelServerRenderer renderer,
                              DrawContext<ComputedEasingRPO> data,
                              Vector3f actualSize);

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
    public static abstract class Builder<B extends Builder<B, T>, T extends RenderableParticleObject<T>> extends ParticleObject.Builder<B, T> {
        protected ParticleEffect particleEffect;
        protected EasingCurve<Vector3f> rotation = new ConstantEasingCurve<>(new Vector3f(0));
        protected EasingCurve<Vector3f> offset = new ConstantEasingCurve<>(new Vector3f(0));
        protected EasingCurve<Integer> amount = new ConstantEasingCurve<>(1);
        protected ObjectInterceptor<T> beforeDraw;
        protected ObjectInterceptor<T> afterDraw;

        /**
         * Set the particle effect on the builder.  This method is not cumulative; repeated calls will overwrite the
         * value.
         */
        public final B particleEffect(ParticleEffect particleEffect) {
            this.particleEffect = particleEffect;
            return self();
        }

        /**
         * Set a constant particle amount on the builder.
         * This method is not cumulative; repeated calls will overwrite the
         * value.
         */
        public B amount(int amount) {
            this.amount = new ConstantEasingCurve<>(amount);
            return self();
        }

        /**
         * Set an easing function for the particle amount on the builder.
         * This method is not cumulative; repeated calls will overwrite the
         * value.
         */
        public B amount(EasingCurve<Integer> amount) {
            this.amount = amount;
            return self();
        }

        public abstract RenderableParticleObject<T> build();
    }
}
