package com.nullblock.remake.block;

import com.nullblock.remake.NullBlockRemakeMod;
import com.nullblock.remake.block.entity.NullBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final BlockEntityType<NullBlockEntity> NULL_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(NullBlockRemakeMod.MODID, "null_block_entity"),
            BlockEntityType.Builder.of(NullBlockEntity::new, ModBlocks.NULL_BLOCK).build(null)
    );

    public static void init() {
        // Triggers static initializer above to run registration.
    }
}
