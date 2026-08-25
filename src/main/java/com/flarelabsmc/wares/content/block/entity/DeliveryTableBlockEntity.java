package com.flarelabsmc.wares.content.block.entity;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.content.advancement.DeliveryTableTrigger;
import com.flarelabsmc.wares.content.block.DeliveryTableBlock;
import com.flarelabsmc.wares.config.Config;
import com.flarelabsmc.wares.data.agreement.DeliveryAgreement;
import com.flarelabsmc.wares.data.agreement.AgreementType;
import com.flarelabsmc.wares.data.agreement.component.RequestedItem;
import com.flarelabsmc.wares.integration.kubejs.KubeJSIntegration;
import com.flarelabsmc.wares.content.item.DeliveryAgreementItem;
import com.flarelabsmc.wares.menu.DeliveryTableMenu;
import com.flarelabsmc.wares.registry.WaresBlockEntities;
import com.flarelabsmc.wares.registry.WaresItems;
import com.flarelabsmc.wares.registry.WaresProfessions;
import com.flarelabsmc.wares.registry.WaresSoundEvents;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeliveryTableBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int SLOTS = 14;
    public static final int AGREEMENT_SLOT = 0;
    public static final int PACKAGES_SLOT = 1;
    public static final int[] AGREEMENT_SLOTS = new int[]{AGREEMENT_SLOT};
    public static final int[] PACKAGES_SLOTS = new int[]{PACKAGES_SLOT};
    public static final int[] AGREEMENT_PLUS_PACKAGES_SLOTS = ArrayUtils.addAll(AGREEMENT_SLOTS, PACKAGES_SLOTS);
    public static final int[] INPUT_SLOTS = new int[]{2, 3, 4, 5, 6, 7};
    public static final int[] OUTPUT_SLOTS = new int[]{8, 9, 10, 11, 12, 13};
    public static final int[] INPUT_PLUS_OUTPUT_SLOTS = ArrayUtils.addAll(INPUT_SLOTS, OUTPUT_SLOTS);

    public static final int PACKAGER_WORK_RADIUS = 3;
    public static final int PACKAGER_LAST_WORK_THRESHOLD = 20 * 40; // 40 seconds = 800 ticks

    public static final int CONTAINER_DATA_SIZE = 3;
    public static final int CONTAINER_DATA_PROGRESS = 0;
    public static final int CONTAINER_DATA_DURATION = 1;
    public static final int CONTAINER_DATA_CAN_DELIVER_MANUALLY = 2;

    protected final ContainerData containerData = new ContainerData() {
        public int get(int id) {
            return switch (id) {
                case CONTAINER_DATA_PROGRESS -> DeliveryTableBlockEntity.this.progress;
                case CONTAINER_DATA_DURATION -> DeliveryTableBlockEntity.this.getDeliveryTime();
                case CONTAINER_DATA_CAN_DELIVER_MANUALLY ->
                        Config.MANUAL_DELIVERY_ALLOWED.get() && DeliveryTableBlockEntity.this.canDeliverManually ? 1 : 0;
                default -> 0;
            };
        }

        public void set(int id, int value) {
            if (id == CONTAINER_DATA_PROGRESS)
                DeliveryTableBlockEntity.this.progress = value;
            else if (id == CONTAINER_DATA_CAN_DELIVER_MANUALLY)
                DeliveryTableBlockEntity.this.canDeliverManually = Config.MANUAL_DELIVERY_ALLOWED.get() && value == 1;
        }

        public int getCount() {
            return CONTAINER_DATA_SIZE;
        }
    };

    protected final ItemStackHandler inventory;
    protected SidedInvWrapper[] inventoryHandlers;
    protected int progress = 0;
    protected boolean canDeliverManually = false;
    protected boolean deliveringManually = false;

    protected DeliveryAgreement agreement = DeliveryAgreement.EMPTY;
    protected boolean voidAgreementOnBreak;
    protected AgreementTableLock agreementLock = new AgreementTableLock(this);
    protected UUID ownerUUID = Util.NIL_UUID;

    public DeliveryTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(WaresBlockEntities.DELIVERY_TABLE.get(), pos, blockState);
        inventory = createInventory();
        inventoryHandlers = new SidedInvWrapper[]{
                new SidedInvWrapper(this, Direction.DOWN),
                new SidedInvWrapper(this, Direction.NORTH),
                new SidedInvWrapper(this, Direction.UP)
        };
    }

    public void serverTick() {
        if (level == null)
            return;

        convertAgreementStackIfNeeded();

        ItemStack agreementItem = getAgreementItem();
        if (!agreementItem.is(WaresItems.DELIVERY_AGREEMENT.get())) {
            if (progress > 0) {
                resetProgress();
                setChanged();
            }

            if (Config.MOVE_COMPLETED_AGREEMENT_TO_OUTPUT.get() && agreementItem.is(WaresItems.COMPLETED_DELIVERY_AGREEMENT.get())) {
                ItemStack agreementStack = getAgreementItem();
                for (int outputSlot : OUTPUT_SLOTS) {
                    if (getItem(outputSlot).isEmpty()) {
                        setItem(outputSlot, agreementStack);
                        setAgreementItem(ItemStack.EMPTY);
                        break;
                    }
                }
            }

            return;
        }

        int prevProgress = progress;
        Deliverability deliverability = getDeliverability();

        if (deliverability == Deliverability.CAN_DELIVER) {
            boolean packagerWorkingAtTable = isPackagerWorkingAtTable();
            if (!deliveringManually && Config.PACKAGER_REQUIRED.get() && !packagerWorkingAtTable) {
                canDeliverManually = Config.MANUAL_DELIVERY_ALLOWED.get();
                return;
            } else
                canDeliverManually = false;

            if (deliveringManually && packagerWorkingAtTable) {
                // Adjusting progress to not complete instantly when worker arrives.
                double completion = (progress / (double) getDeliveryTime());
                progress = Math.round((float) (agreement.getDeliveryTimeOrDefault() * completion));

                deliveringManually = false;
            }

            progress++;
        } else if (deliverability != Deliverability.NO_SPACE_FOR_OUTPUT)
            resetProgress();

        if (progress >= getDeliveryTime()) {
            int deliveredPackages = deliver(getBatchSize());
            if (deliveredPackages > 0)
                onBatchDelivered(deliveredPackages);
        }

        if (prevProgress != progress)
            setChanged();
    }

    private void onBatchDelivered(int deliveredBatches) {
        if (level == null)
            return;

        deliveringManually = false;
        level.playSound(null, getBlockPos(), WaresSoundEvents.CARDBOARD_FALL.get(), SoundSource.BLOCKS,
                0.85f, level.getRandom().nextFloat() * 0.1f + 0.95f);
        if (!getAgreement().isInfinite()) {
            level.playSound(null, getBlockPos(), WaresSoundEvents.WRITING.get(), SoundSource.BLOCKS,
                    0.5f, level.getRandom().nextFloat() * 0.1f + 0.95f);
        }

        Optional<Villager> worker = getPackagerWorker(16);
        if (worker.isPresent()) {
            Villager packager = worker.get();
            int xp = packager.getVillagerXp() + deliveredBatches;
            packager.setVillagerXp(xp);

            int villagerLevel = packager.getVillagerData().getLevel();
            if (VillagerData.canLevelUp(villagerLevel) && xp >= Config.getMaxXpPerLevel(villagerLevel)) {
                level.playSound(null, packager, SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.75f, 1);
                packager.increaseProfessionLevelOnUpdate = true;
                packager.updateMerchantTimer = 30;
            }
        }

        @Nullable ServerPlayer player = getAwardedPlayer();

        triggerAdvancement(Wares.AdvancementTriggers.BATCH_DELIVERED, player);
        KubeJSIntegration.batchDelivered(this, player);
    }

    protected boolean isPackagerWorkingAtTable() {
        Optional<Villager> worker = getPackagerWorker(PACKAGER_WORK_RADIUS);
        if (worker.isEmpty())
            return false;

        if (!Config.PACKAGER_SHOULD_BE_WORKING.get())
            return true;

        Villager packager = worker.get();
        final long lastWorkedAt = packager.getBrain().getMemory(MemoryModuleType.LAST_WORKED_AT_POI).orElse(-1L);

        if (lastWorkedAt < 0L)
            return false;

        assert level != null;
        final int timeSinceLastWork = (int) (level.getGameTime() - lastWorkedAt);
        return timeSinceLastWork < PACKAGER_LAST_WORK_THRESHOLD;
    }

    public Optional<Villager> getPackagerWorker(final int radius) {
        assert level != null;
        if (level.isClientSide)
            throw new IllegalStateException("Should not be called client-side. Only server has info about villager job site.");

        List<Villager> villagersInRadius = level.getEntitiesOfClass(Villager.class, new AABB(getBlockPos()).inflate(radius));

        for (Villager villager : villagersInRadius) {
            if (villager.getVillagerData().getProfession() == WaresProfessions.PACKAGER.get()) {
                Optional<GlobalPos> jobSiteMemory = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
                if (jobSiteMemory.isPresent() && jobSiteMemory.get().pos().equals(getBlockPos()))
                    return Optional.of(villager);
            }
        }

        return Optional.empty();
    }

    public DeliveryAgreement getAgreement() {
        return agreement;
    }

    public int getBatchSize() {
        Optional<Villager> worker = getPackagerWorker(PACKAGER_WORK_RADIUS);
        int packages = Config.DELIVERIES_REQUIRE_BOXES.get() ? getItem(PACKAGES_SLOT).getCount() : Integer.MAX_VALUE;
        int villagerLevel = worker.map(villager -> villager.getVillagerData().getLevel()).orElse(1);
        return Math.min(packages, Config.getBatchSizeForLevel(villagerLevel));
    }

    protected int getDeliveryTime() {
        int time = agreement.getDeliveryTimeOrDefault();
        return deliveringManually ? Math.round(time * Config.MANUAL_DELIVERY_TIME_MODIFIER.get().floatValue()) : time;
    }

    protected void resetProgress() {
        progress = 0;
        deliveringManually = false;
        canDeliverManually = false;
    }

    public void startManualDelivery() {
        if (Config.MANUAL_DELIVERY_ALLOWED.get() && canDeliverManually && !deliveringManually && !isPackagerWorkingAtTable() && getDeliverability() == Deliverability.CAN_DELIVER) {
            deliveringManually = true;
            canDeliverManually = false;

            // Adjusting progress to manual delivery time modifier:
            int duration = agreement.getDeliveryTimeOrDefault();
            double completion = progress / (double) duration;
            progress = (int) Math.round(getDeliveryTime() * completion);
        }
    }

    protected int deliver(final int batchCount) {
        int deliveredCount = 0;
        for (int i = 0; i < batchCount; i++) {
            // First check is just to be sure,
            // Afterward it is necessary to check because items have changed.
            if (getDeliverability() != Deliverability.CAN_DELIVER)
                return deliveredCount;

            consumePackage();
            consumeFromInputSlots(agreement.getRequested());
            insertCopiesToOutputSlots(agreement.getPayment());
            deliveredCount++;

            assert level != null;

            agreement.onDeliver();

            agreement.toItemStack(getAgreementItem());
            sendUpdateToNearbyClients();

            if (agreement.isCompleted()) {
                boolean almostExpired = getAgreement().canExpire() && getAgreement().getExpireTimestamp() - level.getGameTime() < 20 * 60; // 1 min
                if (almostExpired)
                    getAgreementItem().getOrCreateTag().putBoolean("almostExpired", true);
                int experience = getAgreement().getExperience();
                if (experience > 0 && level instanceof ServerLevel serverLevel)
                    ExperienceOrb.award(serverLevel, Vec3.atCenterOf(getBlockPos()).add(0, 0.5f, 0), experience);
                break;
            }
        }

        resetProgress();
        return deliveredCount;
    }

    private void consumePackage() {
        if (Config.DELIVERIES_REQUIRE_BOXES.get())
            removeItem(PACKAGES_SLOT, 1);
    }

    protected Deliverability getDeliverability() {
        if (agreement.isEmpty() || !agreement.canDeliver(level != null ? level.getGameTime() : 0))
            return Deliverability.AGREEMENT_INVALID;
        if (!hasPackage())
            return Deliverability.NO_PACKAGES;
        if (!hasRequestedItems())
            return Deliverability.NO_INPUT;
        if (!hasSpaceForPayment())
            return Deliverability.NO_SPACE_FOR_OUTPUT;

        return Deliverability.CAN_DELIVER;
    }

    protected boolean hasPackage() {
        return !getItem(PACKAGES_SLOT).isEmpty() || !Config.DELIVERIES_REQUIRE_BOXES.get();
    }

    protected boolean hasRequestedItems() {
        List<RequestedItem> requestedItems = agreement.getRequested();

        List<ItemStack> inputStacks = new ArrayList<>();
        for (int slotIndex : INPUT_SLOTS) {
            ItemStack stackInSlot = inventory.getStackInSlot(slotIndex);
            if (!stackInSlot.isEmpty())
                inputStacks.add(stackInSlot.copy());
        }

        for (RequestedItem requestedItem : requestedItems) {
            if (requestedItem.isEmpty())
                break;
            int requiredCount = requestedItem.getCount();

            for (ItemStack stack : inputStacks) {
                if (!stack.isEmpty() && requestedItem.matches(stack)) {
                    ItemStack split = stack.split(requiredCount);
                    requiredCount -= split.getCount();
                }

                if (requiredCount <= 0)
                    break;
            }

            if (requiredCount > 0)
                return false;
        }

        return true;
    }

    /**
     * This inventory is used to check if paymentItems would fit in the output slots by fake adding items to it.
     * There probably exists a simpler way.
     */
    private final SimpleContainer outputSpaceCheckContainer = new SimpleContainer(6);

    protected boolean hasSpaceForPayment() {
        outputSpaceCheckContainer.clearContent();

        int i = 0;
        for (int slotIndex : OUTPUT_SLOTS) {
            outputSpaceCheckContainer.setItem(i, inventory.getStackInSlot(slotIndex).copy());
            i++;
        }

        for (ItemStack stack : getAgreement().getPayment()) {
            if (!outputSpaceCheckContainer.addItem(stack.copy()).isEmpty())
                return false;
        }

        return true;
    }

    protected void consumeFromInputSlots(List<RequestedItem> requestedItems) {
        for (RequestedItem requestedItem : requestedItems) {
            int requiredCount = requestedItem.getCount();

            for (int slotIndex : INPUT_SLOTS) {
                if (requestedItem.matches(inventory.getStackInSlot(slotIndex))) {
                    ItemStack extractedStack = inventory.extractItem(slotIndex, requiredCount, false);
                    requiredCount -= extractedStack.getCount();

                    if (requiredCount <= 0)
                        break;
                }
            }
        }
    }

    protected void insertCopiesToOutputSlots(List<ItemStack> paymentItems) {
        for (ItemStack stack : paymentItems) {
            ItemStack insertedStack = stack.copy();
            for (int slotIndex : OUTPUT_SLOTS) {
                insertedStack = inventory.insertItem(slotIndex, insertedStack, false);
                if (insertedStack.isEmpty())
                    break;
            }
        }
    }

    public boolean isAgreementLocked() {
        return agreementLock.isLocked();
    }

    public boolean shouldVoidAgreementOnBreak() {
        return voidAgreementOnBreak;
    }


    // <Container>

    protected ItemStackHandler createInventory() {
        return new ItemStackHandler(SLOTS) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (slot == AGREEMENT_SLOT)
                    return stack.getItem() instanceof DeliveryAgreementItem;
                else if (slot == PACKAGES_SLOT)
                    return Config.DELIVERIES_REQUIRE_BOXES.get() && stack.is(Wares.Tags.Items.DELIVERY_BOXES);
                return super.isItemValid(slot, stack);
            }

           
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot == PACKAGES_SLOT && !Config.DELIVERIES_REQUIRE_BOXES.get())
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            protected void onContentsChanged(int slot) {
                if (slot == AGREEMENT_SLOT) {
                    updateBlockStateIfNeeded();
                    agreement = DeliveryAgreement.fromItemStack(getItem(AGREEMENT_SLOT))
                            .orElse(DeliveryAgreement.EMPTY);
                    resetProgress();
                }
                setChanged();
            }
        };
    }

    public IItemHandlerModifiable getInventory() {
        return inventory;
    }

    public int getContainerSize() {
        return inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.getStackInSlot(slot);
    }

    public ItemStack getAgreementItem() {
        return getItem(AGREEMENT_SLOT);
    }

    public ItemStack extractAgreementItem() {
        return removeItem(AGREEMENT_SLOT, 1);
    }

    public void setAgreementItem(ItemStack stack) {
        setItem(AGREEMENT_SLOT, stack);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return inventory.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = inventory.getStackInSlot(slot);
        inventory.setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }

    @Override
    public int [] getSlotsForFace(Direction side) {
        return switch (side) {
            case DOWN -> OUTPUT_SLOTS;
            case UP -> Config.DELIVERIES_REQUIRE_BOXES.get() ? AGREEMENT_PLUS_PACKAGES_SLOTS : AGREEMENT_SLOTS;
            case NORTH, SOUTH, WEST, EAST ->
                    Config.TABLE_OUTPUTS_FROM_SIDES.get() ? INPUT_PLUS_OUTPUT_SLOTS : INPUT_SLOTS;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack pStack, Direction direction) {
        return index >= OUTPUT_SLOTS[0];
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public boolean canPlaceItem(int slotIndex, ItemStack stack) {
        return (slotIndex == AGREEMENT_SLOT && stack.getItem() instanceof DeliveryAgreementItem)
                || (slotIndex == PACKAGES_SLOT && stack.is(Wares.Tags.Items.DELIVERY_BOXES))
                || (slotIndex >= INPUT_SLOTS[0] && slotIndex < OUTPUT_SLOTS[0]);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.wares.delivery_table");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        var itemList = NonNullList.<ItemStack>create();

        for (var i = 0; i < SLOTS; i++)
        {
            itemList.add(inventory.getStackInSlot(i));
        }

        return itemList;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemStacks) {
        for (var i = 0; i < SLOTS; i++)
        {
            inventory.setStackInSlot(i, itemStacks.get(i));
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new DeliveryTableMenu(containerId, inventory, this, containerData);
    }


    // <Load/Save>

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
        deliveringManually = tag.getBoolean("DeliveringManually");
        voidAgreementOnBreak = tag.getBoolean("VoidAgreementOnBreak");
        agreement = DeliveryAgreement.fromItemStack(getAgreementItem()).orElse(DeliveryAgreement.EMPTY);
        agreementLock.load(tag.getCompound("AgreementLock"));

        if (tag.contains("Owner", Tag.TAG_INT_ARRAY))
            ownerUUID = tag.getUUID("Owner");

        updateBlockStateIfNeeded();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putBoolean("DeliveringManually", deliveringManually);
        if (voidAgreementOnBreak)
            tag.putBoolean("VoidAgreementOnBreak", true);
        tag.put("AgreementLock", agreementLock.save(new CompoundTag()));

        if (!Util.NIL_UUID.equals(ownerUUID))
            tag.putUUID("Owner", ownerUUID);
    }

    // <Updating>

    protected void convertAgreementStackIfNeeded() {
        if (level == null || level.isClientSide)
            return;

        if (!getAgreementItem().is(WaresItems.DELIVERY_AGREEMENT.get()))
            return;

        if (getAgreement().isCompleted()) {
            setAgreementItem(DeliveryAgreementItem.convertToCompleted(getAgreementItem()));
            @Nullable ServerPlayer player = getAwardedPlayer();
            triggerAdvancement(Wares.AdvancementTriggers.AGREEMENT_COMPLETED, player);
            KubeJSIntegration.agreementCompleted(this, player);
        }
        else if (getAgreement().isExpired(level.getGameTime())) {
            setAgreementItem(DeliveryAgreementItem.convertToExpired(getAgreementItem()));
            @Nullable ServerPlayer player = getAwardedPlayer();
            triggerAdvancement(Wares.AdvancementTriggers.AGREEMENT_EXPIRED, player);
            KubeJSIntegration.agreementExpired(this, player);
        }
    }

    protected void updateBlockStateIfNeeded() {
        AgreementType type = AgreementType.fromItemStack(getAgreementItem());
        BlockState currentBlockState = getBlockState();
        if (level != null && currentBlockState.getValue(DeliveryTableBlock.AGREEMENT) != type)
            level.setBlockAndUpdate(worldPosition, currentBlockState.setValue(DeliveryTableBlock.AGREEMENT, type));
    }


    protected void sendUpdateToNearbyClients() {
        assert level != null;
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, new AABB(getBlockPos()).inflate(32));
        for (ServerPlayer player : nearbyPlayers) {
            player.connection.send(this.getUpdatePacket());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean trySetOwner(ServerPlayer serverPlayer) {
        if (Util.NIL_UUID.equals(ownerUUID) || Config.LAST_PLAYER_IS_OWNER.get()) {
            ownerUUID = serverPlayer.getUUID();
            setChanged();
            return true;
        }

        return false;
    }

    public void triggerAdvancement(DeliveryTableTrigger trigger, @Nullable ServerPlayer player) {
        if (player != null) {
            trigger.trigger(player, this);
        }
    }

    private @Nullable ServerPlayer getAwardedPlayer() {
        if (level == null) return null;

        if (!Util.NIL_UUID.equals(ownerUUID)) {
            @Nullable Player owner = level.getPlayerByUUID(ownerUUID);
            if (owner instanceof ServerPlayer serverPlayer) {
                return serverPlayer;
            }
        }

        if (Config.TRIGGER_FOR_NEAREST_PLAYER.get()) {
            @Nullable Player nearestPlayer = level.getNearestPlayer(TargetingConditions.forNonCombat(),
                    getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
            if (nearestPlayer instanceof ServerPlayer serverPlayer) {
                return serverPlayer;
            }
        }

        return null;
    }

    public void onPlacedBy(LivingEntity placer) {
        if (placer instanceof ServerPlayer serverPlayer) {
            trySetOwner(serverPlayer);
        }
    }
}
