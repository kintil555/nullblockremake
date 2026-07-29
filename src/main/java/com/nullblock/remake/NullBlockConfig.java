package com.nullblock.remake;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Common config for NullBlock Remake.
 *
 * showInCreativeMenu:
 *   When true (default), the Null Block item shows up in the Building Blocks
 *   creative tab, same as any normal block.
 *
 *   When false, the item is still fully registered (so ItemStacks, saves,
 *   and other mods' references to ModBlocks.NULL_BLOCK / NULL_BLOCK_ITEM keep
 *   working exactly the same), it is just excluded from the creative menu
 *   listing. This is meant for the case where another mod depends on
 *   NullBlockRemakeMod purely as a library — e.g. calling
 *   {@link com.nullblock.remake.api.NullBlockAPI#makePassable} to silently
 *   swap out surface blocks (like grass) for a disguised null block — and
 *   does not want players to be able to obtain/place raw null blocks by hand
 *   from the creative inventory.
 */
public final class NullBlockConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir()
            .resolve(NullBlockRemakeMod.MODID + ".json");

    public static boolean SHOW_IN_CREATIVE_MENU = true;

    private NullBlockConfig() {
    }

    private static final class Data {
        boolean showInCreativeMenu = true;
    }

    public static void load() {
        if (!Files.exists(PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(PATH)) {
            Data data = GSON.fromJson(reader, Data.class);
            if (data != null) {
                SHOW_IN_CREATIVE_MENU = data.showInCreativeMenu;
            }
        } catch (IOException e) {
            NullBlockRemakeMod.LOGGER.error("Failed to load {} config", NullBlockRemakeMod.MODID, e);
        }
    }

    public static void save() {
        Data data = new Data();
        data.showInCreativeMenu = SHOW_IN_CREATIVE_MENU;
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            NullBlockRemakeMod.LOGGER.error("Failed to save {} config", NullBlockRemakeMod.MODID, e);
        }
    }
}
