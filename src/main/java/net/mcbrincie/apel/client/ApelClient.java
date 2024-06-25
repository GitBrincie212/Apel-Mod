package net.mcbrincie.apel.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mcbrincie.apel.lib.renderers.ApelFramePayload;

public class ApelClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ApelFramePayloadHandler apelFramePayloadHandler = new ApelFramePayloadHandler(new ParticleManagerRenderer());
        ClientPlayNetworking.registerGlobalReceiver(ApelFramePayload.ID, apelFramePayloadHandler);
    }
}
