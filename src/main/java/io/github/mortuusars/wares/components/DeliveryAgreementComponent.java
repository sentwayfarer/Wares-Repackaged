package io.github.mortuusars.wares.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DeliveryAgreementComponent(
        long expireTime,
        int ordered,
        int remaining
) {
    public static final Codec<DeliveryAgreementComponent> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.LONG.fieldOf("expireTime").forGetter(DeliveryAgreementComponent::expireTime),
                    Codec.INT.fieldOf("ordered").forGetter(DeliveryAgreementComponent::ordered),
                    Codec.INT.fieldOf("remaining").forGetter(DeliveryAgreementComponent::remaining)
                ).apply(instance, DeliveryAgreementComponent::new)
            );

    public static final StreamCodec<ByteBuf, DeliveryAgreementComponent> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, DeliveryAgreementComponent::expireTime,
                    ByteBufCodecs.INT, DeliveryAgreementComponent::ordered,
                    ByteBufCodecs.INT, DeliveryAgreementComponent::remaining,
                    DeliveryAgreementComponent::new
            );
}
