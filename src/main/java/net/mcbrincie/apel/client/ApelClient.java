package net.mcbrincie.apel.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mcbrincie.apel.lib.renderers.ApelFramePayload;
import net.mcbrincie.apel.lib.renderers.ApelNetworkRenderer;
import net.mcbrincie.apel.lib.util.math.bezier.BezierCurve;
import net.mcbrincie.apel.lib.util.math.bezier.CubicBezierCurve;
import net.mcbrincie.apel.lib.util.math.bezier.LinearBezierCurve;
import net.mcbrincie.apel.lib.util.math.bezier.ParameterizedBezierCurve;
import net.mcbrincie.apel.lib.util.math.bezier.QuadraticBezierCurve;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.List;

public class ApelClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ApelFramePayload.ID, ((payload, context) -> {
            context.client().execute(() -> {

                ParticleManager particleManager = context.client().particleManager;

                Vector3f frameOrigin = new Vector3f(0);
                ParticleEffect particleEffect = null;
                for (ApelNetworkRenderer.Instruction ins : payload.instructions()) {
                    switch (ins) {
                        case ApelNetworkRenderer.Frame(Vector3f origin) -> frameOrigin = origin;

                        case ApelNetworkRenderer.PType(ParticleEffect pe) -> particleEffect = pe;

                        case ApelNetworkRenderer.Particle(Vector3f pos) ->
                                drawParticle(particleManager, particleEffect, pos);

                        case ApelNetworkRenderer.Line(Vector3f start, Vector3f end, int amount) -> {
                            int amountSubOne = (amount - 1);
                            // Do not use 'sub', it modifies in-place
                            float stepX = (end.x - start.x) / amountSubOne;
                            float stepY = (end.y - start.y) / amountSubOne;
                            float stepZ = (end.z - start.z) / amountSubOne;
                            Vector3f curr = new Vector3f(start);
                            for (int i = 0; i < amount; i++) {
                                drawParticle(particleManager, particleEffect, curr);
                                curr.add(stepX, stepY, stepZ);
                            }
                        }

                        case ApelNetworkRenderer.Ellipse(
                                Vector3f center, float radius, float stretch, Vector3f rotation, int amount
                        ) -> {
                            float angleInterval = (float) Math.TAU / (float) amount;
                            Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
                            for (int i = 0; i < amount; i++) {
                                double currRot = angleInterval * i;
                                float x = (float) Math.cos(currRot) * radius;
                                float y = (float) Math.sin(currRot) * stretch;
                                Vector3f pos = new Vector3f(x, y, 0).rotate(quaternion).add(center);
                                drawParticle(particleManager, particleEffect, pos);
                            }
                        }
                        case ApelNetworkRenderer.Ellipsoid(
                                Vector3f drawPos, float radius, float stretch1, float stretch2, Vector3f rotation,
                                int amount
                        ) -> {
                            final double sqrt5Plus1 = 3.23606;
                            Vector3f scale = new Vector3f(radius, stretch1, stretch2);
                            Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
                            for (int i = 0; i < amount; i++) {
                                // Offset into the real-number distribution
                                float k = i + .5f;
                                // Project point on unit sphere
                                double phi = Math.acos(1f - ((2f * k) / amount));
                                double theta = Math.PI * k * sqrt5Plus1;
                                double sinPhi = Math.sin(phi);
                                float x = (float) (Math.cos(theta) * sinPhi);
                                float y = (float) (Math.sin(theta) * sinPhi);
                                float z = (float) Math.cos(phi);
                                // Scale, rotate, translate
                                Vector3f pos = new Vector3f(x, y, z).mul(scale).rotate(quaternion).add(drawPos);
                                drawParticle(particleManager, particleEffect, pos);
                            }
                        }
                        case ApelNetworkRenderer.BezierCurve(
                                Vector3f drawPos, Vector3f start, List<Vector3f> controlPoints, Vector3f end,
                                Vector3f rotation, int amount
                        ) -> {
                            BezierCurve bezierCurve = switch(controlPoints.size()) {
                                case 0 -> new LinearBezierCurve(start, end);
                                case 1 -> new QuadraticBezierCurve(start, end, controlPoints.get(0));
                                case 2 -> new CubicBezierCurve(start, end, controlPoints.get(0), controlPoints.get(1));
                                default -> new ParameterizedBezierCurve(start, end, controlPoints);
                            };

                            float interval = 1.0f / amount;
                            Quaternionfc quaternion = new Quaternionf().rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);

                            for (int i = 0; i < amount; i++) {
                                Vector3f pos = bezierCurve.compute(interval * i);
                                pos.rotate(quaternion).add(drawPos);
                                drawParticle(particleManager, particleEffect, pos);
                            }
                        }
                    }
                }
            });
        }));
    }

    private void drawParticle(ParticleManager particleManager, ParticleEffect particleEffect, Vector3f pos) {
        particleManager.addParticle(particleEffect, pos.x, pos.y, pos.z, 0.0f, 0.0f, 0.0f);
    }
}
