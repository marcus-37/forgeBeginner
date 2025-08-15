package net.marcus.marcusmod.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.marcus.marcusmod.block.modblocks;
import net.marcus.marcusmod.marcusmod;
import net.marcus.marcusmod.recipe.crazyMachineRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CrazyCategory implements IRecipeCategory<crazyMachineRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "crazy_crafting");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID,
            "textures/gui/crazy.png");

    public static final RecipeType<crazyMachineRecipe> CRAZY_CRAFTING_TYPE =
            new RecipeType<>(UID, crazyMachineRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CrazyCategory(IGuiHelper helper) {
        this.background = helper.drawableBuilder(TEXTURE, 0, 0, 172, 82).setTextureSize(176, 205).build();
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(modblocks.CRAZY_MACHINE.get()));
    }

    @Override
    public RecipeType<crazyMachineRecipe> getRecipeType() {
        return CRAZY_CRAFTING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.marcusmod.crazy_machine");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, crazyMachineRecipe crazyMachineRecipe, IFocusGroup iFocusGroup) {
        // 你要显示的槽位位置，可以按行列排好
        int[][] slotPositions = {
                {25, 24},
                {25, 49},
                {136, 23},
                {136, 49}
        };

        List<crazyMachineRecipe.CountedIngredient> inputs = crazyMachineRecipe.countedIngredients;
        for (int i = 0; i < inputs.size(); i++) {
            crazyMachineRecipe.CountedIngredient ci = inputs.get(i);

            // 将 Ingredient 转成带数量的所有可能物品
            List<ItemStack> stacksWithCount = Arrays.stream(ci.ingredient.getItems())
                    .map(stack -> {
                        ItemStack s = stack.copy();
                        s.setCount(ci.count); // 设置数量
                        return s;
                    })
                    .toList();

            int x = slotPositions[i][0];
            int y = slotPositions[i][1];

            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addItemStacks(stacksWithCount);
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 80, 36).addItemStack(crazyMachineRecipe.getResultItem(null));
    }
}
