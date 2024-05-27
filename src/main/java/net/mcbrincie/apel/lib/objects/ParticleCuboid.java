package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

/** The particle object class that represents a cuboid. Which is a rectangle
 * living in 3D, it can also be a cube if all the values of the size vector
 * are supplied with the same value. You can also specify which faces you want
 * to be rendered(for example you can remove the bottom face). It also adds a
 * new interceptor called {@code duringDrawIntercept} which is used during the
 * drawing of the particle lines
 */
public class ParticleCuboid extends ParticleObject {
    public Vec3d size;
    protected Vec3i amount;
    public DrawInterceptor<ParticleCuboid> afterCalcsIntercept;
    public DrawInterceptor<ParticleCuboid> beforeCalcsIntercept;
    public DrawInterceptor<ParticleCuboid> duringDrawIntercept;

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle to use, the amount of particles per face section(bottom is X, top is Y and the bars are Z)
     * the size of the cuboid(width, height, depth) & the rotation to apply There is also a simplified version
     * for no rotation.
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param size The size in regard to width, height, depth
     * @param rotation The rotation to apply
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vec3i, Vec3d)
     */
    public ParticleCuboid(ParticleEffect particle, Vec3i amount, @NotNull Vec3d size, Vec3d rotation) {
        super(particle, rotation);
        if (size.x <= 0 || size.y <= 0 || size.z <= 0) {
            throw new IndexOutOfBoundsException("One of the size axis is below or equal to zero");
        }
        this.size = size.multiply(0.5f);
        this.amount = amount;
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle to use, the amount of particles per face section(bottom is X, top is Y and the bars are Z)
     * the size of the cuboid(width, height, depth). It is a simplified version for the case when
     * no rotation is meant to be applied. For rotation offset you can use another constructor
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param size The size in regard to width, height, depth
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vec3i, Vec3d, Vec3d)
     */
    public ParticleCuboid(ParticleEffect particle, Vec3i amount, @NotNull Vec3d size) {
        super(particle);
        if (size.x <= 0 || size.y <= 0 || size.z <= 0) {
            throw new IndexOutOfBoundsException("One of the size axis is below or equal to zero");
        }
        this.size = size.multiply(0.5f);
        this.amount = amount;
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle to use, the amount of particles per face section(bottom is X, top is Y and the bars are Z)
     * the size of the cuboid(width, height, depth). It is a simplified version for the case when
     * no rotation is meant to be applied. This constructor is meant when you want a constant amount of
     * particles per face section. There is a constructor that allows to handle different amounts per face
     * and another that is meant to be used when no rotation is meant to be applied
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param size The size in regard to width, height, depth
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vec3i, Vec3d, Vec3d)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vec3d)
     */
    public ParticleCuboid(ParticleEffect particle, int amount, @NotNull Vec3d size, Vec3d rotation) {
        super(particle, rotation);
        if (size.x <= 0 || size.y <= 0 || size.z <= 0) {
            throw new IndexOutOfBoundsException("One of the size axis is below or equal to zero");
        }
        this.size = size.multiply(0.5f);
        this.amount = Vec3i.ZERO.add(amount, amount, amount);
    }

    /** Constructor for the particle cuboid which is a 3D rectangle. It accepts as parameters
     * the particle to use, the amount of particles per face section(bottom is X, top is Y and the bars are Z)
     * the size of the cuboid(width, height, depth) It is a simplified version for the case when
     * no rotation is meant to be applied. This constructor is meant when you want a constant amount of
     * particles per face section. There is a constructor that allows to handle different amounts per face
     * and another that is meant to be used when no rotation is meant to be applied
     *
     * @param particle The particle to use
     * @param amount The amount of particles for the object
     * @param size The size in regard to width, height, depth
     *
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, Vec3i, Vec3d)
     * @see ParticleCuboid#ParticleCuboid(ParticleEffect, int, Vec3d, Vec3d)
     */
    public ParticleCuboid(ParticleEffect particle, int amount, @NotNull Vec3d size) {
        super(particle);
        if (size.x <= 0 || size.y <= 0 || size.z <= 0) {
            throw new IndexOutOfBoundsException("One of the size axis is below or equal to zero");
        }
        this.size = size.multiply(0.5f);
        this.amount = Vec3i.ZERO.add(amount, amount, amount);
    }

    public ParticleCuboid(ParticleCuboid object) {
        super(object);
        this.size = object.size;
        this.amount = object.amount;
    }

    /** Gets the vertex given the size and the center position
     *
     * @param width The width
     * @param height The height
     * @param depth The depth
     * @param pos The center position
     * @return The vertex's coordinates
     */
    public Vec3d getVertex(double width, double height, double depth, Vec3d pos) {
        Vec3d vertex = new Vec3d(width, height, depth);
        vertex = vertex
                .rotateZ((float) this.rotation.z)
                .rotateY((float) this.rotation.y)
                .rotateX((float) this.rotation.x);
        return vertex.add(pos);
    }

    /** Draws a particle line along 2 vertices
     *
     * @param world
     * @param vertexStart
     * @param vertexEnd
     * @param step
     * @param amountUsed
    */
    public void drawLine(ServerWorld world, Vec3d vertexStart, Vec3d vertexEnd, int step, int amountUsed) {
        if (amountUsed == 0) return;
        InterceptedResult<ParticleCuboid> modifiedPairBefore =
                this.interceptDrawCalcBefore(world, vertexStart, vertexEnd, step, amountUsed, this);
        vertexStart = (Vec3d) modifiedPairBefore.interceptData.get("start_position");
        vertexEnd = (Vec3d) modifiedPairBefore.interceptData.get("end_position");
        amountUsed = (int) modifiedPairBefore.interceptData.get("amount");
        double dist = vertexEnd.distanceTo(vertexStart);
        double dirX = (vertexEnd.x - vertexStart.x) / dist;
        double dirY = (vertexEnd.y - vertexStart.y) / dist;
        double dirZ = (vertexEnd.z - vertexStart.z) / dist;
        double interval = dist / amountUsed;
        Vec3d curr = new Vec3d(vertexStart.x, vertexStart.y, vertexStart.z);
        InterceptedResult<ParticleCuboid> modifiedPairAfter =
                this.interceptDrawCalcAfter(world, vertexStart, vertexEnd, curr, step, amountUsed, this);
        vertexStart = (Vec3d) modifiedPairAfter.interceptData.get("start_position");
        vertexEnd = (Vec3d) modifiedPairAfter.interceptData.get("end_position");
        amountUsed = (int) modifiedPairAfter.interceptData.get("amount");
        curr = (Vec3d) modifiedPairAfter.interceptData.get("draw_position");
        for (int i = 0; i < amountUsed; i++) {
            world.spawnParticles(
                    this.particle, curr.x, curr.y, curr.z, 0,
                    0.0f, 0.0f, 0.0f, 1
            );
            curr = curr.add((dirX * interval), (dirY * interval), (dirZ * interval));
            InterceptedResult<ParticleCuboid> modifiedPairDrawAfter =
                    this.interceptDuringDraw(world, vertexStart, vertexEnd, curr, step, amountUsed, this);
            curr = (Vec3d) modifiedPairDrawAfter.interceptData.get("draw_position");
        }
    }

    @Override
    public void draw(ServerWorld world, int step, Vec3d pos) {
        double width = size.x;
        double height = size.y;
        double depth = size.z;
        Vec3d vertex1 = this.getVertex(width, height, depth, pos);
        Vec3d vertex2 = this.getVertex(width, -height, -depth, pos);
        Vec3d vertex3 = this.getVertex(-width, height, -depth, pos);
        Vec3d vertex4 = this.getVertex(width, height, -depth, pos);
        Vec3d vertex5 = this.getVertex(-width, -height, depth, pos);
        Vec3d vertex6 = this.getVertex(width, -height, depth, pos);
        Vec3d vertex7 = this.getVertex(-width, height, depth, pos);
        Vec3d vertex8 = this.getVertex(-width, -height, -depth, pos);

        int bottomFaceAmount = this.amount.getX();
        int topFaceAmount = this.amount.getY();
        int verticalBarsAmount = this.amount.getZ();
        // Bottom Face
        this.drawLine(world, vertex2, vertex4, step, bottomFaceAmount);
        this.drawLine(world, vertex4, vertex3, step, bottomFaceAmount);
        this.drawLine(world, vertex3, vertex8, step, bottomFaceAmount);
        this.drawLine(world, vertex8, vertex2, step, bottomFaceAmount);

        // Top Face
        this.drawLine(world, vertex1, vertex7, step, topFaceAmount);
        this.drawLine(world, vertex7, vertex5, step, topFaceAmount);
        this.drawLine(world, vertex5, vertex6, step, topFaceAmount);
        this.drawLine(world, vertex6, vertex1, step, topFaceAmount);

        // Vertical
        this.drawLine(world, vertex5, vertex8, step, verticalBarsAmount);
        this.drawLine(world, vertex2, vertex6, step, verticalBarsAmount);
        this.drawLine(world, vertex3, vertex7, step, verticalBarsAmount);
        this.drawLine(world, vertex1, vertex4, step, verticalBarsAmount);
    }

    private InterceptedResult<ParticleCuboid> interceptDrawCalcAfter(
            ServerWorld world, Vec3d start, Vec3d end, Vec3d drawPos,
            int step, int amountUsed, ParticleCuboid obj
    ) {
        InterceptData interceptData = new InterceptData(world, drawPos, step);
        interceptData.put("draw_position", drawPos);
        interceptData.put("start_position", start);
        interceptData.put("end_position", end);
        interceptData.put("amount", amountUsed);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleCuboid> interceptDrawCalcBefore(
            ServerWorld world, Vec3d start, Vec3d end, int step, int amountUsed, ParticleCuboid obj
    ) {
        InterceptData interceptData = new InterceptData(world, null, step);
        interceptData.put("start_position", start);
        interceptData.put("end_position", end);
        interceptData.put("amount", amountUsed);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleCuboid> interceptDuringDraw(
            ServerWorld world, Vec3d start, Vec3d end, Vec3d drawPos,
            int step, int amountUsed, ParticleCuboid obj
    ) {
        InterceptData interceptData = new InterceptData(world, null, step);
        interceptData.put("draw_position", drawPos);
        interceptData.put("start_position", start);
        interceptData.put("end_position", end);
        interceptData.put("amount", amountUsed);
        if (this.duringDrawIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.duringDrawIntercept.apply(interceptData, obj);
    }
}
