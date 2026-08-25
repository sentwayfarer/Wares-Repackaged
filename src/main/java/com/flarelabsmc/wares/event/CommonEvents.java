package com.flarelabsmc.wares.event;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.block.PackageDispenseBehavior;
import com.flarelabsmc.wares.command.WaresCommand;
import com.flarelabsmc.wares.config.Config;
import com.flarelabsmc.wares.data.agreement.SealedDeliveryAgreement;
import com.flarelabsmc.wares.data.agreement.component.SteppedInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

public class CommonEvents {
    @EventBusSubscriber(modid = Wares.ID)
    public static class ModBus {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                Wares.AdvancementTriggers.register();
                Wares.Stats.register();
                DispenserBlock.registerBehavior(Wares.Items.PACKAGE.get(), new PackageDispenseBehavior());
            });
        }
    }

    @EventBusSubscriber(modid = Wares.ID)
    public static class ForgeBus {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            WaresCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void addWanderingTrades(WandererTradesEvent event) {
            if (!Config.WANDERING_TRADER_AGREEMENTS.get())
                return;

            ItemStack regularSealedAgreement = new ItemStack(Wares.Items.SEALED_DELIVERY_AGREEMENT.get());
            new SealedDeliveryAgreement.Builder()
                    .requested(Wares.resource("agreement/wandering_trader/regular_price"))
                    .payment(Wares.resource("agreement/wandering_trader/regular_ware"))
                    .ordered(new SteppedInt(16, 84, 4))
                    .experience(new SteppedInt(16, 64, 4))
                    .id("wandering_trader_agreement")
                    .build()
                    .toItemStack(regularSealedAgreement);

            event.getGenericTrades().add(new BasicItemListing(6, regularSealedAgreement, 1, 6));

            ItemStack rareSealedAgreement = new ItemStack(Wares.Items.SEALED_DELIVERY_AGREEMENT.get());
            new SealedDeliveryAgreement.Builder()
                    .requested(Wares.resource("agreement/wandering_trader/rare_price"))
                    .payment(Wares.resource("agreement/wandering_trader/rare_ware"))
                    .ordered(new SteppedInt(6, 48, 4))
                    .experience(new SteppedInt(32, 96, 4))
                    .id("wandering_trader_agreement")
                    .build()
                    .toItemStack(rareSealedAgreement);

            event.getRareTrades().add(new BasicItemListing(12, rareSealedAgreement, 1, 12));
        }
    }
}
