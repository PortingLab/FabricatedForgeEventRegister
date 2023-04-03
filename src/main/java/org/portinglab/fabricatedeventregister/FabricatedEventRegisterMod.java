package org.portinglab.fabricatedeventregister;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.portinglab.fabricatedeventbus.api.BusBuilder;
import org.portinglab.fabricatedeventbus.api.EventBusImpl;

public class FabricatedEventRegisterMod implements ModInitializer {
	public static final String MODID = "fabricated-eventsubscriber";
	public static final String MODNAME = "FabricatedForgeEventSubscriber";
	public static final Logger LOGGER = LogManager.getLogger(MODNAME);
	public static final EventBusImpl EVENT_BUS = BusBuilder.builder().startShutdown().build();

	@Override
	public void onInitialize() {

	}
}
