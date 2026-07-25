package com.nullblock.remake.api.trigger;

/** Implemented by external mods to react when a Null Block trigger fires. */
@FunctionalInterface
public interface NullTriggerListener {
    void onTrigger(NullTriggerContext context);
}
