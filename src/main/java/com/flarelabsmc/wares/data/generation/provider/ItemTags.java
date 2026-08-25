package com.flarelabsmc.wares.data.generation.provider;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.registry.WaresItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class ItemTags extends ItemTagsProvider {
    public ItemTags(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pLookupProvider, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), pLookupProvider, blockTagsProvider.contentsGetter(), Wares.ID, existingFileHelper);
    }


    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(Wares.Tags.Items.AGREEMENTS)
                .add(WaresItems.SEALED_DELIVERY_AGREEMENT.get(),
                     WaresItems.DELIVERY_AGREEMENT.get(),
                     WaresItems.COMPLETED_DELIVERY_AGREEMENT.get(),
                     WaresItems.EXPIRED_DELIVERY_AGREEMENT.get());

        tag(Wares.Tags.Items.DELIVERY_BOXES)
                .add(WaresItems.CARDBOARD_BOX.get());

        tag(Wares.Tags.Items.CARDBOARD_BOX_BLACKLISTED)
                .add(WaresItems.PACKAGE.get(),
                     WaresItems.CARDBOARD_BOX.get());
    }

    private void optionalTags(TagAppender<Item> tag, String namespace, String... items) {
        for (String item : items) {
            tag.addOptionalTag(ResourceLocation.fromNamespaceAndPath(namespace, item));
        }
    }

    private void optional(TagAppender<Item> tag, String namespace, String... items) {
        for (String item : items) {
            tag.addOptional(ResourceLocation.fromNamespaceAndPath(namespace, item));
        }
    }
}