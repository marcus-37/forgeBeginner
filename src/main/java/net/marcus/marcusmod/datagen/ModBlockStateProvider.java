package net.marcus.marcusmod.datagen;

import net.marcus.marcusmod.block.modblocks;
import net.marcus.marcusmod.marcusmod;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, marcusmod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        BlockWithItem(modblocks.REACTOR);
        BlockWithItem(modblocks.SINGLE_ORE);


        stairsBlock(((StairBlock) modblocks.REACTOR_STAIR.get()), blockTexture(modblocks.REACTOR.get()));
        slabBlock(((SlabBlock) modblocks.REACTOR_SLAB.get()), blockTexture(modblocks.REACTOR.get()), blockTexture(modblocks.REACTOR.get()));
        fenceBlock(((FenceBlock) modblocks.REACTOR_FENCE.get()), blockTexture(modblocks.REACTOR.get()));
        buttonBlock(((ButtonBlock) modblocks.REACTOR_BUTTON.get()), blockTexture(modblocks.REACTOR.get()));
        fenceGateBlock(((FenceGateBlock) modblocks.REACTOR_FENCE_GATE.get()), blockTexture(modblocks.REACTOR.get()));
        pressurePlateBlock(((PressurePlateBlock) modblocks.REACTOR_PRESSURE_PLATE.get()), blockTexture(modblocks.REACTOR.get()));
        wallBlock(((WallBlock) modblocks.REACTOR_WALL.get()), blockTexture(modblocks.REACTOR.get()));
        doorBlock(((DoorBlock) modblocks.REACTOR_DOOR.get()), "cutout", modLoc("block/reactor_door_bottom"),  modLoc("block/reactor_door_up"));
        trapdoorBlock(((TrapDoorBlock) modblocks.REACTOR_TRAPDOOR.get()), "cutout", modLoc("block/reactor_trapdoor"), true);

        simpleBlockWithItem(modblocks.CRAZY_MACHINE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/crazy_machine")));

    }

    private void BlockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
