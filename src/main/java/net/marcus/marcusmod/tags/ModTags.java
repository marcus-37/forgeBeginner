package net.marcus.marcusmod.tags;

import net.marcus.marcusmod.marcusmod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> TEST_ORES = tag("test_ores");


        @Contract("_ -> new")
        private static @NotNull TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> REACTABLE = tag("reactable");

        @Contract("_ -> new")
        private static @NotNull TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, name));
        }
    }



}
