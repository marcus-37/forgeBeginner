package net.marcus.marcusmod.datagen;

import net.marcus.marcusmod.marcusmod;
import net.marcus.marcusmod.worldgen.modConfiguredFeatures;
import net.marcus.marcusmod.worldgen.worldBiomeModifier;
import net.marcus.marcusmod.worldgen.worldPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, modConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, worldPlacedFeatures::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, worldBiomeModifier::bootstrap);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(marcusmod.MOD_ID));
    }
}
