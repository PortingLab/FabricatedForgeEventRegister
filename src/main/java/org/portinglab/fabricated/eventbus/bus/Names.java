package org.portinglab.fabricated.eventbus.bus;

import static org.objectweb.asm.Type.*;

import org.objectweb.asm.tree.MethodNode;

class Names {
    static final String SUBSCRIBE_EVENT = "Lorg/portinglab/fabricated/eventbus/bus/api/SubscribeEvent;";
    static final String HAS_RESULT = "Lorg/portinglab/fabricated/eventbus/bus/api/Event$HasResult;";
    static final Method HAS_RESULT_M = new Method("hasResult", getMethodDescriptor(BOOLEAN_TYPE));

    static final String CANCELLABLE = "Lorg/portinglab/fabricated/eventbus/bus/api/Cancelable;";
    static final Method CANCELABLE_M = new Method("isCancelable", getMethodDescriptor(BOOLEAN_TYPE));

    static final String LISTENER_LIST = "Lorg/portinglab/fabricated/eventbus/bus/ListenerList;";
    static final Method LISTENER_LIST_INIT = new Method("<init>", getMethodDescriptor(VOID_TYPE, getType(LISTENER_LIST)));
    static final Method LISTENER_LIST_GET  = new Method("getListenerList", getMethodDescriptor(getType(LISTENER_LIST)));
    static final Method LISTENER_LIST_F = new Method("LISTENER_LIST", LISTENER_LIST);
    static final String LISTENER_LIST_HELPER = "Lorg/portinglab/fabricated/eventbus/bus/api/EventListenerHelper;";

    static final String EVENT = "Lorg/portinglab/fabricated/eventbus/bus/api/Event;";
    static final Method INIT_M = new Method("<init>", getMethodDescriptor(VOID_TYPE));
    static final Method STATIC_INIT_M = new Method("<clinit>", getMethodDescriptor(VOID_TYPE));

    static record Method(String name, String desc) {
        boolean equals(MethodNode node) {
            return this.name.equals(node.name) && this.desc.equals(node.desc);
        }
    }
}
