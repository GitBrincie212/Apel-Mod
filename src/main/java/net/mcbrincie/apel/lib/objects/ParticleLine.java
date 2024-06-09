package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.CommonUtils;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

/** The particle object class that represents a 2D line. It is one of the
 * most simple objects to use as it needs only a start & an ending position
 * in order to draw that line. The line cannot be curved and is only linear.
 * <br><br>
 * <b>Note:</b> rotation won't be applied to the calculations, as such it doesn't make
 * any difference.
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleLine extends ParticleObject {
    protected Vector3f start;
    protected Vector3f end;

    private DrawInterceptor<ParticleLine, AfterDrawData> afterDraw;
    private DrawInterceptor<ParticleLine, BeforeDrawData> beforeDraw;

    public enum BeforeDrawData {}
    public enum AfterDrawData {}

    private final CommonUtils commonUtils = new CommonUtils();

    /** Constructor for the particle line which is a line. It accepts as parameters
     * the particle to use, the starting endpoint & the ending endpoint. Rotation
     * doesn't matter in this context.
     *
     * @param particleEffect The particle effect to use
     * @param start The starting endpoint
     * @param end The ending endpoint
     * @param amount The amount of particles
    */
    public ParticleLine(ParticleEffect particleEffect, Vector3f start, Vector3f end, int amount) {
        super(particleEffect);
        this.setAmount(amount);
        if (start.equals(end)) {
            throw new IllegalArgumentException("Endpoints must not be equal");
        }
        this.start = start;
        this.end = end;
    }

    /** The copy constructor for a specific particle object. It copies all
     * the params, including the interceptors the particle object has
     *
     * @param line The particle object to copy from
    */
    public ParticleLine(ParticleLine line) {
        super(line);
        this.end = line.end;
        this.start = line.start;
        this.beforeDraw = line.beforeDraw;
        this.afterDraw = line.afterDraw;
    }

    /** Sets the starting point of the line
     *
     * @param start The new starting point of the line
     * @return The previous starting point
    */
    public Vector3f setStart(Vector3f start) {
        if (start.equals(this.end)) {
            throw new IllegalArgumentException("Endpoints must not be equal");
        }
        Vector3f prevStart = new Vector3f(this.start);
        this.start = start;
        return prevStart;
    }

    /** Sets the ending point of the line
     *
     * @param end The new ending point of the line
     * @return The previous ending point
    */
    public Vector3f setEnd(Vector3f end) {
        if (end.equals(this.start)) {
            throw new IllegalArgumentException("Endpoints must not be equal");
        }
        Vector3f prevEnd = new Vector3f(this.end);
        this.end = start;
        return prevEnd;
    }

    @Override
    @Deprecated
    public Vector3f getRotation() {
        return null;
    }

    @Override
    @Deprecated
    public Vector3f setRotation(Vector3f rotation) {
        return null;
    }

    /** Gets the starting endpoint
     *
     * @return The starting endpoint
     */
    public Vector3f getStart() {
        return this.start;
    }

    /** Gets the ending endpoint
     *
     * @return The ending endpoint
     */
    public Vector3f getEnd() {
        return this.end;
    }

    @Override
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptedResult<ParticleLine, BeforeDrawData> modifiedBefore = this.doBeforeDraw(world, step);
        ParticleLine objectInUse = modifiedBefore.object;
        commonUtils.drawLine(this, world, this.start, this.end, this.amount);
        this.doAfterDraw(world, step);
        this.endDraw(world, step, drawPos);
    }

    /** Sets the interceptor to run after drawing the line.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleLine
     * instance.
     *
     * @param afterDraw the new interceptor to execute after drawing the line
     */
    @Override
    public void setAfterDraw(DrawInterceptor<ParticleLine, AfterDrawData> afterDraw) {
        this.afterDraw = afterDraw;
    }

    private void doAfterDraw(ServerWorld world, int step) {
        InterceptData<AfterDrawData> interceptData = new InterceptData<>(world, null, step, AfterDrawData.class);
        if (this.afterDraw == null) return;
        this.afterDraw.apply(interceptData, this);
    }

    /** Set the interceptor to run before drawing the line.  The interceptor will be provided
     * with references to the {@link ServerWorld}, the animation step number, and the ParticleLine
     * instance.
     *
     * @param beforeDraw the new interceptor to execute before drawing the line
     */
    @Override
    public void setBeforeDraw(DrawInterceptor<ParticleLine, BeforeDrawData> beforeDraw) {
        this.beforeDraw = beforeDraw;
    }

    private InterceptedResult<ParticleLine, BeforeDrawData> doBeforeDraw(ServerWorld world, int step) {
        InterceptData<BeforeDrawData> interceptData = new InterceptData<>(world, null, step, BeforeDrawData.class);
        if (this.beforeDraw == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeDraw.apply(interceptData, this);
    }
}
