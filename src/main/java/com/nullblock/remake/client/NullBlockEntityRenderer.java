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
        boolean hasRealDisguise = disguise != null && !disguise.isAir();

        // No disguise yet: fall back to rendering the NullBlock's own model
        // (null_block texture) so the block is visible instead of invisible.
        if (!hasRealDisguise) {
            disguise = entity.getBlockState();
        }

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        RenderType renderType = net.minecraft.client.renderer.ItemBlockRenderTypes.getChunkRenderType(disguise);

        // Wrap the level so neighbor lookups resolve adjacent NullBlocks to
        // their disguise state; this lets checkSides=true actually cull
        // shared faces between two adjacent disguised NullBlocks instead of
        // seeing the real (invisible) NullBlock and never occluding.
        DisguiseAwareBlockGetter culledLevel = new DisguiseAwareBlockGetter(entity.getLevel());

        // checkSides face-culls each side of the model against
        // Block.shouldRenderFace, which for the *placeholder* model
        // (NullBlock's own cube_all) is evaluated against NullBlock's own
        // occlusion shape — always empty by design (see NullBlock#getOcclusionShape),
        // so the cube_all placeholder gets its faces culled inconsistently
        // against itself instead of against real neighbors. The placeholder
        // must always render as a full, intact cube, so it skips side
        // culling entirely. Once a real disguise is assigned, checkSides
        // stays true so the disguise behaves like the real block it mimics
        // (culling shared faces against solid neighbors and other disguised
        // NullBlocks via DisguiseAwareBlockGetter).
        boolean checkSides = hasRealDisguise;

        poseStack.pushPose();
        dispatcher.getModelRenderer().tesselateBlock(
                culledLevel,
                dispatcher.getBlockModel(disguise),
                disguise,
                entity.getBlockPos(),
                poseStack,
                bufferSource.getBuffer(renderType),
                checkSides,
                net.minecraft.util.RandomSource.create(),
                disguise.getSeed(entity.getBlockPos()),
                packedOverlay
        );
        poseStack.popPose();
    }
}
