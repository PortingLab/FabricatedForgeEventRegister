package org.portinglab.fabricated.eventbus.bus.api;

public interface EventBusInvokeDispatcherImpl {
    void invoke(EventListenerImpl listener, Event event);
}
