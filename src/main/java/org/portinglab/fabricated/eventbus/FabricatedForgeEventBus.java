package org.portinglab.fabricated.eventbus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class FabricatedForgeEventBus {
    public static final String MODID = "fabricated-eventbus";
    public static final String MODNAME = "FabricatedForgeEventBus";
    public static final String MARKER = "EVENTBUS";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public static final Marker EVENTBUS = MarkerManager.getMarker(MARKER);
}
