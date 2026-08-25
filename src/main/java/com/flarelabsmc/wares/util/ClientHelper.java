package com.flarelabsmc.wares.util;

import com.flarelabsmc.wares.content.block.entity.DeliveryTableBlockEntity;
import com.flarelabsmc.wares.client.gui.screen.DeliveryTableScreen;
import com.flarelabsmc.wares.data.agreement.DeliveryAgreement;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ClientHelper {
    public static void releaseUseKey() {
        Minecraft.getInstance().options.keyUse.setDown(false);
    }

    public static boolean isViewingInDeliveryTableScreen(ItemStack agreementStack) {
        return Minecraft.getInstance().screen instanceof DeliveryTableScreen deliveryTableScreen && ItemStack.isSameItemSameComponents(deliveryTableScreen.getMenu().blockEntity.getAgreementItem(), agreementStack);
    }

    public static Supplier<DeliveryAgreement> getDeliveryTableAgreementSupplier(DeliveryAgreement fallbackAgreement) {
        if (Minecraft.getInstance().screen instanceof DeliveryTableScreen deliveryTableScreen) {
            DeliveryTableBlockEntity blockEntity = deliveryTableScreen.getMenu().blockEntity;
            return blockEntity::getAgreement;
        }

        return () -> fallbackAgreement;
    }
}
