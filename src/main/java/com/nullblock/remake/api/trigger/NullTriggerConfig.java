package com.nullblock.remake.api.trigger;

/** Config for radius-based registrations (PLAYER_ENTER_RADIUS / PLAYER_NEARBY_TICK). */
public final class NullTriggerConfig {
    public final double radius;
    public final int tickInterval; // only used for PLAYER_NEARBY_TICK; ticks between re-fires

    public NullTriggerConfig(double radius) {
        this(radius, 20);
    }

    public NullTriggerConfig(double radius, int tickInterval) {
        this.radius = radius;
        this.tickInterval = Math.max(1, tickInterval);
    }
}
