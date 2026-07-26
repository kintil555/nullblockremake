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

@Mixin(Fluid.class)
public abstract class FluidMixin {

    @Inject(method = "canBeReplacedWith", at = @At("HEAD"), cancellable = true)
    private void nullblock_remake$preventReplace(FluidState state, BlockGetter level, BlockPos pos, Fluid newFluid,
                                                   Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos).getBlock() instanceof NullBlock) {
            cir.setReturnValue(false);
        }
    }
}
