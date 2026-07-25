package com.nullblock.remake.api.trigger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central registry other mods use to subscribe to Null Block triggers.
 * Thread-safety: registration lists are copy-on-write since registration
 * happens rarely (mod init) but iteration happens every tick/interaction.
 */
public final class NullTriggerRegistry {

    private NullTriggerRegistry() {}

    private record Registration(NullTriggerListener listener, NullTriggerConfig config) {}

    private static final Map<NullTriggerType, List<Registration>> LISTENERS = new EnumMap<>(NullTriggerType.class);
    private static final Map<String, List<Registration>> CUSTOM_LISTENERS = new HashMap<>();

    static {
        for (NullTriggerType type : NullTriggerType.values()) {
            LISTENERS.put(type, new CopyOnWriteArrayList<>());
        }
    }

    /** Register a listener for a built-in trigger type. Config may be null for non-radius types. */
    public static void register(NullTriggerType type, NullTriggerListener listener, NullTriggerConfig config) {
        if (type == NullTriggerType.CUSTOM) {
            throw new IllegalArgumentException("Use registerCustom(id, listener) for CUSTOM triggers.");
        }
        LISTENERS.get(type).add(new Registration(listener, config));
    }

    public static void register(NullTriggerType type, NullTriggerListener listener) {
        register(type, listener, null);
    }

    /** Register a listener for a mod-defined custom trigger id (e.g. "backrooms:glitch_zone"). */
    public static void registerCustom(String customId, NullTriggerListener listener) {
        CUSTOM_LISTENERS.computeIfAbsent(customId, k -> new CopyOnWriteArrayList<>())
                .add(new Registration(listener, null));
    }

    public static List<NullTriggerListener> get(NullTriggerType type) {
        List<NullTriggerListener> out = new ArrayList<>();
        for (Registration r : LISTENERS.get(type)) out.add(r.listener());
        return out;
    }

    public static List<NullTriggerListener> getCustom(String customId) {
        List<NullTriggerListener> out = new ArrayList<>();
        for (Registration r : CUSTOM_LISTENERS.getOrDefault(customId, List.of())) out.add(r.listener());
        return out;
    }

    /** Returns configs (radius/tickInterval) for a radius-based trigger type. */
    public static List<NullTriggerConfig> getConfigs(NullTriggerType type) {
        List<NullTriggerConfig> out = new ArrayList<>();
        for (Registration r : LISTENERS.get(type)) {
            if (r.config() != null) out.add(r.config());
        }
        return out;
    }

    /** Dispatches to all listeners registered for the context's trigger type/custom id. */
    public static void fire(NullTriggerContext context) {
        if (context.type() == NullTriggerType.CUSTOM && context.customId() != null) {
            for (NullTriggerListener l : getCustom(context.customId())) l.onTrigger(context);
        } else {
            for (NullTriggerListener l : get(context.type())) l.onTrigger(context);
        }
    }
}
