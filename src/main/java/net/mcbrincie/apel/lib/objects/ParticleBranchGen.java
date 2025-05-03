package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

/**
 * This particle object subclass represents a set of 3D branches. Constructing the branch generator requires two points
 * which both are relative to the provided origin in the {@code DrawContext}, the minimum and maximum dimension threshold of
 * the entire branch set as well as the number of subdivisions for the fractal. The entire branch set may be rotated and offset
 * relative to the draw origin by using the builder properties, setters after construction, or during interceptor calls.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleBranchGen extends ParticleObject<ParticleBranchGen> {
    protected Vector3f start;
    protected Vector3f end;
    protected Vector3f minLength;
    protected Vector3f maxLength;
    protected int subDivisions;
    protected float taperIntensity;
    private static final Random rand = Random.create();

    public static Builder<?> builder() {
        return new Builder<>();
    }

    private ParticleBranchGen(Builder<?> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setStart(builder.start);
        this.setEnd(builder.end);
        this.setMinLength(builder.minDimensionsThreshold);
        this.setMaxLength(builder.maxDimensionsThreshold);
        this.setSubDivisions(builder.subDivs);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.  The start and end points are copied to new
     * vectors.
     *
     * @param branchGen The particle object to copy from
    */
    public ParticleBranchGen(ParticleBranchGen branchGen) {
        super(branchGen);
        this.start = new Vector3f(branchGen.start);
        this.end = new Vector3f(branchGen.end);
        this.minLength = new Vector3f(branchGen.minLength);
        this.maxLength = new Vector3f(branchGen.maxLength);
        this.subDivisions = branchGen.subDivisions;
        this.taperIntensity = branchGen.taperIntensity;
    }

    /** Gets the minimum length threshold
     *
     * @return The minimum length threshold
     */
    public Vector3f getMinLength() {
        return this.minLength;
    }

    /**
     * Sets the minimum length threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param minLength The new minimum length threshold
     * @return The minimum length threshold
     */
    public final Vector3f setMinLength(Vector3f minLength) {
        if (minLength.x <= 0 || minLength.y <= 0 || minLength.z <= 0) {
            throw new IllegalArgumentException("One of the Minimum Length's Axis is lower than or equal to zero");
        }
        Vector3f prevLength = this.minLength;
        this.minLength = minLength;
        return prevLength;
    }

    /** Gets the maximum length threshold
     *
     * @return The maximum length threshold
     */
    public Vector3f getMaxLength() {
        return this.maxLength;
    }

    /**
     * Sets the maximum length threshold of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param maxLength The new maximum length threshold
     * @return The previous maximum length threshold
     */
    public final Vector3f setMaxLength(Vector3f maxLength) {
        if (maxLength.x <= 0 || maxLength.y <= 0 || maxLength.z <= 0) {
            throw new IllegalArgumentException("One of the Maximum Length's Axis is lower than or equal to zero");
        }
        Vector3f prevLength = this.maxLength;
        this.maxLength = maxLength;
        return prevLength;
    }

    /** Gets the starting endpoint
     *
     * @return The starting endpoint
     */
    public Vector3f getStart() {
        return this.start;
    }

    /**
     * Sets the starting point of the line.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param start The new starting point of the line
     * @return The previous starting point
    */
    public final Vector3f setStart(Vector3f start) {
        if (start.equals(this.end)) {
            throw new IllegalArgumentException("Endpoints must not be equal");
        }
        Vector3f prevStart = this.start;
        this.start = start;
        return prevStart;
    }

    /** Gets the ending endpoint
     *
     * @return The ending endpoint
     */
    public Vector3f getEnd() {
        return this.end;
    }

    /**
     * Sets the ending point of the line.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param end The new ending point of the line
     * @return The previous ending point
    */
    public final Vector3f setEnd(Vector3f end) {
        if (end.equals(this.start)) {
            throw new IllegalArgumentException("Endpoints must not be equal");
        }
        Vector3f prevEnd = this.end;
        this.end = end;
        return prevEnd;
    }

    /** Gets the branch subdivisions
     *
     * @return The branch subdivisions
     */
    public int getSubdivisions() {
        return this.subDivisions;
    }

    /**
     * Sets the  branch subdivisions of the branch generator
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param subDivisions The new branch subdivision count
     * @return The previous branch subdivision count
     */
    public final int setSubDivisions(int subDivisions) {
        if (subDivisions <= 0) {
            throw new IllegalArgumentException("Sub Divisions has to be positive and non-zero");
        }
        int prevSubDivisions = this.subDivisions;
        this.subDivisions = subDivisions;
        return prevSubDivisions;
    }


    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(this.offset);

        generateBranch(renderer, drawContext, this.start, this.end, this.subDivisions);
    }


    private void generateBranch(ApelServerRenderer renderer, DrawContext drawContext, Vector3f from, Vector3f to, int depth) {
        if (depth <= 0) {
            renderer.drawParticle(this.particleEffect, drawContext.getCurrentStep(), to);
            return;
        }

        Vector3f dir = new Vector3f(to).sub(from);
        float dist = dir.length();
        dir.normalize();

        float segmentLen;
        if (from.distance(end) < 1e-3f) {
            float angle = rand.nextFloat() * (float) Math.PI * 2;
            dir.set((float)Math.cos(angle), (float)Math.sin(angle), 0);
            segmentLen = minLength.length() + rand.nextFloat() * (maxLength.length() - minLength.length());
        } else {
            float t = Math.min(1f, from.distance(end) / maxLength.length());
            segmentLen = minLength.length() + t * (maxLength.length() - minLength.length());
        }

        Vector3f mid = new Vector3f(from).fma(segmentLen, dir);

        renderer.drawParticle(this.particleEffect, drawContext.getCurrentStep(), mid);

        generateBranch(renderer, drawContext, mid, to, depth - 1);

        Vector3f dev = new Vector3f(dir);
        dev.rotateY((rand.nextFloat() - 0.5f) * 0.5f);
        Vector3f devEnd = new Vector3f(mid).fma(segmentLen, dev);
        generateBranch(renderer, drawContext, mid, devEnd, depth - 1);
    }

    public static class Builder<B extends Builder<B>> extends ParticleObject.Builder<B, ParticleBranchGen> {
        protected Vector3f start = new Vector3f();
        protected Vector3f end = new Vector3f();
        protected Vector3f minDimensionsThreshold = new Vector3f();
        protected Vector3f maxDimensionsThreshold = new Vector3f();
        protected int subDivs = 1;

        private Builder() {}

        /**
         * Set the number of subdivisions on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B subDivisions(int subDivisions) {
            if (subDivisions <= 0) {
                throw new IllegalArgumentException("The Argument subDivisions is negative or equal to zero");
            }
            this.subDivs = subDivisions;
            return self();
        }

        /**
         * Set the minimum dimensions threshold on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B minDimensions(Vector3f minimumDimensionsThreshold) {
            this.minDimensionsThreshold = minimumDimensionsThreshold;
            return self();
        }

        /**
         * Set the maximum dimensions threshold on the builder. This method is not cumulative;
         * repeated calls will overwrite the value.
         */
        public B maxDimensions(Vector3f maximumDimensionsThreshold) {
            this.maxDimensionsThreshold = maximumDimensionsThreshold;
            return self();
        }

        /**
         * Set the start point on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B start(Vector3f start) {
            this.start = start;
            return self();
        }

        /**
         * Set the end point on the builder.  This method is not cumulative; repeated calls will overwrite the value.
         */
        public B end(Vector3f end) {
            this.end = end;
            return self();
        }

        @Override
        public ParticleBranchGen build() {
            return new ParticleBranchGen(this);
        }
    }
}
