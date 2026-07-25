package com.nullblock.remake.block;

import com.nullblock.remake.ModEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight registry of currently-loaded Null Block positions per level,
 * used only to scope radius-trigger scanning (avoids scanning whole worlds).
 * Populated/cleared by NullBlockEntity on set-level / removal / chunk unload.
 *
 * In the original mod nothing ever called track()/untrack(), so this
 * registry stayed permanently empty and every radius-based trigger
 * (PLAYER_ENTER_RADIUS, PLAYER_NEARBY_TICK) was silently dead code. This
 * rewrite wires NullBlockEntity#setLevel, #onLoad and #preRemoveSideEffects
 * to actually populate it (see NullBlockEntity).
 */
public final class NullBlockTracker {

    private NullBlockTracker() {}

    private static final Map<ServerLevel, Set<BlockPos>> POSITIONS = new ConcurrentHashMap<>();

    public static void track(ServerLevel level, BlockPos pos) {
        POSITIONS.computeIfAbsent(level, k -> ConcurrentHashMap.newKeySet()).add(pos.immutable());
    }

    public static void untrack(ServerLevel level, BlockPos pos) {
        Set<BlockPos> set = POSITIONS.get(level);
        if (set != null) {
            set.remove(pos);
        }
        ModEvents.clearRadiusState(pos.immutable());
    }

    public static Collection<ServerLevel> trackedLevels() {
        return Collections.unmodifiableSet(POSITIONS.keySet());
    }

    public static Set<BlockPos> positionsIn(ServerLevel level) {
        return POSITIONS.getOrDefault(level, Collections.emptySet());
    }

    /** Removes all tracking state for a level, e.g. when it unloads entirely. */
    public static void untrackLevel(ServerLevel level) {
        POSITIONS.remove(level);
    }
}
