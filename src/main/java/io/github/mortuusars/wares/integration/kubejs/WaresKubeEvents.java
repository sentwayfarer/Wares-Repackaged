package io.github.mortuusars.wares.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import io.github.mortuusars.wares.integration.kubejs.event.DeliveryKubeEvent;

public final class WaresKubeEvents {
    static EventGroup GROUP = EventGroup.of("WaresEvents");

    public static EventHandler BATCH_DELIVERED = GROUP.server("batchDelivered", () -> DeliveryKubeEvent.class);
    public static EventHandler AGREEMENT_COMPLETED = GROUP.server("agreementCompleted", () -> DeliveryKubeEvent.class);
    public static EventHandler AGREEMENT_EXPIRED = GROUP.server("agreementExpired", () -> DeliveryKubeEvent.class);

    static void init() {
        // NO-OP
    }
}
