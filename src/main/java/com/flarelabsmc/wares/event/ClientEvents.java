package com.flarelabsmc.wares.event;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.client.gui.screen.CardboardBoxScreen;
import com.flarelabsmc.wares.client.gui.screen.DeliveryTableScreen;
import com.flarelabsmc.wares.registry.WaresItems;
import com.flarelabsmc.wares.registry.WaresMenuTypes;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = Wares.ID)
public class ClientEvents {
    @SubscribeEvent
    public static void clientSetup(final RegisterMenuScreensEvent event) {
        event.register(WaresMenuTypes.DELIVERY_TABLE.get(), DeliveryTableScreen::new);
        event.register(WaresMenuTypes.CARDBOARD_BOX.get(), CardboardBoxScreen::new);
    }

    @SubscribeEvent
    public static void onCreativeTabsBuild(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(WaresItems.SEALED_DELIVERY_AGREEMENT.get());
            event.accept(WaresItems.DELIVERY_AGREEMENT.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(WaresItems.DELIVERY_TABLE.get());
            event.accept(WaresItems.CARDBOARD_BOX.get());
            event.accept(WaresItems.PACKAGE.get());
        }
    }
}
