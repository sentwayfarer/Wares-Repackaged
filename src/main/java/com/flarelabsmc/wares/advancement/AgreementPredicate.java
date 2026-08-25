package com.flarelabsmc.wares.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.flarelabsmc.wares.data.agreement.DeliveryAgreement;
import net.minecraft.advancements.critereon.MinMaxBounds;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public record AgreementPredicate(
        Optional<String> idPredicate,
        Optional<String> sealPredicate,
        MinMaxBounds.Ints orderedPredicate,
        MinMaxBounds.Ints deliveredPredicate,
        MinMaxBounds.Ints experiencePredicate,
        MinMaxBounds.Ints deliveryTimePredicate
) {
   public static final Codec<AgreementPredicate> CODEC = RecordCodecBuilder.create(instance ->
           instance.group(
                   Codec.STRING.optionalFieldOf("id").forGetter(AgreementPredicate::idPredicate),
                   Codec.STRING.optionalFieldOf("seal").forGetter(AgreementPredicate::sealPredicate),
                   MinMaxBounds.Ints.CODEC.optionalFieldOf("ordered", MinMaxBounds.Ints.ANY).forGetter(AgreementPredicate::orderedPredicate),
                   MinMaxBounds.Ints.CODEC.optionalFieldOf("delivered", MinMaxBounds.Ints.ANY).forGetter(AgreementPredicate::deliveredPredicate),
                   MinMaxBounds.Ints.CODEC.optionalFieldOf("experience", MinMaxBounds.Ints.ANY).forGetter(AgreementPredicate::experiencePredicate),
                   MinMaxBounds.Ints.CODEC.optionalFieldOf("delivery_time", MinMaxBounds.Ints.ANY).forGetter(AgreementPredicate::deliveryTimePredicate)
           ).apply(instance, AgreementPredicate::new)
   );

    public static final AgreementPredicate ANY = new AgreementPredicate(
            Optional.empty(),
            Optional.empty(),
            MinMaxBounds.Ints.ANY,
            MinMaxBounds.Ints.ANY,
            MinMaxBounds.Ints.ANY,
            MinMaxBounds.Ints.ANY
    );

    public boolean matches(DeliveryAgreement agreement) {
        if (this == ANY)
            return true;

        if (idPredicate.isPresent() && !idPredicate.get().equals(agreement.getId()))
            return false;
        if (sealPredicate.isPresent() && !sealPredicate.get().equals(agreement.getSeal()))
            return false;
        return orderedPredicate.matches(agreement.getOrdered())
                && deliveredPredicate.matches(agreement.getDelivered())
                && experiencePredicate.matches(agreement.getExperience())
                && deliveryTimePredicate.matches(agreement.getDeliveryTime());
    }
}
