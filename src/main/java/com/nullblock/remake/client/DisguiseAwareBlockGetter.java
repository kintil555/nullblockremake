package com.nullblock.remake.client;

import com.nullblock.remake.block.NullBlock;
import com.nullblock.remake.block.entity.NullBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

/**
 * Delegates every read to the real level, except getBlockState: any
 * neighboring NullBlock reports its disguise state instead of the real
 * (invisible) NullBlock state. This lets vanilla's own face-occlusion logic
 * (used by the model renderer when checkSides=true) correctly cull shared
 * faces between two adjacent NullBlocks wearing the same disguise, since
 * NullBlock itself always reports RenderShape.INVISIBLE / empty shapes and
 * would otherwise never occlude anything.
 */
public class DisguiseAwareBlockGetter implements BlockAndTintGetter {

    private final BlockAndTintGetter delegate;

    public DisguiseAwareBlockGetter(BlockAndTintGetter delegate) {
        this.delegate = delegate;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        BlockState state = delegate.getBlockState(pos);
        if (state.getBlock() instanceof NullBlock) {
            BlockEntity be = delegate.getBlockEntity(pos);
            if (be instanceof NullBlockEntity nullBe && nullBe.hasDisguise()) {
                return nullBe.getDisguiseState();
            }
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return delegate.getFluidState(pos);
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return delegate.getBlockEntity(pos);
    }

    @Override
    public int getLightEmission(BlockPos pos) {
        return delegate.getLightEmission(pos);
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public int getMinY() {
        return delegate.getMinY();
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return delegate.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver resolver) {
        return delegate.getBlockTint(pos, resolver);
    }

    @Override
    public float getShade(Direction direction, boolean shaded) {
        return delegate.getShade(direction, shaded);
    }

    @Override
    public int getBrightness(LightLayer layer, BlockPos pos) {
        return delegate.getBrightness(layer, pos);
    }

    @Override
    public int getRawBrightness(BlockPos pos, int amount) {
        return delegate.getRawBrightness(pos, amount);
    }

    @Override
    public boolean canSeeSky(BlockPos pos) {
        return delegate.canSeeSky(pos);
    }
}
