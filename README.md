# Null Block Remake

A from-scratch, stability-focused rewrite of the original [NullBlock](https://github.com/kintil555/nullblockmod) mod for Minecraft Forge.

Null Block is a passthrough "phantom" block: it can visually disguise itself as any other block in the game while remaining fully non-solid (no collision, no interaction blocking — the player walks straight through it). It is built primarily as a **library/API** for other mods to depend on.

## Requirements

- Minecraft 1.21.4
- Forge 54.1.16+
- Java 21

## Features

- Fully passable block with configurable visual disguise (right-click with any block item to apply)
- Persists disguise across save/load
- Stable, non-leaking event tracking for radius-based API triggers
- `NullBlockAPI` public integration surface for other mods:
  - `placeNullBlock` / `makePassable` — programmatic placement
  - `getDisguiseState` / `setDisguiseState` — query/update disguise
  - `NullTriggerRegistry` — subscribe to `ENTITY_COLLISION`, `PLAYER_ENTER_RADIUS`, `PLAYER_NEARBY_TICK`, `BLOCK_INTERACTION`, and custom triggers
- Client-side camera darkening when the player's eyes are inside a solid-looking disguise (matches vanilla in-block suffocation visuals)
- `showInCreativeMenu` config option, for mods that want to use Null Block purely as an internal building block

## What changed from the original mod

The original `nullblockmod` had several dead/broken subsystems that this rewrite fixes:

- Config was built but never registered (`showInCreativeMenu` was never actually loaded from disk)
- `NullBlockTracker` was never populated (`track()`/`untrack()` were never called), so `PLAYER_ENTER_RADIUS` and `PLAYER_NEARBY_TICK` triggers never fired
- `BLOCK_INTERACTION` trigger type existed but was never fired anywhere
- Radius-trigger "inside" state was tracked in an unbounded, never-cleared set

## Using as a dependency (other mods)

Via [JitPack](https://jitpack.io) — add to your `build.gradle`:

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
dependencies {
    modImplementation "com.github.kintil555:nullblockremake:fabric-port-SNAPSHOT"
}
```

Then use `com.nullblock.remake.api.NullBlockAPI` and `NullTriggerRegistry` freely — see Features above.

## Building

```bash
./gradlew build
```

The compiled jar is written to `build/libs/`.
