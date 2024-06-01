package net.mcbrincie.apel.lib.objects;

import net.mcbrincie.apel.lib.util.CommonUtils;
import net.mcbrincie.apel.lib.util.interceptor.DrawInterceptor;
import net.mcbrincie.apel.lib.util.interceptor.InterceptData;
import net.mcbrincie.apel.lib.util.interceptor.InterceptedResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleTetrahedron extends ParticleObject{
    public DrawInterceptor<ParticleTetrahedron, afterCalc> afterCalcsIntercept;
    public DrawInterceptor<ParticleTetrahedron, beforeCalc> beforeCalcsIntercept;

    private final CommonUtils commonUtils = new CommonUtils();

    public enum afterCalc {
        DRAW_POSITION, END_VERTEX, START_VERTEX
    }

    public enum beforeCalc {
        END_VERTEX, START_VERTEX
    }

    protected Vector3f vertex1;
    protected Vector3f vertex2;
    protected Vector3f vertex3;
    protected Vector3f vertex4;

    private final IllegalArgumentException UNBALANCED_VERTICES = new IllegalArgumentException(
            "Unbalanced vertices, there must be only 4 vertices"
    );

    public ParticleTetrahedron(ParticleEffect particle, Vector3f[] vertices, Vector3f rotation, int amount) {
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

    public ParticleTetrahedron(ParticleEffect particle, Vector3f[] vertices, int amount) {
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
    }

    private void checkValidTetrahedron(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
        float result = ((vertex2.sub(vertex1).cross(vertex3.sub(vertex1))).dot(vertex4.sub(vertex1)));
        if (Math.abs(result) > 0.0001f) {
            throw new IllegalArgumentException("Provided vertices do not produce a tetrahedron");
        }
    }

    private void checkForParallelism(Vector3f[] vertices) {
        Vector3f vertex1 = vertices[0];
        Vector3f vertex2 = vertices[1];
        Vector3f vertex3 = vertices[2];
        Vector3f vertex4 = vertices[3];
        this.checkValidTetrahedron(vertex1, vertex2, vertex3, vertex4);
    }

    public Vector3f setVertex1(Vector3f vertex1) {
        Vector3f prevVertex1 = this.vertex1;
        this.checkValidTetrahedron(vertex1, this.vertex2, this.vertex3, this.vertex4);
        this.vertex1 = vertex1;
        return prevVertex1;
    }

    public Vector3f setVertex2(Vector3f vertex2) {
        Vector3f prevVertex2 = this.vertex2;
        this.checkValidTetrahedron(this.vertex2, vertex2, this.vertex3, this.vertex4);
        this.vertex2 = vertex2;
        return prevVertex2;
    }

    public Vector3f setVertex3(Vector3f vertex3) {
        Vector3f prevVertex3 = this.vertex3;
        this.checkValidTetrahedron(this.vertex1, this.vertex2, vertex3, this.vertex4);
        this.vertex3 = vertex3;
        return prevVertex3;
    }

    public Vector3f setVertex4(Vector3f vertex4) {
        Vector3f prevVertex4 = this.vertex4;
        this.checkValidTetrahedron(this.vertex2, this.vertex2, this.vertex3, vertex4);
        this.vertex4 = vertex4;
        return prevVertex4;
    }

    public Vector3f getVertex1() {return this.vertex1;}
    public Vector3f getVertex2() {return this.vertex2;}
    public Vector3f getVertex3() {return this.vertex3;}
    public Vector3f getVertex4() {return this.vertex4;}

    @Override
    public void draw(ServerWorld world, int step, Vector3f pos) {
        InterceptedResult<ParticleTetrahedron, beforeCalc> modifiedBefore =
                this.interceptDrawCalcBefore(world, step, pos, this);
        ParticleTetrahedron objectToUse = modifiedBefore.object;
        Vector3f vertex0 = this.vertex1.add(pos);
        Vector3f vertex1 = this.vertex2.add(pos);
        Vector3f vertex2 = this.vertex3.add(pos);
        Vector3f vertex3 = this.vertex4.add(pos);
        commonUtils.drawLine(this, world, vertex0, vertex1, this.amount);
        commonUtils.drawLine(this, world, vertex0, vertex2, this.amount);
        commonUtils.drawLine(this, world, vertex0, vertex3, this.amount);
        commonUtils.drawLine(this, world, vertex1, vertex2, this.amount);
        commonUtils.drawLine(this, world, vertex1, vertex3, this.amount);
        commonUtils.drawLine(this, world, vertex2, vertex3, this.amount);
        this.interceptDrawCalcAfter(world, step, pos, this);
        this.endDraw(world, step, pos);
    }

    private InterceptedResult<ParticleTetrahedron, afterCalc> interceptDrawCalcAfter(
            ServerWorld world, int step, Vector3f pos, ParticleTetrahedron obj
    ) {
        InterceptData<afterCalc> interceptData = new InterceptData<>(world, pos, step, afterCalc.class);
        if (this.afterCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.afterCalcsIntercept.apply(interceptData, obj);
    }

    private InterceptedResult<ParticleTetrahedron, beforeCalc> interceptDrawCalcBefore(
            ServerWorld world, int step, Vector3f pos, ParticleTetrahedron obj
    ) {
        InterceptData<beforeCalc> interceptData = new InterceptData<>(world, pos, step, beforeCalc.class);
        if (this.beforeCalcsIntercept == null) return new InterceptedResult<>(interceptData, this);
        return this.beforeCalcsIntercept.apply(interceptData, obj);
    }
}
