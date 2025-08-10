package net.marcus.marcusmod.datagen;

import net.marcus.marcusmod.block.modblocks;
import net.marcus.marcusmod.marcusmod;
import net.marcus.marcusmod.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, marcusmod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModTags.Blocks.TEST_ORES).add(modblocks.SINGLE_ORE.get())
                .addTag(Tags.Blocks.ORES);

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(modblocks.SINGLE_ORE.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(modblocks.REACTOR.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(modblocks.REACTOR.get(),modblocks.SINGLE_ORE.get(),modblocks.REACTOR_FENCE_GATE.get(),modblocks.REACTOR_FENCE.get(),
                        modblocks.REACTOR_DOOR.get(),modblocks.REACTOR_WALL.get(),modblocks.REACTOR_SLAB.get(),modblocks.REACTOR_PRESSURE_PLATE.get(),
                        modblocks.REACTOR_TRAPDOOR.get(),modblocks.REACTOR_BUTTON.get(),modblocks.REACTOR_STAIR.get());

        this.tag(BlockTags.FENCES)
                .add(modblocks.REACTOR_FENCE.get());
        this.tag(BlockTags.WALLS)
                .add(modblocks.REACTOR_WALL.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(modblocks.REACTOR_FENCE_GATE.get());

    }
}
