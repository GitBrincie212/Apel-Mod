package net.mcbrincie.apel.lib.renderers;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.particle.ParticleEffect;
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
public class ApelNetworkRenderer implements ApelServerRenderer {

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

    @Override
    public void drawParticle(ParticleEffect particleEffect, int step, Vector3f drawPos) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Particle(drawPos));
    }

    @Override
    public void drawLine(
            ParticleEffect particleEffect, int step, Vector3f drawPos, Vector3f start, Vector3f end, Vector3f rotation,
            int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Line(drawPos, start, end, rotation, amount));
    }

    @Override
    public void drawEllipsoid(
            ParticleEffect particleEffect, int step, Vector3f drawPos, float xSemiAxis, float ySemiAxis,
            float zSemiAxis, Vector3f rotation, int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Ellipsoid(drawPos, xSemiAxis, ySemiAxis, zSemiAxis, rotation, amount));
    }

    @Override
    public void drawEllipse(
            ParticleEffect particleEffect, int step, Vector3f center, float radius, float stretch, Vector3f rotation,
            int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Ellipse(center, radius, stretch, rotation, amount));
    }

    @Override
    public void drawBezier(
            ParticleEffect particleEffect, int step, Vector3f drawPos,
            net.mcbrincie.apel.lib.util.math.bezier.BezierCurve bezierCurve, Vector3f rotation, int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new BezierCurve(drawPos, bezierCurve, rotation, amount));
    }

    @Override
    public void drawCone(
            ParticleEffect particleEffect, int step, Vector3f drawPos, float height, float radius, Vector3f rotation,
            int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Cone(drawPos, height, radius, rotation, amount));
    }

    @Override
    public void drawCylinder(
            ParticleEffect particleEffect, int step, Vector3f center, float radius, float height, Vector3f rotation,
            int amount
    ) {
        this.detectParticleTypeChange(particleEffect);
        this.instructions.add(new Cylinder(center, radius, height, rotation, amount));
    }

    @Override
    public void beforeFrame(int step, Vector3f frameOrigin) {
        this.instructions.add(new Frame(frameOrigin));
    }

    @Override
    public void afterFrame(int step, Vector3f frameOrigin) {
        ApelFramePayload payload = new ApelFramePayload(this.instructions);
        // Only send if there's more than the Frame instruction (it's always first)
        if (this.instructions.size() > 1) {
            for (ServerPlayerEntity player : PlayerLookup.around(this.getServerWorld(), new Vec3d(frameOrigin), 32)) {
                ServerPlayNetworking.send(player, payload);
            }
        }
        // Recreate, with initial capacity
        this.instructions = new ArrayList<>(this.instructions.size());
        // Clear the particle type, so the next frame will send it, too
        this.prevParticleEffect = null;
    }

    @Override
    public ServerWorld getServerWorld() {
        return this.world;
    }

    private void detectParticleTypeChange(ParticleEffect particleEffect) {
        if (particleEffect != this.prevParticleEffect) {
            this.instructions.add(new PType(particleEffect));
            this.prevParticleEffect = particleEffect;
        }
    }
}
