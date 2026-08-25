package com.flarelabsmc.wares.registry;

import com.flarelabsmc.wares.Wares;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;

import java.util.HashMap;
import java.util.Map;

public class WaresStats {
    private static final Map<ResourceLocation, StatFormatter> STATS = new HashMap<>();

    public static final ResourceLocation SEALED_LETTERS_OPENED =
            register(Wares.resource("sealed_letters_opened"), StatFormatter.DEFAULT);
    public static final ResourceLocation PACKAGES_OPENED =
            register(Wares.resource("packages_opened"), StatFormatter.DEFAULT);
    public static final ResourceLocation INTERACT_WITH_DELIVERY_TABLE =
            register(Wares.resource("interact_with_delivery_table"), StatFormatter.DEFAULT);

    @SuppressWarnings("SameParameterValue")
    private static ResourceLocation register(ResourceLocation location, StatFormatter formatter) {
        STATS.put(location, formatter);
        return location;
    }

    public static void register() {
        STATS.forEach((location, formatter) -> {
            Registry.register(BuiltInRegistries.CUSTOM_STAT, location, location);
            net.minecraft.stats.Stats.CUSTOM.get(location, formatter);
        });
    }
}
