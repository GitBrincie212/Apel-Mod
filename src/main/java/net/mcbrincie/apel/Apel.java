package net.mcbrincie.apel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.mcbrincie.apel.item.ModItems;
import net.mcbrincie.apel.lib.renderers.ApelFramePayload;
import net.mcbrincie.apel.lib.util.TrigTable;
import net.mcbrincie.apel.lib.util.scheduler.ApelScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Apel implements ModInitializer {
	public static final String mod_id = "apel";
    public static final Logger LOGGER = LoggerFactory.getLogger(mod_id);
	public static ApelScheduler apelScheduler = new ApelScheduler();
	public static ExecutorService drawThread = Executors.newSingleThreadExecutor();
	public static TrigTable trigonometryTable = new TrigTable(700);

	@Override
	public void onInitialize() {
		LOGGER.info("Library Mod Initializing");
		ModItems.initItems();
		// TODO: This uses Fabric's networking wrappers, which I'm not convinced are necessary.
		PayloadTypeRegistry.playS2C().register(ApelFramePayload.ID, ApelFramePayload.PACKET_CODEC);
	}
}