package org.portinglab.fabricatedeventregister.fabricated.fml;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.discovery.ModCandidate;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.objectweb.asm.Type;
import org.portinglab.fabricatedeventbus.EventBusErrorMessage;
import org.portinglab.fabricatedeventbus.api.BusBuilder;
import org.portinglab.fabricatedeventbus.api.Event;
import org.portinglab.fabricatedeventbus.api.EventBusImpl;
import org.portinglab.fabricatedeventbus.api.EventListenerImpl;
import org.portinglab.fabricatedeventregister.FabricatedEventRegisterMod;
import org.portinglab.fabricatedeventregister.event.register.EventAutoRegister;
import org.portinglab.fabricatedeventregister.fabricated.event.ModBusEventImpl;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FabricatedFML {
    public static final Marker LOADING = MarkerManager.getMarker("LOADING");

    public static class ModLoadingContext {
        private static final ThreadLocal<ModLoadingContext> context = ThreadLocal.withInitial(ModLoadingContext::new);
        private Object languageExtension;

        public static ModLoadingContext get() {
            return context.get();
        }

        @SuppressWarnings("unchecked")
        public <T> T extension() {
            return (T) languageExtension;
        }
    }

    public static class FMLJavaModLoadingContext {
        private final FMLModContainer container;

        FMLJavaModLoadingContext(FMLModContainer container) {
            this.container = container;
        }

        /**
         * @return The mod's event bus, to allow subscription to Mod specific events
         */
        public EventBusImpl getModEventBus()
        {
            return container.getEventBus();
        }


        /**
         * Helper to get the right instance from the {@link ModLoadingContext} correctly.
         * @return The FMLJavaMod language specific extension from the ModLoadingContext
         */
        public static FMLJavaModLoadingContext get() {
            return ModLoadingContext.get().extension();
        }
    }

    public static class ModAnnotation {
        public static FabricatedForgeSPI.ModFileScanData.AnnotationData fromModAnnotation(final Type clazz, final ModAnnotation annotation) {
            return new FabricatedForgeSPI.ModFileScanData.AnnotationData(annotation.asmType, annotation.type, clazz, annotation.member, annotation.values);
        }

        public static class EnumHolder {
            private final String desc;
            private final String value;

            public EnumHolder(String desc, String value) {
                this.desc = desc;
                this.value = value;
            }

            public String getDesc() {
                return desc;
            }

            public String getValue() {
                return value;
            }
        }
        private final ElementType type;
        private final Type asmType;
        private final String member;
        private final Map<String,Object> values = Maps.newHashMap();

        private ArrayList<Object> arrayList;
        private String arrayName;
        public ModAnnotation(ElementType type, Type asmType, String member) {
            this.type = type;
            this.asmType = asmType;
            this.member = member;
        }

        public ModAnnotation(Type asmType, ModAnnotation parent) {
            this.type = parent.type;
            this.asmType = asmType;
            this.member = parent.member;
        }
        @Override
        public String toString() {
            return MoreObjects.toStringHelper("Annotation")
                    .add("type",type)
                    .add("name",asmType.getClassName())
                    .add("member",member)
                    .add("values", values)
                    .toString();
        }

        public ElementType getType() {
            return type;
        }
        public Type getASMType() {
            return asmType;
        }
        public String getMember() {
            return member;
        }
        public Map<String, Object> getValues() {
            return values;
        }
        public void addArray(String name) {
            this.arrayList = Lists.newArrayList();
            this.arrayName = name;
        }
        public void addProperty(String key, Object value) {
            if (this.arrayList != null) {
                arrayList.add(value);
            } else {
                values.put(key, value);
            }
        }

        public void addEnumProperty(String key, String enumName, String value) {
            addProperty(key, new EnumHolder(enumName, value));
        }

        public void endArray() {
            values.put(arrayName, arrayList);
            arrayList = null;
        }
        public ModAnnotation addChildAnnotation(String name, String desc) {
            ModAnnotation child = new ModAnnotation(Type.getType(desc), this);
            addProperty(name, child.getValues());
            return child;
        }
    }

    public static class FMLEnvironment {
        public static final EnvType dist = FabricLoader.getInstance().getEnvironmentType();
    }

    public static class FMLModContainer extends ModContainerImpl {
        private final FabricatedForgeSPI.ModFileScanData scanResults;
        public final EventBusImpl eventBus;
        private Object modInstance;
        private final Class<?> modClass;
        public FMLModContainer(ModCandidate candidate, FabricatedForgeSPI.ModFileScanData scanResults, Object modInstance, Class<?> modClass) {
            super(candidate);
            Map<ModLoadingStage, Runnable> activityMap = new HashMap<>();
            this.scanResults = scanResults;
            activityMap.put(ModLoadingStage.CONSTRUCT, this::constructMod);
            this.eventBus = BusBuilder.builder().setExceptionHandler(this::onEventFailed).setTrackPhases(false).markerType(ModBusEventImpl.class).useModLauncher().build();
            this.modInstance = modInstance;
            this.modClass = modClass;
        }

        private void onEventFailed(EventBusImpl iEventBus, Event event, EventListenerImpl[] iEventListeners, int i, Throwable throwable)
        {
            FabricatedEventRegisterMod.LOGGER.error(new EventBusErrorMessage(event, i, iEventListeners, throwable));
        }

        private void constructMod() {
            try {
                FabricatedEventRegisterMod.LOGGER.trace(LOADING, "Injecting Automatic event subscribers for {}", getMetadata().getId());
                EventAutoRegister.inject(this, this.scanResults, this.modClass.getClassLoader());
                FabricatedEventRegisterMod.LOGGER.trace(LOADING, "Completed Automatic event subscribers for {}", getMetadata().getId());
            } catch (Throwable e) {
                FabricatedEventRegisterMod.LOGGER.error(LOADING,"Failed to register automatic subscribers. ModID: {}, class {}", getMetadata().getId(), modClass.getName(), e);
            }
        }

        public EventBusImpl getEventBus()
        {
            return this.eventBus;
        }

        public enum ModLoadingStage {
            CONSTRUCT
        }
    }
}
