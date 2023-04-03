package org.portinglab.fabricated.event.lifecycle;

import net.fabricmc.loader.api.ModContainer;
import org.portinglab.fabricated.event.ModBusEventImpl;
import org.portinglab.fabricated.eventbus.bus.api.Event;
import org.portinglab.fabricated.fml.FabricatedFML;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FMLModEvent {
    public static class ModLifecycleEvent extends Event implements ModBusEventImpl {
        private final ModContainer container;

        public ModLifecycleEvent(ModContainer container) {
            this.container = container;
        }

        public final String description() {
            String cn = getClass().getName();
            return cn.substring(cn.lastIndexOf('.')+1);
        }

        ModContainer getContainer() {
            return this.container;
        }

        @Override
        public String toString() {
            return description();
        }
    }

    public static class ParallelDispatchEvent extends ModLifecycleEvent {
        private final FabricatedFML.FMLModContainer.ModLoadingStage modLoadingStage;

        public ParallelDispatchEvent(final ModContainer container, final FabricatedFML.FMLModContainer.ModLoadingStage stage) {
            super(container);
            this.modLoadingStage = stage;
        }

        private Optional<FabricatedFML.DeferredWorkQueue> getQueue() {
            return FabricatedFML.DeferredWorkQueue.lookup(Optional.of(modLoadingStage));
        }

        public CompletableFuture<Void> enqueueWork(Runnable work) {
            return getQueue().map(q->q.enqueueWork(getContainer(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
        }

        public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
            return getQueue().map(q->q.enqueueWork(getContainer(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
        }
    }

    public static class FMLConstructModEvent extends ParallelDispatchEvent {
        public FMLConstructModEvent(final ModContainer container, final FabricatedFML.FMLModContainer.ModLoadingStage stage) {
            super(container, stage);
        }
    }
}
