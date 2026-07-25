package com.nullblock.remake.api.trigger;

/** Built-in trigger categories. Use CUSTOM + a String id for mod-specific triggers. */
public enum NullTriggerType {
    ENTITY_COLLISION,
    PLAYER_ENTER_RADIUS,
    PLAYER_NEARBY_TICK,
    BLOCK_INTERACTION,
    CUSTOM
}
