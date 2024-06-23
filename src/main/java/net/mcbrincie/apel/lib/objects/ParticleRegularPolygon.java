package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Optional;


/** The particle object class that represents a 2D regular polygon. Regular polygons contain the
 * same angles as well as the same edges; Examples include but are not limited to a square, a pentagon,
 * an isosceles triangle... etc. The polygon takes a variable "sides" that dictates which shape to use and
 * calculates the vertices of that shape and connects them
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleRegularPolygon extends ParticleObject {
    private static final Logger LOGGER = LogManager.getLogger();

    protected int sides;
    protected float size;

    protected HashMap<Integer, Vector3f[]> cachedShapes = new HashMap<>();

    private DrawInterceptor<ParticleRegularPolygon, CommonData> afterDraw = DrawInterceptor.identity();
    private DrawInterceptor<ParticleRegularPolygon, CommonData> beforeDraw = DrawInterceptor.identity();

    /** There is no data being transmitted */
    public enum CommonData {}

    /** Constructor for the particle quad which is a 2D Quadrilateral. It accepts as parameters
     * the particle effect to use, the vertices coordinate, the number of particles & the rotation to apply.
     * There is also a simplified version for no rotation.
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param size The size of the regular polygon
     * @param sides The of the regular polygon
     * @param rotation The rotation to apply
     *
     * @see ParticleRegularPolygon#ParticleRegularPolygon(ParticleEffect, int, float, int)
     */
    public ParticleRegularPolygon(ParticleEffect particleEffect, int sides, float size, int amount, Vector3f rotation) {
        super(particleEffect, rotation);
        this.setSides(sides);
        this.setSize(size);
        this.setAmount(amount);
    }

    /** Constructor for the particle quad which is a 2D Quadrilateral. It accepts as parameters
     * the particle to use, the vertices coordinate & the number of particles. There is also
     * a constructor that allows supplying rotation
     *
     * @param particleEffect The particle to use
     * @param amount The number of particles for the object
     * @param sides The sides of the regular polygon
     * @param size The size of the regular polygon
     *
     * @see ParticleRegularPolygon#ParticleRegularPolygon(ParticleEffect, int, float, int, Vector3f)
     */
    public ParticleRegularPolygon(ParticleEffect particleEffect, int sides, float size, int amount) {
        this(particleEffect, sides, size, amount, new Vector3f(0));
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param polygon The particle polygon object to copy from
    */
    public ParticleRegularPolygon(ParticleRegularPolygon polygon) {
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
    public int getSides() {return this.sides;}

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
    public float getSize() {return this.size;}

    protected void fromVertices(ApelRenderer renderer, int step, Vector3f center, Vector3f[] vertices) {
        Vector3f start = vertices[0];
        int index = 1;
        Vector3f nextVertex;
        Quaternionf quaternion = new Quaternionf()
                .rotateZ(this.rotation.z)
                .rotateY(this.rotation.y)
                .rotateX(this.rotation.x);
        int lastIndex = vertices.length - 1;
        for (Vector3f vertex : vertices) {
            nextVertex = vertices[index];
            nextVertex = new Vector3f(nextVertex).rotate(quaternion).add(center);
            vertex = new Vector3f(vertex).rotate(quaternion).add(center);
            this.drawLine(renderer, vertex, nextVertex, step, this.amount);
            index++;
        }
    }

    @Override
    public void draw(ApelRenderer renderer, int step, Vector3f drawPos) {
        this.beforeDraw(renderer.getWorld(), step);

        Vector3f[] vertices = this.cachedShapes.get(this.sides);
        if (vertices == null) {
            float angleInterval = (float) (Math.TAU / this.sides);
            float offset = (float) (2.5f * Math.PI);
            vertices = new Vector3f[this.sides + 1];
            for (int i = 0; i <= this.sides; i++) {
                float currAngle = (angleInterval * i) + offset;
                float x = this.size * trigTable.getCosine(currAngle);
                float y = this.size * trigTable.getSine(currAngle);
                vertices[i] = new Vector3f(x, y, 0);
            }
            this.cachedShapes.put(this.sides, vertices);
        }
        this.fromVertices(renderer, step, drawPos.add(this.offset), vertices);

        this.doAfterDraw(renderer.getWorld(), step);
        this.endDraw(renderer, step, drawPos);
    }

    /** Sets the after draw interceptor, the method executes right after the particle polygon
     * is drawn onto the screen, it has nothing attached.
     *
     * @param afterDraw The new interceptor to use
     */
    public void setAfterDraw(DrawInterceptor<ParticleRegularPolygon, CommonData> afterDraw) {
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
    public void setBeforeDraw(DrawInterceptor<ParticleRegularPolygon, CommonData> beforeDraw) {
        this.beforeDraw = Optional.ofNullable(beforeDraw).orElse(DrawInterceptor.identity());
    }

    private void beforeDraw(ServerWorld world, int step) {
        InterceptData<CommonData> interceptData = new InterceptData<>(world, null, step, CommonData.class);
        this.beforeDraw.apply(interceptData, this);
    }
}
