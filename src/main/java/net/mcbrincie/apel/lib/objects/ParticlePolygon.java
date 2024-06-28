package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.Apel;
import net.mcbrincie.apel.lib.renderers.ApelServerRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Optional;


/** The particle object class that represents a 2D regular polygon. Regular polygons contain the
 * same angles as well as the same edges; Examples include but are not limited to a square, a pentagon,
 * an isosceles triangle... etc. The polygon takes a variable "sides" that dictates which shape to use and
 * calculates the vertices of that shape and connects them
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticlePolygon extends ParticleObject {
    private static final Logger LOGGER = LogManager.getLogger();

    protected int sides;
    protected float size;

    protected HashMap<Integer, Vector3f[]> cachedShapes = new HashMap<>();

    private DrawInterceptor<ParticlePolygon, CommonData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticlePolygon, CommonData> beforeDraw = DrawInterceptor.identity();

    /** There is no data being transmitted */
    public enum CommonData {}

    /** Constructor for the particle polygon which is a 2D regular polygon(shape).
     * It accepts as parameters the particle effect to use, the sides of the polygon,
     * the size of the polygon, the number of particles, and the rotation to apply.
     * There is also a simplified version for no rotation.
     *
     * @param particleEffect The particle to use
     * @param sides The of the regular polygon
     * @param size The size of the regular polygon
     * @param amount The number of particles for the object
     * @param rotation The rotation to apply
     *
     * @see ParticlePolygon#ParticlePolygon(ParticleEffect, int, float, int)
     */
    public ParticlePolygon(ParticleEffect particleEffect, int sides, float size, int amount, Vector3f rotation) {
        super(particleEffect, rotation);
        this.setSides(sides);
        this.setSize(size);
        this.setAmount(amount);
    }

    /** Constructor for the particle polygon which is a 2D regular polygon(shape).
     * It accepts as parameters the particle effect to use, the sides of the polygon,
     * the size of the polygon, and the number of particles.
     * There is also a more complex version for supplying rotation.
     *
     * @param particleEffect The particle to use
     * @param size The size of the regular polygon
     * @param sides The sides of the regular polygon
     * @param amount The number of particles for the object
     *
     * @see ParticlePolygon#ParticlePolygon(ParticleEffect, int, float, int, Vector3f)
     */
    public ParticlePolygon(ParticleEffect particleEffect, int sides, float size, int amount) {
        this(particleEffect, sides, size, amount, new Vector3f(0));
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param polygon The particle polygon object to copy from
    */
    public ParticlePolygon(ParticlePolygon polygon) {
        super(polygon);
        this.sides = polygon.sides;
        this.size = polygon.size;
        this.cachedShapes = polygon.cachedShapes;
        this.beforeDraw = polygon.beforeDraw;
        this.afterDraw = polygon.afterDraw;
    }

    /** Sets the sides to a new value and returns the previous sides used
     *
     * @param sides The new sides
     * @throws IllegalArgumentException If the sides are less than 3
     * @return The previous sides used
     */
    public int setSides(int sides) throws IllegalArgumentException {
        if (sides < 3) {
            throw new IllegalArgumentException("Cannot produce a polygon with sides below 3");
        }
        int prevSides = this.sides;
        this.sides = sides;
        return prevSides;
    }

    /** Gets the sides of the regular polygon
     *
     * @return The sides of the regular polygon
     */
    public int getSides() {
        return this.sides;
    }

    /** Sets the size of the polygon to a new value and returns the previous size used
     *
     * @param size The new size
     * @throws IllegalArgumentException If the size is negative or 0
     * @return The previous size used
     */
    public float setSize(float size) throws IllegalArgumentException {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be non-negative and above 0");
        }
        float prevSize = this.size;
        this.size = size;
        return prevSize;
    }

    /** Gets the size of the regular polygon
     *
     * @return The size of the regular polygon
     */
    public float getSize() {
        return this.size;
    }

    @Override
    public void draw(ApelServerRenderer renderer, int step, Vector3f drawPos) {
        this.doBeforeDraw(renderer.getServerWorld(), step);

        Vector3f[] vertices = getRawVertices();
        // Defensive copy
        Vector3f objectDrawPos = new Vector3f(drawPos).add(this.offset);
        this.computeVertices(vertices, objectDrawPos);

        // Divide the particles evenly among sides
        int particlesPerLine = this.amount / this.sides;
        for (int i = 0; i < vertices.length - 1; i++) {
            renderer.drawLine(this.particleEffect, step, vertices[i], vertices[i + 1], particlesPerLine);
        }

        this.doAfterDraw(renderer.getServerWorld(), step);
        this.endDraw(renderer, step, drawPos);
    }

    private @NotNull Vector3f[] getRawVertices() {
        // Cache the vertices, does not rotate or offset
        // (since these change a lot and the goal is to use the cache as much as possible)
        Vector3f[] cachedVertices = this.cachedShapes.computeIfAbsent(this.sides, sides -> {
            float angleInterval = (float) (Math.TAU / this.sides);
            // Apply this angle offset to make it point up to 0º
            float offset = (float) (2.5f * Math.PI);
            // Sides + 1 to repeat the starting point to prevent modulo math later
            Vector3f[] newVertices = new Vector3f[this.sides + 1];
            for (int i = 0; i < this.sides; i++) {
                float currAngle = (angleInterval * i) + offset;
                float x = this.size * Apel.TRIG_TABLE.getCosine(currAngle);
                float y = this.size * Apel.TRIG_TABLE.getSine(currAngle);
                newVertices[i] = new Vector3f(x, y, 0);
            }
            // Ensure the last particle is exactly the same as the first
            newVertices[this.sides] = new Vector3f(newVertices[0]);
            return newVertices;
        });
        // Defensive copy of vertices so the cache isn't corrupted
        Vector3f[] verticesCopy = new Vector3f[cachedVertices.length];
        for (int i = 0; i < cachedVertices.length; i++) {
            verticesCopy[i] = new Vector3f(cachedVertices[i]);
        }
        return verticesCopy;
    }

    private void computeVertices(Vector3f[] vertices, Vector3f center) {
        Quaternionf quaternion = new Quaternionf().rotateZ(this.rotation.z)
                                                  .rotateY(this.rotation.y)
                                                  .rotateX(this.rotation.x);
        for (Vector3f vertex : vertices) {
            vertex.rotate(quaternion).add(center);
        }
    }

    /** Sets the after draw interceptor, the method executes right after the particle polygon
     * is drawn onto the screen, it has nothing attached.
     *
     * @param afterDraw The new interceptor to use
     */
    public void setAfterDraw(DrawInterceptor<ParticlePolygon, CommonData> afterDraw) {
        this.afterDraw = Optional.ofNullable(afterDraw).orElse(DrawInterceptor.identity());
    }

    private void doAfterDraw(ServerWorld world, int step) {
        InterceptData<CommonData> interceptData = new InterceptData<>(world, null, step, CommonData.class);
        this.afterDraw.apply(interceptData, this);
    }

    /** Sets the before draw interceptor, the method executes right before the particle polygon
     * is drawn onto the screen, it has nothing attached.
     *
     * @param beforeDraw The new interceptor to use
     */
    public void setBeforeDraw(DrawInterceptor<ParticlePolygon, CommonData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void doBeforeDraw(ServerWorld world, int step) {
        InterceptData<CommonData> interceptData = new InterceptData<>(world, null, step, CommonData.class);
        this.beforeDraw.apply(interceptData, this);
    }
}
