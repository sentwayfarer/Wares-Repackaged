package com.flarelabsmc.wares.data.generation.provider;

import com.flarelabsmc.wares.Wares;
import com.flarelabsmc.wares.content.block.CardboardBoxBlock;
import com.flarelabsmc.wares.content.block.DeliveryTableBlock;
import com.flarelabsmc.wares.data.agreement.AgreementType;
import com.flarelabsmc.wares.registry.WaresBlocks;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockStatesAndModels extends BlockStateProvider {

    public BlockStatesAndModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen.getPackOutput(), Wares.ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        String table_path = WaresBlocks.DELIVERY_TABLE.getId().getPath();

        getVariantBuilder(WaresBlocks.DELIVERY_TABLE.get()).forAllStates(state -> {
            AgreementType agreement = state.getValue(DeliveryTableBlock.AGREEMENT);
            String agreement_suffix = agreement == AgreementType.NONE ? "" : "_agreement_" + agreement.getSerializedName();
            ModelFile.ExistingModelFile model = models().getExistingFile(
                    modLoc("block/" + table_path + agreement_suffix));
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int) state.getValue(DeliveryTableBlock.FACING).getOpposite().toYRot())
                    .build();
        });

        getVariantBuilder(WaresBlocks.CARDBOARD_BOX.get()).forAllStates(state -> {
            Int2ObjectMap<String> PACKAGES = new Int2ObjectOpenHashMap<>(
                    new int[]{1, 2, 3, 4},
                    new String[]{"one", "two", "three", "four"});
            ModelFile.ExistingModelFile model = models().getExistingFile(
                    modLoc("block/" + WaresBlocks.CARDBOARD_BOX.getId()
                            .getPath() + "_" + PACKAGES.get(state.getValue(CardboardBoxBlock.BOXES).intValue())));
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(state.getValue(CardboardBoxBlock.AXIS) == Direction.Axis.X ? 0 : 90)
                    .build();
        });

        horizontalBlock(WaresBlocks.PACKAGE.get(), models().getExistingFile(modLoc("block/" + WaresBlocks.PACKAGE.getId().getPath())));
    }
}