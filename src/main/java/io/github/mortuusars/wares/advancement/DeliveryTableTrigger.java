package io.github.mortuusars.wares.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.wares.block.entity.DeliveryTableBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

@MethodsReturnNonnullByDefault
public class DeliveryTableTrigger extends SimpleCriterionTrigger<DeliveryTableTrigger.TriggerInstance> {
    public DeliveryTableTrigger() {}

    public void trigger(ServerPlayer player, DeliveryTableBlockEntity tableBlockEntity) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(tableBlockEntity));
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            AgreementPredicate agreement,
            NbtPredicate agreementNbt,
            LocationPredicate location
    )
            implements SimpleCriterionTrigger.SimpleInstance
    {
        public static final Codec<TriggerInstance> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                                AgreementPredicate.CODEC.fieldOf("agreement").forGetter(TriggerInstance::agreement),
                                NbtPredicate.CODEC.fieldOf("agreementNbt").forGetter(TriggerInstance::agreementNbt),
                                LocationPredicate.CODEC.fieldOf("location").forGetter(TriggerInstance::location)
                        ).apply(instance, TriggerInstance::new)

        );

        public boolean matches(DeliveryTableBlockEntity tableBlockEntity) {
            if (!(tableBlockEntity.getLevel() instanceof ServerLevel serverLevel))
                return false;

            BlockPos pos = tableBlockEntity.getBlockPos();
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();

            return agreement.matches(tableBlockEntity.getAgreement())
                    && agreementNbt.matches(tableBlockEntity.getAgreementItem())
                    && location.matches(serverLevel, x, y, z);
        }
    }
}