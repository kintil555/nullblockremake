package com.nullblock.remake.block.entity;

import com.nullblock.remake.block.ModBlockEntities;
import com.nullblock.remake.block.NullBlockTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the "disguise" state for a NullBlock — the appearance the null block
 * borrows from another block, while keeping its own collision/interaction
 * behaviour (fully passable).
 *
 * Also owns the block's registration with {@link NullBlockTracker}: this is
 * what makes the radius-based API triggers (PLAYER_ENTER_RADIUS,
 * PLAYER_NEARBY_TICK) actually work. The original mod defined
 * NullBlockTracker but never called track()/untrack() from anywhere, so the
 * tracker was permanently empty and those triggers never fired. This class
 * now tracks itself on load and untracks itself on removal (setRemoved() is
 * called by vanilla both on block removal and on chunk unload).
 *
 * Other mods can read/write disguise state via
 * {@link com.nullblock.remake.api.NullBlockAPI} instead of touching this
 * class directly.
 */
public class NullBlockEntity extends BlockEntity {

    /** The block state being visually mimicked. Null = no disguise (default invisible/void look). */
    @Nullable
    private BlockState disguiseState;

    public NullBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NULL_BLOCK_ENTITY, pos, state);
    }

    @Nullable
    public BlockState getDisguiseState() {
        return disguiseState;
    }

    /**
     * Sets the block this null block should visually mimic. Pass null to clear
     * the disguise. Does not touch collision — the null block is always passable
     * regardless of disguise.
     */
    public void setDisguiseState(@Nullable BlockState state) {
        this.disguiseState = state;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean hasDisguise() {
        return disguiseState != null;
    }

    // ------------------------------------------------------------------
    // NullBlockTracker lifecycle wiring
    // ------------------------------------------------------------------

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        trackIfServer();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        untrackIfServer();
    }

    /**
     * Called by BlockEntity#clearRemoved when the block entity is placed back
     * into a valid state (e.g. after a chunk reloads it). Ensures re-tracking
     * happens even if setLevel already ran before the level was fully valid.
     */
    @Override
    public void clearRemoved() {
        super.clearRemoved();
        trackIfServer();
    }

    private void trackIfServer() {
        if (level instanceof ServerLevel serverLevel && !isRemoved()) {
            NullBlockTracker.track(serverLevel, worldPosition);
        }
    }

    private void untrackIfServer() {
        if (level instanceof ServerLevel serverLevel) {
            NullBlockTracker.untrack(serverLevel, worldPosition);
        }
    }

    // ------------------------------------------------------------------
    // Persistence
    // ------------------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (disguiseState != null) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(disguiseState.getBlock());
            tag.putString("DisguiseBlock", id.toString());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("DisguiseBlock")) {
            ResourceLocation id = ResourceLocation.parse(tag.getString("DisguiseBlock"));
            BuiltInRegistries.BLOCK.getOptional(id).ifPresentOrElse(
                    block -> disguiseState = block.defaultBlockState(),
                    () -> disguiseState = null
            );
        } else {
            disguiseState = null;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
