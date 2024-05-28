package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4x3fStack;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleTetrahedron extends ParticleObject{
    public DrawInterceptor<ParticleTetrahedron> afterCalcsIntercept;
    public DrawInterceptor<ParticleTetrahedron> beforeCalcsIntercept;
    public DrawInterceptor<ParticleTetrahedron> duringDrawIntercept;

    protected Vec3d vertex1;
    protected Vec3d vertex2;
    protected Vec3d vertex3;
    protected Vec3d vertex4;

    private final IllegalArgumentException UNBALANCED_VERTICES = new IllegalArgumentException(
            "Unbalanced vertices, there must be only 4 vertices"
    );

    public ParticleTetrahedron(ParticleEffect particle, Vec3d[] vertices, Vec3d rotation, int amount) {
        super(particle, rotation);
        if (vertices.length != 4) {
            throw UNBALANCED_VERTICES;
        }
        this.checkForParallelism(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.vertex4 = vertices[3];
        this.amount = amount;
    }

    public ParticleTetrahedron(ParticleEffect particle, Vec3d[] vertices, int amount) {
        super(particle);
        if (vertices.length != 4) {
            throw UNBALANCED_VERTICES;
        }
        this.checkForParallelism(vertices);
        this.vertex1 = vertices[0];
        this.vertex2 = vertices[1];
        this.vertex3 = vertices[2];
        this.vertex4 = vertices[3];
        this.amount = amount;
    }

    public ParticleTetrahedron(ParticleTetrahedron object) {
        super(object);
        this.vertex1 = object.vertex1;
        this.vertex2 = object.vertex2;
        this.vertex3 = object.vertex3;
        this.vertex4 = object.vertex4;
        this.beforeCalcsIntercept = object.beforeCalcsIntercept;
        this.afterCalcsIntercept = object.afterCalcsIntercept;
        this.duringDrawIntercept = object.duringDrawIntercept;
    }

    private void checkValidTetrahedron(Vec3d vertex1, Vec3d vertex2, Vec3d vertex3, Vec3d vertex4) {
        // TODO: find a way to determine if the 4 vertices make a tetrahedron
        if (false) {
            throw new IllegalArgumentException("Provided vertices do not produce a tetrahedron");
        }
    }

    private void checkForParallelism(Vec3d[] vertices) {
        Vec3d vertex1 = vertices[0];
        Vec3d vertex2 = vertices[1];
        Vec3d vertex3 = vertices[2];
        Vec3d vertex4 = vertices[3];
        this.checkValidTetrahedron(vertex1, vertex2, vertex3, vertex4);
    }

    public Vec3d setVertex1(Vec3d vertex1) {
        Vec3d prevVertex1 = this.vertex1;
        this.checkValidTetrahedron(vertex1, this.vertex2, this.vertex3, this.vertex4);
        this.vertex1 = vertex1;
        return prevVertex1;
    }

    public Vec3d setVertex2(Vec3d vertex2) {
        Vec3d prevVertex2 = this.vertex2;
        this.checkValidTetrahedron(this.vertex2, vertex2, this.vertex3, this.vertex4);
        this.vertex2 = vertex2;
        return prevVertex2;
    }

    public Vec3d setVertex3(Vec3d vertex3) {
        Vec3d prevVertex3 = this.vertex3;
        this.checkValidTetrahedron(this.vertex1, this.vertex2, vertex3, this.vertex4);
        this.vertex3 = vertex3;
        return prevVertex3;
    }

    public Vec3d setVertex4(Vec3d vertex4) {
        Vec3d prevVertex4 = this.vertex4;
        this.checkValidTetrahedron(this.vertex2, this.vertex2, this.vertex3, vertex4);
        this.vertex4 = vertex4;
        return prevVertex4;
    }

    public Vec3d getVertex1() {return this.vertex1;}
    public Vec3d getVertex2() {return this.vertex2;}
    public Vec3d getVertex3() {return this.vertex3;}
    public Vec3d getVertex4() {return this.vertex4;}


    protected void drawLine(ServerWorld world, Vec3d vertexStart, Vec3d vertexEnd, int step) {
        InterceptedResult<ParticleTetrahedron> modifiedPairBefore =
                this.interceptDrawCalcBefore(world, vertexStart, vertexEnd, step, this);
        vertexStart = (Vec3d) modifiedPairBefore.interceptData.get("start_position");
        vertexEnd = (Vec3d) modifiedPairBefore.interceptData.get("end_position");
        ParticleTetrahedron currObject = modifiedPairBefore.object;
        double dist = vertexEnd.distanceTo(vertexStart);
        double dirX = (vertexEnd.x - vertexStart.x) / dist;
        double dirY = (vertexEnd.y - vertexStart.y) / dist;
        double dirZ = (vertexEnd.z - vertexStart.z) / dist;
        double interval = dist / currObject.amount;
        Vec3d curr = new Vec3d(vertexStart.x, vertexStart.y, vertexStart.z);
        InterceptedResult<ParticleTetrahedron> modifiedPairAfter =
                this.interceptDrawCalcAfter(world, vertexStart, vertexEnd, curr, step, currObject);
        vertexStart = (Vec3d) modifiedPairAfter.interceptData.get("start_position");
        vertexEnd = (Vec3d) modifiedPairAfter.interceptData.get("end_position");
        curr = (Vec3d) modifiedPairAfter.interceptData.get("draw_position");
        currObject = modifiedPairAfter.object;
        int amountUsed = currObject.amount;
        for (int i = 0; i < amountUsed; i++) {
            world.spawnParticles(
                    this.particle, curr.x, curr.y, curr.z, 0,
                    0.0f, 0.0f, 0.0f, 1
            );
            curr = curr.add((dirX * interval), (dirY * interval), (dirZ * interval));
            InterceptedResult<ParticleTetrahedron> modifiedPairDrawAfter =
                    this.interceptDuringDraw(world, vertexStart, vertexEnd, curr, step, currObject);
            curr = (Vec3d) modifiedPairDrawAfter.interceptData.get("draw_position");
            currObject = modifiedPairAfter.object;
        }
    }

    @Override
    public void draw(ServerWorld world, int step, Vec3d pos) {
        Vec3d vertex0 = this.vertex1.add(pos);
        Vec3d vertex1 = this.vertex2.add(pos);
        Vec3d vertex2 = this.vertex3.add(pos);
        Vec3d vertex3 = this.vertex4.add(pos);
        this.drawLine(world, vertex0, vertex1, step);
        this.drawLine(world, vertex0, vertex2, step);
        this.drawLine(world, vertex0, vertex3, step);
        this.drawLine(world, vertex1, vertex2, step);
        this.drawLine(world, vertex1, vertex3, step);
        this.drawLine(world, vertex2, vertex3, step);
    }

    private InterceptedResult<ParticleTetrahedron> interceptDrawCalcAfter(
            ServerWorld world, Vec3d start, Vec3d end, Vec3d drawPos,
            int step, ParticleTetrahedron obj
    ) {
        InterceptData interceptData = new InterceptData(world, drawPos, step);
        interceptData.put("draw_position", drawPos);
        interceptData.put("start_position", start);
        interceptData.put("end_position", end);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleTetrahedron> interceptDrawCalcBefore(
            ServerWorld world, Vec3d start, Vec3d end, int step, ParticleTetrahedron obj
    ) {
        InterceptData interceptData = new InterceptData(world, null, step);
        interceptData.put("start_position", start);
        interceptData.put("end_position", end);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleTetrahedron> interceptDuringDraw(
            ServerWorld world, Vec3d start, Vec3d end, Vec3d drawPos,
            int step, ParticleTetrahedron obj
    ) {
        InterceptData interceptData = new InterceptData(world, null, step);
        interceptData.put("draw_position", drawPos);
        interceptData.put("start_position", start);
        interceptData.put("end_position", end);
        if (this.duringDrawIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.duringDrawIntercept.apply(interceptData, obj);
    }
}
