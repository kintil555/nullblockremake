package com.nullblock.remake.block.entity;

import com.nullblock.remake.NullBlockRemakeMod;
import com.nullblock.remake.block.NullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Darkens the camera's view while the player's eyes are physically inside a
 * NullBlock that currently has a visually solid disguise.
 *
 * NullBlock is always fully passable (see NullBlock#getCollisionShape,
 * always Shapes.empty()), so the player's eyes can end up inside a block
 * that LOOKS completely solid (e.g. disguised as stone or wood) without
 * vanilla's own suffocation/in-wall darkening ever kicking in — vanilla
 * only darkens the screen based on real collision/occlusion at the eye
 * position, and NullBlock reports empty collision there.
 *
 * Implemented via ViewportEvent (ComputeFogColor + RenderFog) rather than a
 * GUI overlay layer, since ViewportEvent is a stable part of Forge's client
 * event API in 1.21.4.
 */
@Mod.EventBusSubscriber(modid = NullBlockRemakeMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class NullBlockCameraOverlay {

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        if (!isEyeInsideDisguisedNullBlock()) {
            return;
        }
        // Pure black fog color.
        event.setRed(0.0F);
        event.setGreen(0.0F);
        event.setBlue(0.0F);
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!isEyeInsideDisguisedNullBlock()) {
            return;
        }
        // Push the fog as close to the camera as possible so the black fog
        // color set above covers the entire view, the same visual result as
        // vanilla's "suffocating inside a block" darkening.
        event.setNearPlaneDistance(-8.0F);
        event.setFarPlaneDistance(0.75F);
        event.setCanceled(true);
    }

    private static boolean isEyeInsideDisguisedNullBlock() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) {
            return false;
        }

        Vec3 eyePos = player.getEyePosition();
        BlockPos eyeBlockPos = BlockPos.containing(eyePos);
        BlockState stateAtEye = player.level().getBlockState(eyeBlockPos);

        if (!(stateAtEye.getBlock() instanceof NullBlock)) {
            return false;
        }

        BlockEntity be = player.level().getBlockEntity(eyeBlockPos);
        if (!(be instanceof NullBlockEntity nullBe) || !nullBe.hasDisguise()) {
            // No disguise: NullBlock renders its own translucent/invisible
            // placeholder, not a solid appearance — nothing to darken for.
            return false;
        }

        BlockState disguise = nullBe.getDisguiseState();

        // Only darken when the disguise LOOKS solid at this exact point.
        // Using the disguise's own "is a solid full-cube render" signal
        // (rather than just "has any disguise") avoids darkening the view
        // for disguises that aren't full/opaque blocks (glass, slabs,
        // etc.), matching how vanilla only darkens when the eye position is
        // actually inside solid, opaque geometry.
        return disguise.isSolidRender();
    }
}
