package com.nullblock.remake;

import com.nullblock.remake.api.trigger.NullTriggerConfig;
import com.nullblock.remake.api.trigger.NullTriggerContext;
import com.nullblock.remake.api.trigger.NullTriggerRegistry;
import com.nullblock.remake.api.trigger.NullTriggerType;
import com.nullblock.remake.block.NullBlockTracker;
import com.nullblock.remake.block.entity.NullBlockEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Server-side dispatch for Null Block API triggers. Other mods never touch
 * this class directly; they subscribe via NullBlockAPI and this class fires
 * their listeners when the relevant condition occurs.
 */
public final class ModEvents {

    private ModEvents() {}

    // Tracks entities currently "inside" a radius per (block-pos, entity),
    // so PLAYER_ENTER_RADIUS fires once on entry rather than every tick.
    // Keyed per-BlockPos so entries can be dropped in bulk when a null block
    // is untracked (see NullBlockTracker#untrack), avoiding the unbounded
    // growth the original implementation had (its String-keyed set was never
    // cleared when a block was removed/unloaded).
    private static final Map<BlockPos, Set<UUID>> INSIDE_RADIUS = new HashMap<>();

    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register(ModEvents::onEntityLoad);
        ServerTickEvents.END_SERVER_TICK.register(ModEvents::onServerTick);
    }

    /** Called by NullBlockTracker when a null block is untracked (removed/unloaded). */
    public static void clearRadiusState(BlockPos pos) {
        INSIDE_RADIUS.remove(pos);
    }

    /**
     * ENTITY_COLLISION: fired when an entity is loaded into a level occupying
     * a Null Block's position. For continuous movement-based overlap, mods
     * should combine PLAYER_NEARBY_TICK (small radius) with their own AABB
     * check in the listener.
     */
    private static void onEntityLoad(Entity entity, ServerLevel level) {
        BlockPos pos = entity.blockPosition();
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof NullBlockEntity) {
            NullTriggerRegistry.fire(new NullTriggerContext(NullTriggerType.ENTITY_COLLISION, null, level, pos, entity));
        }
    }

    /**
     * Drives PLAYER_ENTER_RADIUS / PLAYER_NEARBY_TICK checks by scanning each
     * player against tracked Null Block positions (see NullBlockTracker),
     * instead of scanning the whole world every tick.
     */
    private static void onServerTick(net.minecraft.server.MinecraftServer server) {
        if (NullTriggerRegistry.getConfigs(NullTriggerType.PLAYER_ENTER_RADIUS).isEmpty()
                && NullTriggerRegistry.getConfigs(NullTriggerType.PLAYER_NEARBY_TICK).isEmpty()) {
            return;
        }
        for (ServerLevel level : NullBlockTracker.trackedLevels()) {
            Set<BlockPos> positions = NullBlockTracker.positionsIn(level);
            if (positions.isEmpty()) continue;
            for (Player player : level.players()) {
                for (BlockPos pos : positions) {
                    evaluateRadiusTriggers(level, player, pos, level.getGameTime());
                }
            }
        }
    }

    private static void evaluateRadiusTriggers(ServerLevel level, Player player, BlockPos nullBlockPos, long gameTime) {
        double distSq = player.blockPosition().distSqr(nullBlockPos);

        for (NullTriggerConfig cfg : NullTriggerRegistry.getConfigs(NullTriggerType.PLAYER_ENTER_RADIUS)) {
            Set<UUID> inRadius = INSIDE_RADIUS.computeIfAbsent(nullBlockPos, k -> new HashSet<>());
            boolean inside = distSq <= cfg.radius * cfg.radius;
            boolean wasInside = inRadius.contains(player.getUUID());
            if (inside && !wasInside) {
                inRadius.add(player.getUUID());
                NullTriggerRegistry.fire(new NullTriggerContext(NullTriggerType.PLAYER_ENTER_RADIUS, null, level, nullBlockPos, player));
            } else if (!inside && wasInside) {
                inRadius.remove(player.getUUID());
            }
        }

        for (NullTriggerConfig cfg : NullTriggerRegistry.getConfigs(NullTriggerType.PLAYER_NEARBY_TICK)) {
            if (distSq > cfg.radius * cfg.radius) continue;
            if (gameTime % cfg.tickInterval != 0) continue;
            NullTriggerRegistry.fire(new NullTriggerContext(NullTriggerType.PLAYER_NEARBY_TICK, null, level, nullBlockPos, player));
        }
    }
}
