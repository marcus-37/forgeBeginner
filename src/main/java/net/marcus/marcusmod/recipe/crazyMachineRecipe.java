package net.marcus.marcusmod.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.marcus.marcusmod.marcusmod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class crazyMachineRecipe implements Recipe<SimpleContainer> {

    //private final NonNullList<Ingredient> inputItem;
    private final ItemStack output;
    private final ResourceLocation id;
    public final List<CountedIngredient> countedIngredients; // 替代或并行保存原来的 inputItem
    private final int processTime; // in ticks

    public crazyMachineRecipe(NonNullList<Ingredient> inputItem, ItemStack output, ResourceLocation id) {
        //this.inputItem = inputItem;
        this.output = output;
        this.id = id;
        this.countedIngredients = new ArrayList<>();
        for (Ingredient ing : inputItem) {
            this.countedIngredients.add(new CountedIngredient(ing, 1));
        }
        this.processTime = 100;
    }

    public crazyMachineRecipe(List<CountedIngredient> countedIngredients, ItemStack result, ResourceLocation id, int processTime) {
        this.countedIngredients = new ArrayList<>(countedIngredients);
        this.output = result;
        this.id = id;
        this.processTime = Math.max(1, processTime);
    }

    public crazyMachineRecipe(List<CountedIngredient> countedIngredients, ItemStack output, ResourceLocation id) {
        this(countedIngredients, output, id, 100);
    }

    public boolean matches(@NotNull SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) return false;

        // 先判断是否所有 countedIngredient 的 ingredient 都是 simple（可选缓存）
        boolean isSimple = this.countedIngredients.stream().allMatch(ci -> ci.ingredient.isSimple());

        // 遍历容器，统计每个槽的堆叠（按真实 count）
        // 我们直接对每个 CountedIngredient 计算总可用
        for (crazyMachineRecipe.CountedIngredient ci : this.countedIngredients) {
            int required = ci.count;
            int available = 0;
            for (int j = 0; j < pContainer.getContainerSize(); ++j) {
                ItemStack stack = pContainer.getItem(j);
                if (stack.isEmpty()) continue;
                if (ci.ingredient.test(stack)) {
                    // 使用 stack.getCount() 累加（允许一个槽提供多个单位）
                    available += stack.getCount();
                    if (available >= required) break;
                }
            }
            if (available < required) return false;
        }
        // 全部满足
        return true;
    }


    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    public int getProcessTime() {
        return this.processTime;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<crazyMachineRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "crazy_crafting";
    }

    public static class CountedIngredient {
        public final Ingredient ingredient;
        public final int count;

        public CountedIngredient(Ingredient ingredient, int count) {
            this.ingredient = ingredient;
            this.count = Math.max(1, count);
        }
    }



    public static class Serializer implements RecipeSerializer<crazyMachineRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "crazy_crafting");

        public @NotNull crazyMachineRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");

            List<crazyMachineRecipe.CountedIngredient> counted = new ArrayList<>();
            for (int i = 0; i < ingredients.size(); i++) {
                JsonElement el = ingredients.get(i);
                Ingredient ing = Ingredient.fromJson(el);
                int count = 1;
                if (el.isJsonObject()) {
                    JsonObject obj = el.getAsJsonObject();
                    if (obj.has("count")) {
                        count = GsonHelper.getAsInt(obj, "count", 1);
                    }
                }
                counted.add(new crazyMachineRecipe.CountedIngredient(ing, count));
            }
            int processTime = GsonHelper.getAsInt(pSerializedRecipe, "process_time", 100); // default 100 ticks
            return new crazyMachineRecipe(counted, output, pRecipeId, processTime);
        }

        @Override
        public @Nullable crazyMachineRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int size = pBuffer.readInt();
            List<CountedIngredient> counted = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                Ingredient ing = Ingredient.fromNetwork(pBuffer);
                int count = pBuffer.readVarInt(); // 读取 count
                counted.add(new CountedIngredient(ing, count));
            }

            ItemStack output = pBuffer.readItem();
            int processTime = pBuffer.readVarInt();
            return new crazyMachineRecipe(counted, output, pRecipeId, processTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, crazyMachineRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.countedIngredients.size());

            for (CountedIngredient ci : pRecipe.countedIngredients) {
                ci.ingredient.toNetwork(pBuffer);
                pBuffer.writeVarInt(ci.count); // 写入 count
            }

            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
            pBuffer.writeVarInt(pRecipe.getProcessTime());
        }
    }
}
