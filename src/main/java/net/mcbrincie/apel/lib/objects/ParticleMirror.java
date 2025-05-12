package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.mcbrincie.apel.lib.util.interceptor.ObjectInterceptor;
import org.joml.Vector3f;

/** A utility particle object class that copies one particle object as
 * a mirrored one (it draws twice but acts as one particle object).
 * Particle mirrors can also mirror themselves which can produce cool patterns.
 * The particle mirror can also lock certain rotation axis or even disable the rotation
 * inversion entirely. The same thing can be achieved via interceptors, but this makes
 * it easier to use. Keep in mind that invisible particle objects won't be rendered.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleMirror extends ParticleObject<ParticleMirror> {
    protected ParticleObject<?> target_object;
    protected EasingCurve<Float> distance;
    protected boolean lockXAxis = false;
    protected boolean lockYAxis = false;
    protected boolean lockZAxis = false;
    protected int amount = -1;

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleMirror(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, ObjectInterceptor.identity(),
              ObjectInterceptor.identity());
        this.setTargetObject(builder.target_object);
        this.setDistance(builder.distance);
        this.setLockX(builder.lockX);
        this.setLockY(builder.lockY);
        this.setLockZ(builder.lockZ);
    }

    /** The copy constructor for the particle mirror. Which
     * copies all the object, offset, and interceptor references into a brand-new
     * instance of the particle combiner.
     *
     * @param mirror The particle mirror to copy from
     */
    public ParticleMirror(ParticleMirror mirror) {
        super(mirror);
        this.target_object = mirror.target_object;
        this.distance = mirror.distance;
        this.lockXAxis = mirror.lockXAxis;
        this.lockYAxis = mirror.lockYAxis;
        this.lockZAxis = mirror.lockZAxis;
        this.amount = -1;
    }

    /** Gets the particle object that is mirrored
     *
     * @return The list of particle objects
     */
    public ParticleObject<?> getTargetObject() {
        return this.target_object;
    }

    /** Gets the distance between the target object and the center mirror
     *
     * @return The distance between
     */
    public EasingCurve<Float> getDistance() {
        return this.distance;
    }

    /** Gets the lock rotation axis values. Returns a list of
     * three elements for the boolean values going in the order of xyz
     *
     * @return the boolean values going in order of xyz
     */
    public boolean[] getLockAxis() {return new boolean[]{this.lockXAxis, this.lockYAxis, this.lockZAxis};}

    /** Sets the target object to a different object.
     *
     * @param newObject The new particle object
     * @return The previous particle object
     */
    public ParticleObject<?> setTargetObject(ParticleObject<?> newObject) {
        ParticleObject<?> prevParticleObj = this.target_object;
        this.target_object = newObject;
        return prevParticleObj;
    }

    /** Sets the distance between the target and mirrored particle object.
     * Distance can be negative as well, so it doesn't really matter.
     * This method overload will set a constant value for the distance
     *
     * @param distance The new distance between the objects
     * @return The previous distance between the objects
     */
    public EasingCurve<Float> setDistance(float distance) {
        return this.setDistance(new ConstantEasingCurve<>(distance));
    }

    /** Sets the distance between the target and mirrored particle object.
     * Distance can be negative as well, so it doesn't really matter.
     * This method overload will set an ease curve value for the distance
     *
     * @param distance The new distance between the objects
     * @return The previous distance between the objects
     */
    public EasingCurve<Float> setDistance(EasingCurve<Float> distance) {
        EasingCurve<Float> prevDist = this.distance;
        this.distance = distance;
        return prevDist;
    }

    /** Sets the lock X value which locks the rotation for this axis
     *
     * @param bool The new boolean value if it should lock or not
     * @return The previous boolean value used
     */
    public boolean setLockX(boolean bool) {
        boolean prevLockAxis = this.lockXAxis;
        this.lockXAxis = bool;
        return prevLockAxis;
    }

    /** Sets the lock Y value which locks the rotation for this axis
     *
     * @param bool The new boolean value if it should lock or not
     * @return The previous boolean value used
     */
    public boolean setLockY(boolean bool) {
        boolean prevLockAxis = this.lockYAxis;
        this.lockYAxis = bool;
        return prevLockAxis;
    }

    /** Sets the lock Z value which locks the rotation for this axis
     *
     * @param bool The new boolean value if it should lock or not
     * @return The previous boolean value used
     */
    public boolean setLockZ(boolean bool) {
        boolean prevLockAxis = this.lockZAxis;
        this.lockZAxis = bool;
        return prevLockAxis;
    }

    @Override
    protected ComputedEasings computeAdditionalEasings(ComputedEasingPO container) {
        return container.addComputedField("distance", this.distance);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        ComputedEasingPO computedEasings = drawContext.getComputedEasings();
        float dist = (float) computedEasings.getComputedField("distance");
        Vector3f position = drawContext.getPosition();
        if (dist == 0) {
            this.target_object.doDraw(
                    renderer,
                    drawContext.getCurrentStep(),
                    position,
                    drawContext.getNumberOfStep(),
                    drawContext.getDeltaTickTime()
            );
            return;
        }
        Vector3f rotatedDirection = new Vector3f(0, 1, 0)
                .rotateZ(computedEasings.computedRotation.z)
                .rotateY(computedEasings.computedRotation.y)
                .rotateX(computedEasings.computedRotation.x);
        Vector3f mirrored_pos = new Vector3f(position).add(rotatedDirection.mul(dist));
        Vector3f target_pos = (new Vector3f(position).mul(2)).sub(mirrored_pos);
        this.target_object.doDraw(
                renderer,
                drawContext.getCurrentStep(),
                target_pos,
                drawContext.getNumberOfStep(),
                drawContext.getDeltaTickTime()
        );
        float pi = (float) Math.PI;
        float x = this.lockXAxis ? -pi : pi;
        float y = this.lockYAxis ? -pi : pi;
        float z = this.lockZAxis ? -pi : pi;
        Vector3f prevRot = this.target_object.getRotation().getValue(
                (float) drawContext.getCurrentStep() / drawContext.getNumberOfStep()
        );
        this.target_object.setRotation(new Vector3f(x, y, z).sub(prevRot));
        this.target_object.doDraw(
                renderer,
                drawContext.getCurrentStep(),
                mirrored_pos,
                drawContext.getNumberOfStep(),
                drawContext.getDeltaTickTime()
        );
        this.target_object.setRotation(prevRot);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleMirror> {
        protected ParticleObject<?> target_object;
        protected EasingCurve<Float> distance;
        protected boolean lockX = false;
        protected boolean lockY = false;
        protected boolean lockZ = false;
        protected boolean isDistSet = false;

        private Builder() {}

        /** Sets the target object to the particle mirror. This method works in the fashion of
         * last modification wins as such it isn't cumulative
        */
        public B object(ParticleObject<?> object) {
            this.target_object = object;
            return self();
        }

        /** Sets the distance between the mirror, it also accepts negative distances. This
         * method works in the fashion of last modification wins as such it isn't cumulative
        */
        public B distance(float distance) {
            this.distance = new ConstantEasingCurve<>(distance);
            this.isDistSet = true;
            return self();
        }

        /** Sets the distance between the mirror, it also accepts negative distances. This
         * method works in the fashion of last modification wins as such it isn't cumulative
         */
        public B distance(EasingCurve<Float> distance) {
            this.distance = distance;
            this.isDistSet = true;
            return self();
        }

        /** Sets the lock X value which locks the rotation for this axis. This method
         * switches inverts the value (default it is false) every time called and is cumulative
         */
        public B lockX() {
            this.lockX = !this.lockX;
            return self();
        }

        /** Sets the lock Y value which locks the rotation for this axis. This method
         * switches inverts the value (default it is false) every time called and is cumulative
         */
        public B lockY() {
            this.lockY = !this.lockY;
            return self();
        }

        /** Sets the lock Z value which locks the rotation for this axis. This method
         * switches inverts the value (default it is false) every time called and is cumulative
         */
        public B lockZ() {
            this.lockZ = !this.lockZ;
            return self();
        }

        /** Sets the lock for all the three axis values which locks the rotation entirely. This method
         * switches inverts the values (all by default are false) every time called and is cumulative
         */
        public B lockAllAxis(float distance) {
            this.lockX = !this.lockX;
            this.lockY = !this.lockY;
            this.lockZ = !this.lockZ;
            return self();
        }

        @Override
        public ParticleMirror build() {
            if (this.target_object == null) {
                throw new IllegalStateException("A Target Particle Object Must Be Provided");
            }
            if (!this.isDistSet) {
                throw new IllegalStateException("A Distance Must Be Provided");
            }
            return new ParticleMirror(this);
        }
    }
}
