package com.nullblock.remake.block;

import com.nullblock.remake.NullBlockRemakeMod;
import com.nullblock.remake.block.entity.NullBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final BlockEntityType<NullBlockEntity> NULL_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(NullBlockRemakeMod.MODID, "null_block_entity"),
            FabricBlockEntityTypeBuilder.create(NullBlockEntity::new, ModBlocks.NULL_BLOCK).build()
    );

    public static void init() {
        // Triggers static initializer above to run registration.
    }
}
