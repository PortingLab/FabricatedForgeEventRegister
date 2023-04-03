package org.portinglab.fabricatedeventregister.event;

import net.fabricmc.api.EnvType;
import org.portinglab.fabricatedeventbus.api.EventBusImpl;
import org.portinglab.fabricatedeventregister.FabricatedEventRegisterMod;
import org.portinglab.fabricatedeventregister.fabricated.fml.FabricatedFML;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModEvent {
    /**
     * The unique mod identifier for this mod.
     * <b>Required to be lowercased in the english locale for compatibility. Will be truncated to 64 characters long.</b>
     *
     * This will be used to identify your mod for third parties (other mods), it will be used to identify your mod for registries such as block and item registries.
     * By default, you will have a resource domain that matches the modid. All these uses require that constraints are imposed on the format of the modid.
     */
    String value();

    /**
     * Annotate a class which will be subscribed to an Event Bus at mod construction time.
     * Defaults to subscribing the current modid to the {@link FabricatedEventRegisterMod#EVENT_BUS}
     * on both sides.
     *
     * @see Bus
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface EventBusSubscriber {
        /**
         * Specify targets to load this event subscriber on. Can be used to avoid loading Client specific events
         * on a dedicated server, for example.
         *
         * @return an array of Dist to load this event subscriber on
         */
        EnvType[] value() default {
            EnvType.CLIENT , EnvType.SERVER
        };

        /**
         * Optional value, only necessary if this annotation is not on the same class that has a @Mod annotation.
         * Needed to prevent early classloading of classes not owned by your mod.
         * @return a modid
         */
        String modid() default "";

        /**
         * Specify an alternative bus to listen to
         *
         * @return the bus you wish to listen to
         */
        Bus bus() default Bus.EVENT;

        enum Bus {
            /**
             * The main Event Bus.
             * @see FabricatedEventRegisterMod#EVENT_BUS
             */
            EVENT(() -> FabricatedEventRegisterMod.EVENT_BUS),
            /**
             * The mod specific Event bus.
             * @see FabricatedFML.FMLJavaModLoadingContext#getModEventBus()
             */
            MOD(() -> FabricatedFML.FMLJavaModLoadingContext.get().getModEventBus());

            private final Supplier<EventBusImpl> busSupplier;

            Bus(final Supplier<EventBusImpl> eventBusSupplier) {
                this.busSupplier = eventBusSupplier;
            }

            public Supplier<EventBusImpl> bus() {
                return busSupplier;
            }
        }
    }
}
