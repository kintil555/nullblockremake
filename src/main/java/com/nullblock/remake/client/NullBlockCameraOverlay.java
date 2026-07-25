package com.nullblock.remake.client;

import com.nullblock.remake.block.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Instantly darkens the player's view whenever the camera's eye position is
 * inside a NullBlock, since the block is invisible/passable and would
 * otherwise show whatever geometry is behind it. No fade — the change must
 * be immediate to sell the "null" effect.
 */
public final class NullBlockCameraOverlay {

    private NullBlockCameraOverlay() {
    }

    public static void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        BlockPos pos = BlockPos.containing(mc.gameRenderer.getMainCamera().getPosition());
        BlockState state = mc.level.getBlockState(pos);
        if (!state.is(ModBlocks.NULL_BLOCK)) {
            return;
        }

        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), 0xFF000000);
    }
}
