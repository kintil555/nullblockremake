package com.nullblock.remake.api.trigger;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** Immutable data passed to a {@link NullTriggerListener} when a trigger fires. */
public final class NullTriggerContext {
    private final NullTriggerType type;
    private final String customId;
    private final Level level;
    private final BlockPos pos;
    private final Entity entity;

    public NullTriggerContext(NullTriggerType type, @Nullable String customId, Level level, BlockPos pos, @Nullable Entity entity) {
        this.type = type;
        this.customId = customId;
        this.level = level;
        this.pos = pos;
        this.entity = entity;
    }

    public NullTriggerType type() { return type; }
    @Nullable public String customId() { return customId; }
    public Level level() { return level; }
    public BlockPos pos() { return pos; }
    @Nullable public Entity entity() { return entity; }
}
