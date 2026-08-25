package com.flarelabsmc.wares.data.generation;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.data.generation.provider.*;
import io.github.mortuusars.wares.data.generation.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Wares.ID)
public class DataGeneration
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new Advancements(generator, lookupProvider, helper));
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(LootTables.BlockLoot::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(LootTables.ChestLoot::new, LootContextParamSets.CHEST))));
        generator.addProvider(event.includeServer(), new Recipes(generator));
        BlockTags blockTags = new BlockTags(generator, lookupProvider, helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ItemTags(generator, lookupProvider, blockTags, helper));

        BlockStatesAndModels blockStates = new BlockStatesAndModels(generator, helper);
        generator.addProvider(event.includeClient(), blockStates);
        generator.addProvider(event.includeClient(), new ItemModels(generator, blockStates.models().existingFileHelper));
        generator.addProvider(event.includeClient(), new Sounds(generator, helper));
    }
}