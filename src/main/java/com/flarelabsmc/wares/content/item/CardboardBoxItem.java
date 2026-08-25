package com.flarelabsmc.wares.content.item;

import com.flarelabsmc.wares.menu.CardboardBoxMenu;
import com.flarelabsmc.wares.registry.WaresBlocks;
import com.flarelabsmc.wares.registry.WaresItems;
import com.flarelabsmc.wares.registry.WaresSoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class CardboardBoxItem extends BlockItem {
    public CardboardBoxItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack usedStack = context.getItemInHand();

        Level level = context.getLevel();

        if (!context.isSecondaryUseActive()
                && !level.getBlockState(context.getClickedPos()).is(WaresBlocks.CARDBOARD_BOX.get())
                && usedStack.is(WaresItems.CARDBOARD_BOX.get())) {
            if (context.getPlayer() instanceof ServerPlayer serverPlayer)
                openCardboardBoxGui(serverPlayer, usedStack);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useOn(context);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (stack.is(WaresItems.CARDBOARD_BOX.get()) && player instanceof ServerPlayer serverPlayer)
            openCardboardBoxGui(serverPlayer, stack);

        return InteractionResultHolder.success(stack);
    }

    private void openCardboardBoxGui(ServerPlayer serverPlayer, ItemStack cardboardBoxStack) {
        NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, player) ->
                        new CardboardBoxMenu(containerId, playerInventory), cardboardBoxStack.getHoverName()),
                buffer -> buffer.writeItemStack(cardboardBoxStack, false));

        serverPlayer.level().playSound(null, serverPlayer, WaresSoundEvents.CARDBOARD_BOX_USE.get(), SoundSource.PLAYERS,
                1f, serverPlayer.level().getRandom().nextFloat() * 0.3f + 0.85f);
    }
}
