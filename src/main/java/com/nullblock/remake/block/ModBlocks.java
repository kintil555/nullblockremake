package com.nullblock.remake.block;

import com.nullblock.remake.NullBlockRemakeMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {

    public static final Block NULL_BLOCK = register("null_block");

    public static final Item NULL_BLOCK_ITEM = registerItem("null_block");

    private static Block register(String name) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(NullBlockRemakeMod.MODID, name));
        Block block = new NullBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .noCollission()
                .noOcclusion()
                .strength(0.5f)
                .sound(SoundType.GLASS)
                .isValidSpawn((state, level, pos, entityType) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }

    private static Item registerItem(String name) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(NullBlockRemakeMod.MODID, name));
        Item item = new NullBlockItem(NULL_BLOCK, new Item.Properties().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void init() {
        // Triggers static initializers above to run registration.
    }
}
