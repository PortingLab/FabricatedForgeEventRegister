package org.portinglab.fabricated.eventregister.event.register;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.ModContainer;
import org.objectweb.asm.Type;
import org.portinglab.fabricated.eventregister.FabricatedEventRegisterMod;
import org.portinglab.fabricated.eventregister.event.ModEvent;
import org.portinglab.fabricated.fml.FabricatedFML;
import org.portinglab.fabricated.fml.FabricatedSPI;

import java.util.*;
import java.util.stream.Collectors;

public class EventAutoRegister {
    private static final Type AUTO_SUBSCRIBER = Type.getType(ModEvent.EventBusRegister.class);
    private static final Type MOD_TYPE = Type.getType(ModEvent.class);
    public static void inject(final ModContainer mod, final FabricatedSPI.ModFileScanData scanData, final ClassLoader loader) {
        if (scanData == null) return;
        FabricatedEventRegisterMod.LOGGER.debug(FabricatedFML.LOADING,"Attempting to inject @EventBusSubscriber classes into the eventbus for {}", mod.getMetadata().getId());
        List<FabricatedSPI.ModFileScanData.AnnotationData> ebsTargets = scanData.getAnnotations().stream().
                filter(annotationData -> AUTO_SUBSCRIBER.equals(annotationData.annotationType())).
                collect(Collectors.toList());
        Map<String, String> modids = scanData.getAnnotations().stream().
                filter(annotationData -> MOD_TYPE.equals(annotationData.annotationType())).
                collect(Collectors.toMap(a -> a.clazz().getClassName(), a -> (String)a.annotationData().get("value")));

        ebsTargets.forEach(ad -> {
            @SuppressWarnings("unchecked")
            final List<FabricatedFML.ModAnnotation.EnumHolder> sidesValue = (List<FabricatedFML.ModAnnotation.EnumHolder>)ad.annotationData().
                    getOrDefault("value", Arrays.asList(new FabricatedFML.ModAnnotation.EnumHolder(null, "CLIENT"), new FabricatedFML.ModAnnotation.EnumHolder(null, "DEDICATED_SERVER")));
            final EnumSet<EnvType> sides = sidesValue.stream().map(eh -> EnvType.valueOf(eh.getValue())).
                    collect(Collectors.toCollection(() -> EnumSet.noneOf(EnvType.class)));
            final String modId = (String)ad.annotationData().getOrDefault("modid", modids.getOrDefault(ad.clazz().getClassName(), mod.getMetadata().getId()));
            final FabricatedFML.ModAnnotation.EnumHolder busTargetHolder = (FabricatedFML.ModAnnotation.EnumHolder)ad.annotationData().getOrDefault("bus", new FabricatedFML.ModAnnotation.EnumHolder(null, "FORGE"));
            final ModEvent.EventBusRegister.Bus busTarget = ModEvent.EventBusRegister.Bus.valueOf(busTargetHolder.getValue());
            if (Objects.equals(mod.getMetadata().getId(), modId) && sides.contains(FabricatedFML.FMLEnvironment.dist)) {
                try {
                    FabricatedEventRegisterMod.LOGGER.debug(FabricatedFML.LOADING, "Auto-subscribing {} to {}", ad.clazz().getClassName(), busTarget);
                    busTarget.bus().get().register(Class.forName(ad.clazz().getClassName(), true, loader));
                }
                catch (ClassNotFoundException e) {
                    FabricatedEventRegisterMod.LOGGER.fatal(FabricatedFML.LOADING, "Failed to load mod class {} for @EventBusSubscriber annotation", ad.clazz(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
