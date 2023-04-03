package org.portinglab.fabricated.eventregister;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.portinglab.fabricated.eventbus.bus.api.BusBuilder;
import org.portinglab.fabricated.eventbus.bus.api.EventBusImpl;

public class FabricatedEventRegisterMod implements ModInitializer {
	public static final String MODID = "fabricated-eventregister";
	public static final String MODNAME = "FabricatedForgeEventRegister";
	public static final Logger LOGGER = LogManager.getLogger(MODNAME);
	public static final EventBusImpl EVENT_BUS = BusBuilder.builder().startShutdown().build();

	@Override
	public void onInitialize() {

	}
}
