package net.marcus.marcusmod.datagen;

import net.marcus.marcusmod.block.modblocks;
import net.marcus.marcusmod.item.moditems;
import net.marcus.marcusmod.marcusmod;
import net.marcus.marcusmod.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, marcusmod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {

        this.tag(ModTags.Items.REACTABLE)
                .add(moditems.SINGLE.get());
        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(moditems.SINGLE_BOOTS.get(),
                        moditems.SINGLE_CHESTPLATE.get(),
                        moditems.SINGLE_HELMET.get(),
                        moditems.SINGLE_LEGGING.get()
                );


    }
}
