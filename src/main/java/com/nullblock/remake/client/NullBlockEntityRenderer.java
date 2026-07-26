package com.nullblock.remake.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nullblock.remake.block.entity.NullBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders the disguise block's model in place of the invisible NullBlock,
 * with ambient occlusion enabled so the disguise blends in like a normal
 * block instead of looking flat-lit.
 */
public class NullBlockEntityRenderer implements BlockEntityRenderer<NullBlockEntity> {

    public NullBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(NullBlockEntity entity, float partialTick, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState disguise = entity.getDisguiseState();
        if (disguise == null || disguise.isAir()) {
            return;
        }

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        RenderType renderType = net.minecraft.client.renderer.ItemBlockRenderTypes.getChunkRenderType(disguise);

        // Wrap the level so neighbor lookups resolve adjacent NullBlocks to
        // their disguise state; this lets checkSides=true actually cull
        // shared faces between two adjacent disguised NullBlocks instead of
        // seeing the real (invisible) NullBlock and never occluding.
        DisguiseAwareBlockGetter culledLevel = new DisguiseAwareBlockGetter(entity.getLevel());

        poseStack.pushPose();
        dispatcher.getModelRenderer().tesselateBlock(
                culledLevel,
                dispatcher.getBlockModel(disguise),
                disguise,
                entity.getBlockPos(),
                poseStack,
                bufferSource.getBuffer(renderType),
                true,
                net.minecraft.util.RandomSource.create(),
                disguise.getSeed(entity.getBlockPos()),
                packedOverlay
        );
        poseStack.popPose();
    }
}
