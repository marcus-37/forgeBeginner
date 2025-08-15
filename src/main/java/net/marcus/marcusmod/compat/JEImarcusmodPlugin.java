package net.marcus.marcusmod.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.marcus.marcusmod.block.modblocks;
import net.marcus.marcusmod.marcusmod;
import net.marcus.marcusmod.recipe.crazyMachineRecipe;
import net.marcus.marcusmod.screen.crazyMachinescreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEImarcusmodPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CrazyCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<crazyMachineRecipe> crazyRecipes = recipeManager.getAllRecipesFor(crazyMachineRecipe.Type.INSTANCE);

        registration.addRecipes(CrazyCategory.CRAZY_CRAFTING_TYPE, crazyRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(crazyMachinescreen.class, 60, 30, 20, 30,
                CrazyCategory.CRAZY_CRAFTING_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(modblocks.CRAZY_MACHINE.get()),
                CrazyCategory.CRAZY_CRAFTING_TYPE);
    }

}
