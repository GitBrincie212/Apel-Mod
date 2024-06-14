package net.mcbrincie.apel.lib.renderers;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ApelNetworkRenderer implements ApelRenderer {

    private final ServerWorld world;
    private List<Instruction> instructions;

    private ParticleEffect prevParticleEffect;

    public ApelNetworkRenderer(ServerWorld world) {
        this.world = world;
        this.instructions = new ArrayList<>();
    }

    @Override
    public void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Particle(drawPos));
    }

    @Override
    public void drawLine(ParticleEffect particleEffect, int step, Vector3f start, Vector3f end, int count) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Line(start, end, count));
    }

    @Override
    public void beforeFrame(int step, Vector3f frameOrigin) {
        ApelRenderer.super.beforeFrame(step, frameOrigin);
        this.instructions.add(new Frame(frameOrigin));
    }

    @Override
    public void afterFrame(int step, Vector3f frameOrigin) {
        ApelFramePayload payload = new ApelFramePayload(this.instructions);
        for (ServerPlayerEntity player : PlayerLookup.around(this.getWorld(), new Vec3d(frameOrigin), 32)) {
            ServerPlayNetworking.send(player, payload);
        }
        // Recreate, with initial capacity
        this.instructions = new ArrayList<>(this.instructions.size());
    }

    @Override
    public ServerWorld getWorld() {
        return this.world;
    }

    private void detectParticleTypeChange(ParticleEffect particleEffect) {
        if (particleEffect != this.prevParticleEffect) {
            this.instructions.add(new PType(particleEffect));
            this.prevParticleEffect = particleEffect;
        }
    }

    sealed interface Instruction {
        void write(RegistryByteBuf buf);
    }

    record Frame(Vector3f origin) implements Instruction {

        static Frame from(RegistryByteBuf buf) {
            return new Frame(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()));
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('F');
            buf.writeFloat(origin.x);
            buf.writeFloat(origin.y);
            buf.writeFloat(origin.z);
        }
    }

    record PType(ParticleEffect particleEffect) implements Instruction {

        static PType from(RegistryByteBuf buf) {
            return new PType(ParticleTypes.PACKET_CODEC.decode(buf));
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('T');
            ParticleTypes.PACKET_CODEC.encode(buf, this.particleEffect);
        }
    }

    record Particle(Vector3f pos) implements Instruction {

        static Particle from(RegistryByteBuf buf) {
            return new Particle(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()));
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('P');
            buf.writeFloat(pos.x);
            buf.writeFloat(pos.y);
            buf.writeFloat(pos.z);
        }
    }

    record Line(Vector3f start, Vector3f end, int amount) implements Instruction {

        static Line from(RegistryByteBuf buf) {
            return new Line(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                            new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                            buf.readShort());
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('L');
            buf.writeFloat(start.x);
            buf.writeFloat(start.y);
            buf.writeFloat(start.z);
            buf.writeFloat(end.x);
            buf.writeFloat(end.y);
            buf.writeFloat(end.z);
            buf.writeShort(amount);
        }
    }
}
