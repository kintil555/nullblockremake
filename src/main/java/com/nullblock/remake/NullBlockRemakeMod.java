package com.nullblock.remake;

import com.nullblock.remake.block.ModBlockEntities;
import com.nullblock.remake.block.ModBlocks;
import com.nullblock.remake.block.ModDataComponents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
@Mod(NullBlockRemakeMod.MODID)
public class NullBlockRemakeMod {

    public static final String MODID = "nullblock_remake";
    public static final Logger LOGGER = LoggerFactory.getLogger(NullBlockRemakeMod.class);

    public NullBlockRemakeMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        // Register the common config so showInCreativeMenu is actually loaded
        // from disk. The original mod built this ForgeConfigSpec but never
        // called registerConfig, so the file was never created/read.
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NullBlockConfig.SPEC);

        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS
                && NullBlockConfig.SHOW_IN_CREATIVE_MENU.get()) {
            event.accept(ModBlocks.NULL_BLOCK_ITEM.get());
        }
    }
}
