package com.nullblock.remake.client;

import com.nullblock.remake.block.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class NullBlockRemakeModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.NULL_BLOCK_ENTITY, NullBlockEntityRenderer::new);
    }
}
