package net.mcbrincie.apel.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mcbrincie.apel.lib.renderers.ApelFramePayload;
import net.mcbrincie.apel.lib.renderers.ApelNetworkRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

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
                    }
                }
            });
        }));
    }

    private void drawParticle(ParticleManager particleManager, ParticleEffect particleEffect, Vector3f pos) {
        particleManager.addParticle(particleEffect, pos.x, pos.y, pos.z, 0.0f, 0.0f, 0.0f);
    }
}
