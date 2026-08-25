package com.flarelabsmc.wares.registry;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.content.block.CardboardBoxBlock;
import com.flarelabsmc.wares.content.block.DeliveryTableBlock;
import com.flarelabsmc.wares.content.block.PackageBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WaresBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Wares.ID);

    public static final DeferredHolder<Block, DeliveryTableBlock> DELIVERY_TABLE = BLOCKS.register("delivery_table",
            () -> new DeliveryTableBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(2f)));

    public static final DeferredHolder<Block, CardboardBoxBlock> CARDBOARD_BOX = BLOCKS.register("cardboard_box",
            () -> new CardboardBoxBlock(BlockBehaviour.Properties.of()
                    .sound(Wares.SoundTypes.CARDBOARD)
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.4f)));

    public static final DeferredHolder<Block, PackageBlock> PACKAGE = BLOCKS.register("package",
            () -> new PackageBlock(BlockBehaviour.Properties.of()
                    .sound(Wares.SoundTypes.CARDBOARD)
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.6f)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
