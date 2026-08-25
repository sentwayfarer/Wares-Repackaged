package com.flarelabsmc.wares.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;

public class WaresKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerEvents(EventGroupRegistry registry) {
        WaresKubeEvents.init();
        registry.register(WaresKubeEvents.GROUP);
    }
}
