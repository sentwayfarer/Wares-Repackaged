package io.github.mortuusars.wares.data.generation.provider;

import io.github.mortuusars.wares.Wares;
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
        blockItem(Wares.Items.DELIVERY_TABLE.get());
        singleTextureItem(Wares.Items.CARDBOARD_BOX.get());
        singleTextureItem(Wares.Items.PACKAGE.get());
        singleTextureItem(Wares.Items.SEALED_DELIVERY_AGREEMENT.get());
        singleTextureItem(Wares.Items.DELIVERY_AGREEMENT.get());
        singleTextureItem(Wares.Items.COMPLETED_DELIVERY_AGREEMENT.get());
        singleTextureItem(Wares.Items.EXPIRED_DELIVERY_AGREEMENT.get());
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
