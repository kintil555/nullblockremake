package com.nullblock.remake.mixin;

import com.nullblock.remake.block.NullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {

    @Inject(method = "isFlowBlocked", at = @At("HEAD"), cancellable = true)
    private void nullblock_remake$treatAsSolidFace(BlockView world, BlockPos pos, Direction direction,
                                                     CallbackInfoReturnable<Boolean> cir) {
        if (world.getBlockState(pos).getBlock() instanceof NullBlock) {
            cir.setReturnValue(true);
        }
    }
}
