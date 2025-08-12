package net.marcus.marcusmod.recipe;

import net.marcus.marcusmod.marcusmod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Modrecipe {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, marcusmod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<crazyMachineRecipe>> CRAZY_CRAFTING_SERIALIZER =
            SERIALIZER.register("crazy_crafting", () -> crazyMachineRecipe.Serializer.INSTANCE);

    public static void register(IEventBus iEventBus) {
        SERIALIZER.register(iEventBus);
    }
}
