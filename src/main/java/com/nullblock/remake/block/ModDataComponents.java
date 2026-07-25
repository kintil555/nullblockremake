package com.nullblock.remake.block;

import com.nullblock.remake.NullBlockRemakeMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Holds the disguise {@link Block} an item-form Null Block should apply when
 * placed (and mirrors the disguise back onto the item when picked/broken).
 * Registry name "block", so `/give @s nullblock_remake:null_block[nullblock_remake:block="minecraft:diamond_ore"]`
 * resolves to it.
 */
public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, NullBlockRemakeMod.MODID);

    public static final RegistryObject<DataComponentType<Block>> DISGUISE_BLOCK =
            DATA_COMPONENTS.register("block", () -> DataComponentType.<Block>builder()
                    .persistent(BuiltInRegistries.BLOCK.byNameCodec())
                    .networkSynchronized(ByteBufCodecs.registry(Registries.BLOCK))
                    .build());
}
