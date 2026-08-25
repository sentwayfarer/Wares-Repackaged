package com.flarelabsmc.wares;

import com.flarelabsmc.wares.registry.*;
import com.mojang.logging.LogUtils;
import com.flarelabsmc.wares.content.advancement.DeliveryTableTrigger;
import com.flarelabsmc.wares.config.Config;
import com.flarelabsmc.wares.world.VillageStructures;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import org.slf4j.Logger;

@Mod(Wares.ID)
public class Wares
{
    public static final String ID = "wares";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Wares(ModContainer mod, IEventBus bus)
    {
        mod.registerConfig(ModConfig.Type.COMMON, Config.COMMON);
        mod.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);
        bus.addListener(Config::onConfigLoad);
        bus.addListener(Config::onConfigReload);

        WaresBlocks.register(bus);
        WaresBlockEntities.register(bus);
        WaresMenuTypes.register(bus);
        WaresItems.register(bus);
        WaresPoiTypes.register(bus);
        WaresProfessions.register(bus);
        WaresSoundEvents.register(bus);

        NeoForge.EVENT_BUS.addListener(VillageStructures::addVillageStructures);
    }

    /**
     * Creates resource location in the mod namespace with the given path.
     */
    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static class SoundTypes {
        public static final SoundType CARDBOARD = new DeferredSoundType(1f, 1f, WaresSoundEvents.CARDBOARD_BREAK, WaresSoundEvents.CARDBOARD_STEP, WaresSoundEvents.CARDBOARD_PLACE, WaresSoundEvents.CARDBOARD_HIT, WaresSoundEvents.CARDBOARD_FALL);
    }

    public static class AdvancementTriggers {
        public static DeliveryTableTrigger BATCH_DELIVERED = new DeliveryTableTrigger();
        public static DeliveryTableTrigger AGREEMENT_COMPLETED = new DeliveryTableTrigger();
        public static DeliveryTableTrigger AGREEMENT_EXPIRED = new DeliveryTableTrigger();

        public static void register() {
            CriteriaTriggers.register("batch_delivered", BATCH_DELIVERED);
            CriteriaTriggers.register("agreement_completed", AGREEMENT_COMPLETED);
            CriteriaTriggers.register("agreement_expired", AGREEMENT_EXPIRED);
        }
    }

    public static class Tags {
        public static class Items {
            public static final TagKey<Item> AGREEMENTS = ItemTags.create(Wares.resource("agreements"));
            public static final TagKey<Item> DELIVERY_BOXES = ItemTags.create(Wares.resource("delivery_boxes"));
            public static final TagKey<Item> CARDBOARD_BOX_BLACKLISTED = ItemTags.create(Wares.resource("cardboard_box_blacklisted"));
        }
    }
}
