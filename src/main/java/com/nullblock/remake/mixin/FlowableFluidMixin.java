package com.nullblock.remake.mixin;

import com.nullblock.remake.block.NullBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowableFluidMixin {

    @Inject(method = "isSolidFace", at = @At("HEAD"), cancellable = true)
    private void nullblock_remake$treatAsSolidFace(BlockGetter level, BlockPos neighborPos, Direction side,
                                                     CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(neighborPos).getBlock() instanceof NullBlock) {
            cir.setReturnValue(true);
        }
    }
}
