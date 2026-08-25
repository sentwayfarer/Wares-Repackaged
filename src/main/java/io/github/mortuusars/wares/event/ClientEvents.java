package io.github.mortuusars.wares.event;

import io.github.mortuusars.wares.Wares;
import io.github.mortuusars.wares.client.gui.screen.CardboardBoxScreen;
import io.github.mortuusars.wares.client.gui.screen.DeliveryTableScreen;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = Wares.ID)
public class ClientEvents {
    @SubscribeEvent
    public static void clientSetup(final RegisterMenuScreensEvent event) {
        event.register(Wares.MenuTypes.DELIVERY_TABLE.get(), DeliveryTableScreen::new);
        event.register(Wares.MenuTypes.CARDBOARD_BOX.get(), CardboardBoxScreen::new);
    }

    @SubscribeEvent
    public static void onCreativeTabsBuild(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(Wares.Items.SEALED_DELIVERY_AGREEMENT.get());
            event.accept(Wares.Items.DELIVERY_AGREEMENT.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(Wares.Items.DELIVERY_TABLE.get());
            event.accept(Wares.Items.CARDBOARD_BOX.get());
            event.accept(Wares.Items.PACKAGE.get());
        }
    }
}
