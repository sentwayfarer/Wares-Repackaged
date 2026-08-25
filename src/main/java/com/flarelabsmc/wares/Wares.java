package com.flarelabsmc.wares;

import com.flarelabsmc.wares.registry.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import com.flarelabsmc.wares.content.advancement.DeliveryTableTrigger;
import com.flarelabsmc.wares.config.Config;
import com.flarelabsmc.wares.world.VillageStructures;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        SoundEvents.SOUNDS.register(bus);

        NeoForge.EVENT_BUS.addListener(VillageStructures::addVillageStructures);
    }

    /**
     * Creates resource location in the mod namespace with the given path.
     */
    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static class SoundEvents {
        private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Wares.ID);

        public static final DeferredHolder<SoundEvent, SoundEvent> PAPER_TEAR = registerSound("item", "paper.tear");
        public static final DeferredHolder<SoundEvent, SoundEvent> PAPER_CRACKLE = registerSound("item", "paper.crackle");

        public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_PLACE = registerSound("block", "cardboard.place");
        public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_BREAK = registerSound("block", "cardboard.break");
        public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_HIT = registerSound("block", "cardboard.hit");
        public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_FALL = registerSound("block", "cardboard.fall");
        public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_STEP = registerSound("block", "cardboard.step");

        public static final DeferredHolder<SoundEvent, SoundEvent> WRITING = registerSound("block", "delivery_table.writing");
        public static final DeferredHolder<SoundEvent, SoundEvent> DELIVERY_TABLE_OPEN = registerSound("block", "delivery_table.open");
        public static final DeferredHolder<SoundEvent, SoundEvent> DELIVERY_TABLE_CLOSE = registerSound("block", "delivery_table.close");
        public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_BOX_USE = registerSound("block", "cardboard_box.use");

        public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_WORK_PACKAGER = registerSound("entity", "villager.work_packager");

        private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String category, String key) {
            Preconditions.checkState(category != null && category.length() > 0, "'category' should not be empty.");
            Preconditions.checkState(key != null && key.length() > 0, "'key' should not be empty.");
            String path = category + "." + key;
            return SOUNDS.register(path, () -> SoundEvent.createVariableRangeEvent(Wares.resource(path)));
        }
    }

    public static class SoundTypes {
        public static final SoundType CARDBOARD = new DeferredSoundType(1f, 1f, SoundEvents.CARDBOARD_BREAK, SoundEvents.CARDBOARD_STEP, SoundEvents.CARDBOARD_PLACE, SoundEvents.CARDBOARD_HIT, SoundEvents.CARDBOARD_FALL);
    }

    public static class Stats {
        private static final Map<ResourceLocation, StatFormatter> STATS = new HashMap<>();

        public static final ResourceLocation SEALED_LETTERS_OPENED =
                register(Wares.resource("sealed_letters_opened"), StatFormatter.DEFAULT);
        public static final ResourceLocation PACKAGES_OPENED =
                register(Wares.resource("packages_opened"), StatFormatter.DEFAULT);
        public static final ResourceLocation INTERACT_WITH_DELIVERY_TABLE =
                register(Wares.resource("interact_with_delivery_table"), StatFormatter.DEFAULT);

        @SuppressWarnings("SameParameterValue")
        private static ResourceLocation register(ResourceLocation location, StatFormatter formatter) {
            STATS.put(location, formatter);
            return location;
        }

        public static void register() {
            STATS.forEach((location, formatter) -> {
                Registry.register(BuiltInRegistries.CUSTOM_STAT, location, location);
                net.minecraft.stats.Stats.CUSTOM.get(location, formatter);
            });
        }
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
