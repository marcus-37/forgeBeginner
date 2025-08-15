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

    public boolean matches(SimpleContainer container, Level level) {
        // 构造临时可用池（副本）
        List<ItemStack> available = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            available.add(s.isEmpty() ? ItemStack.EMPTY : s.copy());
        }

        // 对每个 CountedIngredient 逐个消耗单位
        for (CountedIngredient ci : countedIngredients) {
            int need = ci.count;
            for (int i = 0; i < available.size() && need > 0; i++) {
                ItemStack stack = available.get(i);
                if (stack.isEmpty()) continue;
                if (!ci.ingredient.test(stack)) continue;
                int take = Math.min(stack.getCount(), need);
                need -= take;
                stack.shrink(take);
            }
            if (need > 0) return false; // 这个 ingredient 无法满足
        }
        return true; // 所有需求满足
    }

    // 返回配方需要的所有物品总数
    public int getTotalRequiredItemCount() {
        return countedIngredients.stream().mapToInt(ci -> ci.count).sum();
    }

    // 返回严格匹配（isSimple=false）原料的数量
    public int getStrictIngredientCount() {
        return (int) countedIngredients.stream()
                .filter(ci -> !ci.ingredient.isSimple())
                .count();
    }

    public int getDistinctIngredientTypeCountSimple() {
        return this.countedIngredients == null ? 0 : this.countedIngredients.size();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (CountedIngredient ci : this.countedIngredients) {
            for (int i = 0; i < ci.count; i++) list.add(ci.ingredient);
        }
        return list;
    }

    public int getTotalIngredientCount() {
        return this.countedIngredients.stream().mapToInt(ci -> ci.count).sum();
    }

    // 返回配方中不同 ingredient 的数量（不考虑重复count）
    public int getDistinctIngredientCount() {
        return this.countedIngredients.size();
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

    public int getPriority() {
        return 0;
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
