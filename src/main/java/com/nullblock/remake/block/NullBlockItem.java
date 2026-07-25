package com.nullblock.remake.block;

import com.nullblock.remake.block.entity.NullBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * BlockItem for Null Block. Carries the "filled" disguise (if any) as the
 * {@link ModDataComponents#DISGUISE_BLOCK} data component: an empty stack has
 * no component, a "filled" stack (from pick-block or /give ...[nullblock_remake:block="..."])
 * has it set, and placing a filled stack immediately applies that disguise to
 * the placed NullBlockEntity, same as manually right-clicking a disguise item
 * onto an empty null block.
 */
public class NullBlockItem extends BlockItem {

    public NullBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Nullable
    private Block getDisguiseBlock(ItemStack stack) {
        return stack.get(ModDataComponents.DISGUISE_BLOCK.get());
    }

    @Override
    public Component getName(ItemStack stack) {
        Block disguise = getDisguiseBlock(stack);
        if (disguise != null) {
            return Component.translatable("item.nullblock_remake.null_block.filled", disguise.getName());
        }
        return super.getName(stack);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player,
                                                  ItemStack stack, BlockState state) {
        boolean result = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        Block disguise = getDisguiseBlock(stack);
        if (disguise != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof NullBlockEntity nullBe) {
                nullBe.setDisguiseState(disguise.defaultBlockState());
            }
        }
        return result;
    }
}
