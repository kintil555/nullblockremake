package com.nullblock.remake.client;

import com.nullblock.remake.block.ModBlockEntities;
import com.nullblock.remake.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.renderer.RenderType;

public class NullBlockRemakeModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.NULL_BLOCK_ENTITY, NullBlockEntityRenderer::new);

        // NullBlock uses RenderShape.INVISIBLE, which defaults chunk-level
        // culling/render-layer lookups to solid(). Without this, alpha in
        // the disguise texture is clipped during neighbor face culling.
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.NULL_BLOCK, RenderType.translucent());

        HudRenderCallback.EVENT.register((graphics, tickDelta) -> NullBlockCameraOverlay.render(graphics));
    }
}
