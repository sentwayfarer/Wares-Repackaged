package com.flarelabsmc.wares.data.agreement;

import com.flarelabsmc.wares.registry.WaresItems;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum AgreementType implements StringRepresentable {
    NONE("none"),
    SEALED("sealed"),
    REGULAR("regular"),
    COMPLETED("completed"),
    EXPIRED("expired");

    private final String name;

    AgreementType(String name) {
        this.name = name;
    }

    public static AgreementType fromItemStack(ItemStack stack) {
        if (stack.is(WaresItems.SEALED_DELIVERY_AGREEMENT.get()))
            return SEALED;
        else if (stack.is(WaresItems.DELIVERY_AGREEMENT.get()))
            return REGULAR;
        else if (stack.is(WaresItems.COMPLETED_DELIVERY_AGREEMENT.get()))
            return COMPLETED;
        else if (stack.is(WaresItems.EXPIRED_DELIVERY_AGREEMENT.get()))
            return EXPIRED;
        else
            return NONE;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
