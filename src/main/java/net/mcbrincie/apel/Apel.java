package net.mcbrincie.apel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.mcbrincie.apel.item.ModItems;
import net.mcbrincie.apel.lib.renderers.ApelFramePayload;
import net.mcbrincie.apel.lib.util.math.TrigTable;
import net.mcbrincie.apel.lib.util.scheduler.ApelScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Apel implements ModInitializer {
    public static final String MOD_ID = "apel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ApelScheduler SCHEDULER = new ApelScheduler();
    public static final ExecutorService DRAW_EXECUTOR = Executors.newSingleThreadExecutor();
    public static final TrigTable TRIG_TABLE = new TrigTable(700);

    @Override
    public void onInitialize() {
        LOGGER.info("Library Mod Initializing");
        ModItems.initItems();
        // TODO: This uses Fabric's networking wrappers, which I'm not convinced are necessary.
        PayloadTypeRegistry.playS2C().register(ApelFramePayload.ID, ApelFramePayload.PACKET_CODEC);
    }
}