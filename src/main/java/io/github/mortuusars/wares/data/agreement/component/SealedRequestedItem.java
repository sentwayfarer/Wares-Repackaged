package io.github.mortuusars.wares.data.agreement.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record SealedRequestedItem(Either<TagKey<Item>, Item> tagOrItem, Either<Integer, SteppedInt> count,
                                  @Nullable CompoundTag tag, CompoundTagCompareBehavior tagCompareBehavior) {

    public static final Codec<SealedRequestedItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.either(TagKey.hashedCodec(Registries.ITEM), BuiltInRegistries.ITEM.byNameCodec()).fieldOf("id").forGetter(SealedRequestedItem::tagOrItem),
                    Codec.either(ExtraCodecs.POSITIVE_INT, SteppedInt.CODEC).optionalFieldOf("Count", Either.left(1)).forGetter(SealedRequestedItem::count),
                    CompoundTag.CODEC.optionalFieldOf("tag").forGetter(sri -> Optional.ofNullable(sri.tag())),
                    StringRepresentable.fromEnum(CompoundTagCompareBehavior::values).optionalFieldOf("TagMatching", CompoundTagCompareBehavior.WEAK).forGetter(SealedRequestedItem::tagCompareBehavior))
            .apply(instance, SealedRequestedItem::new));

    public static final SealedRequestedItem EMPTY = new SealedRequestedItem(Either.right(Items.AIR), Either.left(1), (CompoundTag) null, CompoundTagCompareBehavior.WEAK);

    private SealedRequestedItem(Either<TagKey<Item>, Item> tagOrItem, Either<Integer, SteppedInt> count, Optional<CompoundTag> tag, CompoundTagCompareBehavior tagCompareBehavior) {
        this(tagOrItem, count, tag.orElse(null), tagCompareBehavior);
    }

    @Override
    public String toString() {
        return "RequestedItem{" +
                "tagOrItem=" + tagOrItem.map(tag -> "#" + tag.location(), item -> Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString()) +
                ", count=" + count.map(integer -> Integer.toString(integer),
                steppedInt -> String.format("SteppedInt{%s,%s,%s}", steppedInt.min(), steppedInt.max(), steppedInt.step())) +
                (tag != null ? (", tag=" + tag) : "") +
                ",TagMatching:" + tagCompareBehavior.getSerializedName() +
                '}';
    }
}
