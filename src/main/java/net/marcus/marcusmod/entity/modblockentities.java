package net.marcus.marcusmod.entity;

import net.marcus.marcusmod.block.modblocks;
import net.marcus.marcusmod.marcusmod;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class modblockentities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, marcusmod.MOD_ID);

    public static final RegistryObject<BlockEntityType<crazyMachineBlockEntity>> CRAZY_MACHINE_BE =
            BLOCK_ENTITY.register("crazy_machine_be", () ->
                    BlockEntityType.Builder.of(crazyMachineBlockEntity::new,
                            modblocks.CRAZY_MACHINE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY.register(eventBus);
    }
}
