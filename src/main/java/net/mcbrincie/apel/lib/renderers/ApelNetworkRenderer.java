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
    public void drawSphere(
            ParticleEffect particleEffect, int step, Vector3f drawPos, float radius, Vector3f rotation, int amount
    ) {
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
}
