package com.flarelabsmc.wares.registry;

import com.flarelabsmc.wares.Wares;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

import static com.flarelabsmc.wares.registry.WaresPoiTypes.DELIVERY_TABLE_POI;

public class WaresProfessions {
    private static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, Wares.ID);

    public static final DeferredHolder<VillagerProfession, VillagerProfession> PACKAGER =
            PROFESSIONS.register(
                    "packager",
                    () -> new VillagerProfession(
                            "packager",
                            poi -> poi.is(Objects.requireNonNull(DELIVERY_TABLE_POI.getKey())),
                            poi -> poi.is(Objects.requireNonNull(DELIVERY_TABLE_POI.getKey())),
                            ImmutableSet.of(),
                            ImmutableSet.of(),
                            null
                    )
            );

    public static void register(IEventBus eventBus) {
        PROFESSIONS.register(eventBus);
    }
}
