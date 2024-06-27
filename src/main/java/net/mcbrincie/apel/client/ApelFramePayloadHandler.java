package net.mcbrincie.apel.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mcbrincie.apel.lib.renderers.ApelFramePayload;
import net.mcbrincie.apel.lib.renderers.ApelRenderer;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;

public class ApelFramePayloadHandler implements ClientPlayNetworking.PlayPayloadHandler<ApelFramePayload> {

    private final ParticleManagerRenderer renderer;

    public ApelFramePayloadHandler(ParticleManagerRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void receive(ApelFramePayload payload, ClientPlayNetworking.Context context) {
        // TODO: Can this be done once instead of every single payload?
        renderer.setParticleManager(context.client().particleManager);

        context.client().execute(() -> {

            Vector3f frameOrigin = new Vector3f(0);
            ParticleEffect particleEffect = null;
            for (ApelRenderer.Instruction ins : payload.instructions()) {
                switch (ins) {
                    case ApelRenderer.Frame(Vector3f origin) -> frameOrigin = origin;

                    case ApelRenderer.PType(ParticleEffect pe) -> particleEffect = pe;

                    case ApelRenderer.Particle(Vector3f pos) -> renderer.drawParticle(particleEffect, 0, pos);

                    case ApelRenderer.Line(Vector3f start, Vector3f end, int amount) ->
                            renderer.drawLine(particleEffect, 0, start, end, amount);

                    case ApelRenderer.Ellipse(
                            Vector3f center, float radius, float stretch, Vector3f rotation, int amount
                    ) -> renderer.drawEllipse(particleEffect, 0, center, radius, stretch, rotation, amount);

                    case ApelRenderer.Ellipsoid(
                            Vector3f drawPos, float xSemiAxis, float ySemiAxis, float zSemiAxis, Vector3f rotation,
                            int amount
                    ) -> renderer.drawEllipsoid(particleEffect, 0, drawPos, xSemiAxis, ySemiAxis, zSemiAxis, rotation,
                                                amount
                    );

                    case ApelRenderer.BezierCurve(
                            Vector3f drawPos, BezierCurve bezierCurve, Vector3f rotation, int amount
                    ) -> renderer.drawBezier(particleEffect, 0, drawPos, bezierCurve, rotation, amount);

                    case ApelRenderer.Cone(
                            Vector3f drawPos, float height, float radius, Vector3f rotation, int amount
                    ) -> renderer.drawCone(particleEffect, 0, drawPos, height, radius, rotation, amount);

                    case ApelRenderer.Cylinder(
                            Vector3f drawPos, float radius, float height, Vector3f rotation, int amount
                    ) -> renderer.drawCylinder(particleEffect, 0, drawPos, radius, height, rotation, amount);
                }
            }
        });
    }
}
