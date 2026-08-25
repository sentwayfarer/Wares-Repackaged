package com.flarelabsmc.wares.registry;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.content.block.entity.DeliveryTableBlockEntity;
import com.flarelabsmc.wares.content.block.entity.PackageBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("DataFlowIssue")
public class WaresBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Wares.ID);

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DeliveryTableBlockEntity>> DELIVERY_TABLE =
            BLOCK_ENTITIES.register("delivery_table",
                    () -> BlockEntityType.Builder.of(DeliveryTableBlockEntity::new, WaresBlocks.DELIVERY_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PackageBlockEntity>> PACKAGE =
            BLOCK_ENTITIES.register("package",
                    () -> BlockEntityType.Builder.of(PackageBlockEntity::new, WaresBlocks.PACKAGE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
