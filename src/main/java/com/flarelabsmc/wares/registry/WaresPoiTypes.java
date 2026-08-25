package com.flarelabsmc.wares.registry;

import com.flarelabsmc.wares.Wares;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WaresPoiTypes {
    private static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Wares.ID);

    public static final DeferredHolder<PoiType, PoiType> DELIVERY_TABLE_POI = POI_TYPES.register(WaresBlocks.DELIVERY_TABLE.getId().getPath(),
            () -> new PoiType(ImmutableSet.copyOf(WaresBlocks.DELIVERY_TABLE.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
    }
}
