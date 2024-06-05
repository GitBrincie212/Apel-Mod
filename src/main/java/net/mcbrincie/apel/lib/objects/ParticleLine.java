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
 * any difference
*/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleLine extends ParticleObject {
    protected Vector3f start;
    protected Vector3f end;
    public DrawInterceptor<ParticleLine, ParticleLine.emptyData> afterCalcsIntercept;
    public DrawInterceptor<ParticleLine, ParticleLine.emptyData> beforeCalcsIntercept;

    public enum emptyData {}


    private final CommonUtils commonUtils = new CommonUtils();


    /** Constructor for the particle line which is a line. It accepts as parameters
     * the particle to use, the starting endpoint & the ending endpoint. Rotation
     * doesn't matter in this context,
     *
     * @param particle The particle effect to use
     * @param start The starting endpoint
     * @param end The ending endpoint
     * @param amount The amount of particles
     *
    */
    public ParticleLine(ParticleEffect particle, Vector3f start, Vector3f end, int amount) {
        super(particle);
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
        this.beforeCalcsIntercept = line.beforeCalcsIntercept;
        this.afterCalcsIntercept = line.afterCalcsIntercept;
    }

    /** Sets the starting point of the line
     *
     * @param start The new starting point of the line
     * @return The previous starting point
    */
    public Vector3f setStartEndpoint(Vector3f start) {
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
    public Vector3f setEndEndpoint(Vector3f end) {
        if (end.equals(this.start)) {
            throw new IllegalArgumentException("Endpoints must not be equal");
        }
        Vector3f prevEnd = new Vector3f(this.end);
        this.end = start;
        return prevEnd;
    }

    @Override
    @Deprecated
    public Vector3f getRotation() {return null;}

    @Override
    @Deprecated
    public Vector3f setRotation(Vector3f rotation) {return null;}

    /** Gets the starting endpoint
     *
     * @return The starting endpoint
     */
    public Vector3f getStartEndpoint() {return this.start;}

    /** Gets the ending endpoint
     *
     * @return The ending endpoint
     */
    public Vector3f getEndEndpoint() {return this.end;}


    @Override
    public void draw(ServerWorld world, int step, Vector3f drawPos) {
        InterceptedResult<ParticleLine, ParticleLine.emptyData> modifiedBefore =
                this.interceptDrawCalcBefore(world, step, this);
        ParticleLine objectInUse = modifiedBefore.object;
        commonUtils.drawLine(this, world, this.start, this.end, this.amount);
        this.interceptDrawCalcAfter(world, step, objectInUse);
        this.endDraw(world, step, drawPos);
    }

    private void interceptDrawCalcAfter(
            ServerWorld world, int step, ParticleLine obj
    ) {
        InterceptData<ParticleLine.emptyData> interceptData = new InterceptData<>(world, null, step, ParticleLine.emptyData.class);
        if (this.afterCalcsIntercept == null) return;
        this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleLine, ParticleLine.emptyData> interceptDrawCalcBefore(
            ServerWorld world, int step, ParticleLine obj
    ) {
        InterceptData<ParticleLine.emptyData> interceptData = new InterceptData<>(world, null, step, ParticleLine.emptyData.class);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
