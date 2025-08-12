package net.marcus.marcusmod.datagen.loot;

import net.marcus.marcusmod.block.modblocks;
import net.marcus.marcusmod.item.moditems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(modblocks.REACTOR.get());
        this.dropSelf(modblocks.REACTOR_FENCE.get());
        this.dropSelf(modblocks.REACTOR_BUTTON.get());
        this.dropSelf(modblocks.REACTOR_TRAPDOOR.get());
        this.dropSelf(modblocks.REACTOR_FENCE_GATE.get());
        this.dropSelf(modblocks.REACTOR_PRESSURE_PLATE.get());
        this.dropSelf(modblocks.REACTOR_STAIR.get());
        this.dropSelf(modblocks.REACTOR_WALL.get());
        this.dropSelf(modblocks.CRAZY_MACHINE.get());

        this.add(modblocks.REACTOR_DOOR.get(),
                block -> createDoorTable(modblocks.REACTOR_DOOR.get()));
        this.add(modblocks.REACTOR_SLAB.get(),
                block -> createSlabItemTable(modblocks.REACTOR_SLAB.get()));

        this.add(modblocks.SINGLE_ORE.get(),
                block -> createCopperLikeOreDrops(modblocks.SINGLE_ORE.get(), moditems.SINGLE.get()));
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock,Item item) {
        return createSilkTouchDispatchTable(pBlock, this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    protected @NotNull Iterable<Block> getKnownBlocks() {
        return modblocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
