package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.ComputedEasingRPO;
import net.mcbrincie.apel.lib.util.ComputedEasings;
import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.easing.EasingCurve;
import net.mcbrincie.apel.lib.easing.shaped.ConstantEasingCurve;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.ComputedEasingPO;
import net.mcbrincie.apel.lib.util.interceptor.DrawContext;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/** The particle object class that represents a 2D regular polygon. Regular polygons contain the
 * same angles as well as the same edges; Examples include but are not limited to a square, a pentagon,
 * an equilateral triangle, etc. The polygon takes a variable "sides" that dictates how many sides the polygon
 * will have.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticlePolygon extends RenderableParticleObject<ParticlePolygon> {
    protected EasingCurve<Integer> sides;
    protected EasingCurve<Float> size;
    protected EasingCurve<Float> curve;
    private final List<BezierCurve> bezierCurves = new ArrayList<>();

    protected HashMap<Integer, Vector3f[]> cachedShapes = new HashMap<>();

    public static <B extends Builder<B>> Builder<B> builder() {
        return new Builder<>();
    }

    private <B extends Builder<B>> ParticlePolygon(Builder<B> builder) {
        super(builder.particleEffect, builder.rotation, builder.offset, builder.amount, builder.beforeDraw,
              builder.afterDraw);
        this.setSides(builder.sides);
        this.setSize(builder.size);
        this.setCurve(builder.curve);
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has.
     *
     * @param polygon The particle polygon object to copy from
    */
    public ParticlePolygon(ParticlePolygon polygon) {
        super(polygon);
        this.sides = polygon.sides;
        this.size = polygon.size;
        this.curve = polygon.curve;
        this.cachedShapes = polygon.cachedShapes;
    }

    /**
     * Sets the sides to a new value and returns the previous sides used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the sides
     *
     * @param sides The new sides
     * @throws IllegalArgumentException If the sides are less than 3
     * @return The previous sides used
     */
    public final EasingCurve<Integer> setSides(int sides) throws IllegalArgumentException {
        return this.setSides(new ConstantEasingCurve<>(sides));
    }

    /**
     * Sets the sides to a new value and returns the previous sides used.
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the sides
     *
     * @param sides The new sides
     * @throws IllegalArgumentException If the sides are less than 3
     * @return The previous sides used
     */
    public final EasingCurve<Integer> setSides(EasingCurve<Integer> sides) throws IllegalArgumentException {
        EasingCurve<Integer> prevSides = this.sides;
        this.sides = sides;
        return prevSides;
    }

    /** Gets the sides of the regular polygon.
     *
     * @return The sides of the regular polygon
     */
    public EasingCurve<Integer> getSides() {
        return this.sides;
    }

    /**
     * Sets the size of the polygon to a new value and returns the previous size used.  The size is the distance from
     * the centroid to any vertex. This method overload will set a constant value for the size
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param size The new size
     * @throws IllegalArgumentException If the size is negative or 0
     * @return The previous size used
     */
    public final EasingCurve<Float> setSize(float size) throws IllegalArgumentException {
        return this.setSize(new ConstantEasingCurve<>(size));
    }

    /**
     * Sets the size of the polygon to a new value and returns the previous size used.  The size is the distance from
     * the centroid to any vertex. This method overload will set an ease curve value for the size
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     *
     * @param size The new size
     * @throws IllegalArgumentException If the size is negative or 0
     * @return The previous size used
     */
    public final EasingCurve<Float> setSize(EasingCurve<Float> size) throws IllegalArgumentException {
        EasingCurve<Float> prevSize = this.size;
        this.size = size;
        return prevSize;
    }

    /** Gets the size of the regular polygon
     *
     * @return The size of the regular polygon
     */
    public EasingCurve<Float> getSize() {
        return this.size;
    }

    /**
     * Sets the curve of the polygon to a new value and returns the previous curve value used.
     * The curve is the distance of the control points that make up the Bézier curves. Values that
     * are below 1 make the shape contract while values that are above 0 make the shape more circular
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set a constant value for the size
     *
     * @param curve The new curve value
     * @throws IllegalArgumentException If the curve value
     * @return The previous curve value used
     */
    public final EasingCurve<Float> setCurve(float curve) throws IllegalArgumentException {
        return this.setCurve(new ConstantEasingCurve<>(curve));
    }

    /**
     * Sets the curve of the polygon to a new value and returns the previous curve value used.
     * The curve is the distance of the control points that make up the Bézier curves. Values that
     * are below 1 make the shape contract while values that are above 0 make the shape more circular
     * <p>
     * This implementation is used by the constructor, so subclasses cannot override this method.
     * This method overload will set an ease curve value for the size
     *
     * @param curve The new curve value
     * @throws IllegalArgumentException If the curve value
     * @return The previous curve value used
     */
    public final EasingCurve<Float> setCurve(EasingCurve<Float> curve) throws IllegalArgumentException {
        EasingCurve<Float> prevCurve = this.curve;
        this.curve = curve;
        return prevCurve;
    }

    /** Gets the roundness of the regular polygon
     *
     * @return The roundness of the regular polygon
     */
    public EasingCurve<Float> getCurve() {
        return this.curve;
    }

    @Override
    protected ComputedEasings computeAdditionalEasings(ComputedEasingPO container) {
        return container.addComputedField("sides", this.sides)
                .addComputedField("size", this.size)
                .addComputedField("curve", this.curve);
    }

    @Override
    public void draw(ApelServerRenderer renderer, DrawContext drawContext) {
        ComputedEasingRPO computedEasingPO = (ComputedEasingRPO) drawContext.getComputedEasings();
        Vector3f objectDrawPos = new Vector3f(drawContext.getPosition()).add(computedEasingPO.computedOffset);

        // Divide the particles evenly among sides
        int computedSides = (int) computedEasingPO.getComputedField("sides");
        if (computedSides <= 2) {
            throw new RuntimeException("Sides has to be more than 2 in order to generate");
        }
        int particlesPerLine = computedEasingPO.computedAmount / computedSides;
        float computedCurve = (float) computedEasingPO.getComputedField("curve");
        if (computedCurve < -1 || computedCurve > 1) {
            throw new RuntimeException("Curve value is out of bounds between [-1, 1]");
        }
        float computedSize = (float) computedEasingPO.getComputedField("size");
        if (computedSize <= 0) {
            throw new RuntimeException("Size has to be positive and non-zero");
        }
        Vector3f computedRotation = computedEasingPO.computedRotation;

        Vector3f[] vertices = getRawVertices(computedSides, computedSize);
        for (int i = 0; i < vertices.length - 1; i++) {
            Vector3f currVertex = vertices[i];
            Vector3f nextVertex = vertices[i + 1];
            if (computedCurve == 1.0f) {
                renderer.drawLine(
                    this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, vertices[i], vertices[i + 1],
                    computedRotation, particlesPerLine
                );
            } else {
                // Create Bézier curves, if needed
                if (this.bezierCurves.size() < vertices.length - 1) {
                    // Empty is OK, values will be set right after this
                    this.bezierCurves.add(BezierCurve.of(new Vector3f(), new Vector3f(), new Vector3f()));
                }
                Vector3f controlPoint = new Vector3f(
                        MathHelper.lerp(0.5f, currVertex.x, nextVertex.x),
                        MathHelper.lerp(0.5f, currVertex.y, nextVertex.y),
                        MathHelper.lerp(0.5f, currVertex.z, nextVertex.z)
                ).mul(computedCurve);

                BezierCurve bezierCurve = this.bezierCurves.get(i);
                bezierCurve.setStart(currVertex);
                bezierCurve.setEnd(nextVertex);
                bezierCurve.getControlPoints().getFirst().set(controlPoint);

                renderer.drawBezier(
                        this.particleEffect, drawContext.getCurrentStep(), objectDrawPos, bezierCurve,
                        computedRotation, particlesPerLine
                );
            }
        }
    }

    private @NotNull Vector3f[] getRawVertices(int computedSides, float computedSize) {
        // Cache the vertices, does not rotate or offset
        // (since these change a lot and the goal is to use the cache as much as possible)
        Vector3f[] cachedVertices = this.cachedShapes.computeIfAbsent(computedSides, sides -> {
            float angleInterval = (float) (Math.TAU / computedSides);
            // Sides + 1 to repeat the starting point to prevent modulo math later
            Vector3f[] newVertices = new Vector3f[computedSides + 1];
            for (int i = 0; i < computedSides; i++) {
                float currAngle = angleInterval * i; //) + offset;
                float x = Apel.TRIG_TABLE.getCosine(currAngle);
                float y = Apel.TRIG_TABLE.getSine(currAngle);
                newVertices[i] = new Vector3f(x, y, 0);
            }
            // Ensure the last particle is exactly the same as the first
            newVertices[computedSides] = new Vector3f(newVertices[0]);
            return newVertices;
        });
        // Defensive copy of vertices, scaled after copying, so the cache isn't corrupted
        Vector3f[] verticesCopy = new Vector3f[cachedVertices.length];
        for (int i = 0; i < cachedVertices.length; i++) {
            verticesCopy[i] = new Vector3f(cachedVertices[i]).mul(computedSize);
        }
        return verticesCopy;
    }

    public static class Builder<B extends Builder<B>> extends RenderableParticleObject.Builder<B, ParticlePolygon> {
        protected EasingCurve<Integer> sides;
        protected EasingCurve<Float> size;
        protected EasingCurve<Float> curve = new ConstantEasingCurve<>(0f);

        private Builder() {}

        /**
         * Set the number of sides on the builder.  This method is not cumulative; repeated calls will overwrite the
         * value.
         */
        public B sides(int sides) {
            this.sides = new ConstantEasingCurve<>(sides);
            return self();
        }

        /**
         * Set the size on the builder. This method is not cumulative; repeated calls will overwrite the value.
         *
         * @see ParticlePolygon#setSize(float)
         */
        public B size(float size) {
            this.size = new ConstantEasingCurve<>(size);
            return self();
        }

        /**
         * Set the curve on the builder. This method is not cumulative; repeated calls will overwrite the value.
         *
         * @see ParticlePolygon#setCurve(float) (float)
         */
        public B curve(float curve) {
            this.curve = new ConstantEasingCurve<>(curve);
            return self();
        }

        /**
         * Set the number of sides on the builder.  This method is not cumulative; repeated calls will overwrite the
         * value.
         */
        public B sides(EasingCurve<Integer> sides) {
            this.sides = sides;
            return self();
        }

        /**
         * Set the size on the builder. This method is not cumulative; repeated calls will overwrite the value.
         *
         * @see ParticlePolygon#setSize(float)
         */
        public B size(EasingCurve<Float> size) {
            this.size = size;
            return self();
        }

        /**
         * Set the curve on the builder. This method is not cumulative; repeated calls will overwrite the value.
         *
         * @see ParticlePolygon#setCurve(float) (float)
         */
        public B curve(EasingCurve<Float> curve) {
            this.curve = curve;
            return self();
        }

        @Override
        public ParticlePolygon build() {
            return new ParticlePolygon(this);
        }
    }
}
