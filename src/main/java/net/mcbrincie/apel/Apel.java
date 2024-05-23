package net.mcbrincie.apel;

import net.fabricmc.api.ModInitializer;

import net.mcbrincie.apel.item.ModItems;
import net.mcbrincie.apel.lib.util.DelayedTask;
import net.mcbrincie.apel.lib.util.scheduler.ApelScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Apel implements ModInitializer {
	public static final String mod_id = "apel";
    public static final Logger LOGGER = LoggerFactory.getLogger(mod_id);
	public static ApelScheduler apelScheduler = new ApelScheduler();
	public static List<DelayedTask<Runnable>> delayedTasks = new ArrayList<>();

	@Override
	public void onInitialize() {
		LOGGER.info("Library Mod Initializing");
		ModItems.initItems();
	}
}