package com.flarelabsmc.wares.data.generation.provider;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.registry.WaresItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

@SuppressWarnings({"DataFlowIssue", "UnusedReturnValue"})
public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), Wares.ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        blockItem(WaresItems.DELIVERY_TABLE.get());
        singleTextureItem(WaresItems.CARDBOARD_BOX.get());
        singleTextureItem(WaresItems.PACKAGE.get());
        singleTextureItem(WaresItems.SEALED_DELIVERY_AGREEMENT.get());
        singleTextureItem(WaresItems.DELIVERY_AGREEMENT.get());
        singleTextureItem(WaresItems.COMPLETED_DELIVERY_AGREEMENT.get());
        singleTextureItem(WaresItems.EXPIRED_DELIVERY_AGREEMENT.get());
    }

    private ItemModelBuilder blockItem(BlockItem item) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item).getPath(), modLoc("block/" + BuiltInRegistries.ITEM.getKey(item).getPath()));
    }

    private ItemModelBuilder singleTextureItem(Item item) {
        return singleTexture(BuiltInRegistries.ITEM.getKey(item).getPath(),
                mcLoc("item/generated"), "layer0",
                modLoc("item/" + BuiltInRegistries.ITEM.getKey(item).getPath()));
    }
}
