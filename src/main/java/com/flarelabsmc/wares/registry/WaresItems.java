package com.flarelabsmc.wares.registry;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.content.item.CardboardBoxItem;
import com.flarelabsmc.wares.content.item.DeliveryAgreementItem;
import com.flarelabsmc.wares.content.item.PackageItem;
import com.flarelabsmc.wares.content.item.SealedDeliveryAgreementItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WaresItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Wares.ID);

    public static final DeferredHolder<Item, SealedDeliveryAgreementItem> SEALED_DELIVERY_AGREEMENT = ITEMS.register("sealed_delivery_agreement", () ->
            new SealedDeliveryAgreementItem(new Item.Properties()
                    .stacksTo(1)));
    public static final DeferredHolder<Item, DeliveryAgreementItem> DELIVERY_AGREEMENT = ITEMS.register("delivery_agreement", () ->
            new DeliveryAgreementItem(new Item.Properties()
                    .stacksTo(1)));
    public static final DeferredHolder<Item, DeliveryAgreementItem> COMPLETED_DELIVERY_AGREEMENT = ITEMS.register("completed_delivery_agreement", () ->
            new DeliveryAgreementItem(new Item.Properties()
                    .stacksTo(1)));
    public static final DeferredHolder<Item, DeliveryAgreementItem> EXPIRED_DELIVERY_AGREEMENT = ITEMS.register("expired_delivery_agreement", () ->
            new DeliveryAgreementItem(new Item.Properties()
                    .stacksTo(1)));

    public static final DeferredHolder<Item, BlockItem> DELIVERY_TABLE = ITEMS.register("delivery_table", () ->
            new BlockItem(WaresBlocks.DELIVERY_TABLE.get(), new Item.Properties()));

    public static final DeferredHolder<Item, CardboardBoxItem> CARDBOARD_BOX = ITEMS.register("cardboard_box", () ->
            new CardboardBoxItem(WaresBlocks.CARDBOARD_BOX.get(), new Item.Properties()));
    public static final DeferredHolder<Item, PackageItem> PACKAGE = ITEMS.register("package", () ->
            new PackageItem(WaresBlocks.PACKAGE.get(), new Item.Properties()
                    .stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
