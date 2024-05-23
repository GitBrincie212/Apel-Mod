package net.mcbrincie.apel.lib.objects;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

public class ParticleCuboid extends ParticleObject {
    protected Vec3d size;
    protected Vec3i amount;

    public ParticleCuboid(ParticleEffect particle, Vec3i amount, @NotNull Vec3d size, Vec3d rotation) {
        super(particle, rotation);
        if (size.x <= 0 || size.y <= 0 || size.z <= 0) {
            throw new IndexOutOfBoundsException("One of the size axis is below or equal to zero");
        }
        this.size = size.multiply(0.5f);
        this.amount = amount;
    }

    public ParticleCuboid(ParticleEffect particle, Vec3i amount, @NotNull Vec3d size) {
        super(particle);
        if (size.x <= 0 || size.y <= 0 || size.z <= 0) {
            throw new IndexOutOfBoundsException("One of the size axis is below or equal to zero");
        }
        this.size = size.multiply(0.5f);
        this.amount = amount;
    }

    public ParticleCuboid(ParticleCuboid object) {
        super(object);
        this.size = object.size;
        this.amount = object.amount;
    }

    public Vec3d getVertex(double width, double height, double depth, Vec3d pos) {
        Vec3d vertex = new Vec3d(pos.x + width, pos.y + height, pos.z + depth);
        return vertex
                .rotateZ((float) this.rotation.z)
                .rotateY((float) this.rotation.y)
                .rotateX((float) this.rotation.x);
    }

    public void drawLine(ServerWorld world, Vec3d vertexStart, Vec3d vertexEnd, int amountUsed) {
        if (amountUsed == 0) return;
        double dist = vertexEnd.distanceTo(vertexStart);
        double dirX = (vertexEnd.x - vertexStart.x) / dist;
        double dirY = (vertexEnd.y - vertexStart.y) / dist;
        double dirZ = (vertexEnd.z - vertexStart.z) / dist;
        double interval = dist / amountUsed;
        Vec3d curr = new Vec3d(vertexStart.x, vertexStart.y, vertexStart.z);
        for (int i = 0; i < amountUsed; i++) {
            world.spawnParticles(
                    this.particle, curr.x, curr.y, curr.z, 0,
                    0.0f, 0.0f, 0.0f, 1
            );
            curr = curr.add((dirX * interval), (dirY * interval), (dirZ * interval));
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
        this.drawLine(world, vertex2, vertex4, bottomFaceAmount);
        this.drawLine(world, vertex4, vertex3, bottomFaceAmount);
        this.drawLine(world, vertex3, vertex8, bottomFaceAmount);
        this.drawLine(world, vertex8, vertex2, bottomFaceAmount);

        // Top Face
        this.drawLine(world, vertex1, vertex7, topFaceAmount);
        this.drawLine(world, vertex7, vertex5, topFaceAmount);
        this.drawLine(world, vertex5, vertex6, topFaceAmount);
        this.drawLine(world, vertex6, vertex1, topFaceAmount);

        // Vertical
        this.drawLine(world, vertex5, vertex8, verticalBarsAmount);
        this.drawLine(world, vertex2, vertex6, verticalBarsAmount);
        this.drawLine(world, vertex3, vertex7, verticalBarsAmount);
        this.drawLine(world, vertex1, vertex4, verticalBarsAmount);
    }
}
