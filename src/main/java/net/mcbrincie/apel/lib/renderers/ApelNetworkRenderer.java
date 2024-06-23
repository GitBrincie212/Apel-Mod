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

/** The apel network renderer is used for client-side rendering.
 * It sends off a packet to the client that contains instructions
 * on what shape to render along with the parameters to render it
 */
public class ApelNetworkRenderer implements ApelRenderer {

    private final ServerWorld world;
    private List<Instruction> instructions;

    private ParticleEffect prevParticleEffect;

    /** The default constructor for the client-side rendering.
     * Just like the {@code DefaultApelRenderer}, the ApelNetworkRenderer
     * needs to have a server world instance at its disposal
     *
     * @param world The server world instance
     */
    public ApelNetworkRenderer(ServerWorld world) {
        this.world = world;
        this.instructions = new ArrayList<>();
    }

    /** Creates an instruction in the packet to draw a particle point with the drawPos as a parameter.
     * This instruction is then sent to the client for it to render
     *
     * @param particleEffect The ParticleEffect to draw
     * @param step The step its currently in
     * @param drawPos The position at which to draw
     */
    @Override
    public void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Particle(drawPos));
    }

    /** Creates an instruction in the packet to draw a particle line with the start position, end position and
     * the number of particles as parameters this instruction is then sent to the client which it renders
     * the shape itself
     *
     * @param particleEffect The ParticleEffect to use
     * @param step The step its currently in
     * @param start The 3D point at which to start
     * @param end The 3D point at which to end
     * @param count The number of particles to draw along the line
     */
    @Override
    public void drawLine(ParticleEffect particleEffect, int step, Vector3f start, Vector3f end, int count) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Line(start, end, count));
    }

    /** Creates an instruction in the packet to draw a particle sphere with the drawPos as the center of the sphere,
     * the radius for the sphere, the rotation of the sphere and the number of particles to use.
     * This instruction is then sent to the client which renders the shape itself
     *
     * @param particleEffect The ParticleEffect to use
     * @param step The current rendering step
     * @param drawPos The center position of the sphere
     * @param radius The radius of the sphere
     * @param rotation The rotation of the sphere; this is not terribly useful, but it can be used for
     *         interesting effect, if the number of particles in the sphere changes over time.
     * @param amount The number of particles in the sphere
     */
    @Override
    public void drawSphere(ParticleEffect particleEffect,
                           int step,
                           Vector3f drawPos,
                           float radius,
                           Vector3f rotation,
                           int amount) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Ellipsoid(drawPos, radius, radius, radius, rotation, amount));
    }

    /** Creates an instruction in the packet to draw a particle ellipse with "center" as the center of
     * the ellipse, the "radius" for the ellipse's radius, stretch to control how much stretch
     * should be on the ellipse, rotation to apply.
     * And finally, the number of particles for the ellipse
     *
     * @param particleEffect The ParticleEffect to use
     * @param step The current rendering step
     * @param center The point at the center of the ellipse
     * @param radius The radius of the ellipse
     * @param stretch The stretch of the ellipse
     * @param rotation Rotation applied to the ellipse (to change the plane in which it's drawn)
     * @param amount The number of particles to use to draw the ellipse
     */
    @Override
    public void drawEllipse(ParticleEffect particleEffect,
                            int step,
                            Vector3f center,
                            float radius,
                            float stretch,
                            Vector3f rotation,
                            int amount) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Ellipse(center, radius, stretch, rotation, amount));
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
        // Clear the particle type, so the next frame will send it, too
        this.prevParticleEffect = null;
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

    public sealed interface Instruction {
        void write(RegistryByteBuf buf);
    }

    public record Frame(Vector3f origin) implements Instruction {

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

    public record PType(ParticleEffect particleEffect) implements Instruction {

        static PType from(RegistryByteBuf buf) {
            return new PType(ParticleTypes.PACKET_CODEC.decode(buf));
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('T');
            ParticleTypes.PACKET_CODEC.encode(buf, this.particleEffect);
        }
    }

    public record Particle(Vector3f pos) implements Instruction {

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

    public record Line(Vector3f start, Vector3f end, int amount) implements Instruction {

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

    public record Ellipse(Vector3f center, float radius, float stretch, Vector3f rotation, int amount)
            implements Instruction {

        static Ellipse from(RegistryByteBuf buf) {
            return new Ellipse(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                               buf.readFloat(),
                               buf.readFloat(),
                               new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                               buf.readShort());
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('E');
            buf.writeFloat(center.x);
            buf.writeFloat(center.y);
            buf.writeFloat(center.z);
            buf.writeFloat(radius);
            buf.writeFloat(stretch);
            buf.writeFloat(rotation.x);
            buf.writeFloat(rotation.y);
            buf.writeFloat(rotation.z);
            buf.writeShort(amount);
        }
    }

    public record Ellipsoid(Vector3f drawPos, float radius, float stretch1, float stretch2, Vector3f rotation,
                            int amount) implements Instruction {

        static Ellipsoid from(RegistryByteBuf buf) {
            return new Ellipsoid(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                                 buf.readFloat(),
                                 buf.readFloat(),
                                 buf.readFloat(),
                                 new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                                 buf.readShort());
        }

        @Override
        public void write(RegistryByteBuf buf) {
            buf.writeByte('S');
            buf.writeFloat(drawPos.x);
            buf.writeFloat(drawPos.y);
            buf.writeFloat(drawPos.z);
            buf.writeFloat(radius);
            buf.writeFloat(stretch1);
            buf.writeFloat(stretch2);
            buf.writeFloat(rotation.x);
            buf.writeFloat(rotation.y);
            buf.writeFloat(rotation.z);
            buf.writeShort(amount);
        }
    }
}
