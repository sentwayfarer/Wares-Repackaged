package com.flarelabsmc.wares.registry;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.menu.CardboardBoxMenu;
import com.flarelabsmc.wares.menu.DeliveryTableMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WaresMenuTypes {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Wares.ID);

    public static final DeferredHolder<MenuType<?>, MenuType<DeliveryTableMenu>> DELIVERY_TABLE = MENU_TYPES
            .register("delivery_table", () -> IMenuTypeExtension.create(DeliveryTableMenu::fromBuffer));

    public static final DeferredHolder<MenuType<?>, MenuType<CardboardBoxMenu>> CARDBOARD_BOX = MENU_TYPES
            .register("cardboard_box", () -> IMenuTypeExtension.create(CardboardBoxMenu::fromBuffer));
}
