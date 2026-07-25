package com.nullblock.remake;

import com.nullblock.remake.block.ModBlockEntities;
import com.nullblock.remake.block.ModBlocks;
import com.nullblock.remake.block.ModDataComponents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NullBlock Remake — a passthrough "phantom" block that can visually disguise
 * itself as any other block in the game while remaining fully non-solid: no
 * collision, no interaction blocking, the player walks straight through it.
 *
 * This is a from-scratch, stability-focused rewrite of the original NullBlock
 * mod. It is built as a LIBRARY / API for other mods to depend on. See
 * {@link com.nullblock.remake.api.NullBlockAPI} for the public integration
 * surface. Programmatically placing null blocks during world generation
 * (e.g. to build a "backrooms"-style noclip zone) is just ONE example use
 * case enabled by the API — it is not what this mod does by itself.
 */
public class NullBlockRemakeMod implements ModInitializer {

    public static final String MODID = "nullblock_remake";
    public static final Logger LOGGER = LoggerFactory.getLogger(NullBlockRemakeMod.class);

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModBlockEntities.init();
        ModDataComponents.init();

        NullBlockConfig.load();
        ModEvents.init();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
            if (NullBlockConfig.SHOW_IN_CREATIVE_MENU) {
                content.accept(ModBlocks.NULL_BLOCK_ITEM);
            }
        });
    }
}
