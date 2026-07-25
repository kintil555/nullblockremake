package com.nullblock.remake.block;

import com.nullblock.remake.NullBlockRemakeMod;
import com.nullblock.remake.block.entity.NullBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NullBlockRemakeMod.MODID);

    public static final RegistryObject<BlockEntityType<NullBlockEntity>> NULL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("null_block_entity", () -> new BlockEntityType<>(
                    NullBlockEntity::new, Set.of(ModBlocks.NULL_BLOCK.get())
            ));
}
