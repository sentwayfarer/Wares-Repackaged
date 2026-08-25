package com.flarelabsmc.wares.registry;

import com.flarelabsmc.wares.Wares;
import com.google.common.base.Preconditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WaresSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Wares.ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PAPER_TEAR =
            registerSound("item", "paper.tear");
    public static final DeferredHolder<SoundEvent, SoundEvent> PAPER_CRACKLE =
            registerSound("item", "paper.crackle");

    public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_PLACE =
            registerSound("block", "cardboard.place");
    public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_BREAK =
            registerSound("block", "cardboard.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_HIT =
            registerSound("block", "cardboard.hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_FALL =
            registerSound("block", "cardboard.fall");
    public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_STEP =
            registerSound("block", "cardboard.step");

    public static final DeferredHolder<SoundEvent, SoundEvent> WRITING =
            registerSound("block", "delivery_table.writing");
    public static final DeferredHolder<SoundEvent, SoundEvent> DELIVERY_TABLE_OPEN =
            registerSound("block", "delivery_table.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> DELIVERY_TABLE_CLOSE =
            registerSound("block", "delivery_table.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> CARDBOARD_BOX_USE =
            registerSound("block", "cardboard_box.use");

    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_WORK_PACKAGER =
            registerSound("entity", "villager.work_packager");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String category, String key) {
        String path = category + "." + key;
        return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(Wares.resource(path)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
