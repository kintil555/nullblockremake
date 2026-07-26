package com.nullblock.remake.mixin;

import com.nullblock.remake.block.NullBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Root-cause fix: Block#canBeReplaced only governs placement/explosion
 * replacement. Fluid spreading (WaterFluid/LavaFluid, both extending
 * FlowingFluid -> Fluid) decides independently via
 * Fluid#canBeReplaceWith(FluidState, BlockGetter, BlockPos, Fluid, Direction).
 * That is the actual method vanilla calls before a fluid overwrites a
 * non-solid block, so NullBlock (noCollission -> blocksMotion() == false)
 * was always eligible for washing away regardless of Block-side overrides.
 * Mixin here is the only reliable interception point common to every
 * vanilla fluid.
 */
@Mixin(Fluid.class)
public abstract class FluidMixin {

    @Inject(
            method = "canBeReplaceWith(Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/Direction;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nullblock_remake$preventReplace(FluidState state, BlockGetter level, BlockPos pos, Fluid newFluid,
                                                   Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos).getBlock() instanceof NullBlock) {
            cir.setReturnValue(false);
        }
    }
}
