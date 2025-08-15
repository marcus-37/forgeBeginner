package net.marcus.marcusmod.entity;

import net.marcus.marcusmod.recipe.crazyMachineRecipe;
import net.marcus.marcusmod.screen.crazyMachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class crazyMachineBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(5){
        // 当输入槽变动时必须调用（你在 item handler 的回调/监听里触发）
        protected void onContentsChanged(int slot) {
            // 仅在输入槽改动才刷新缓存；如果 slot 为输出或其它，可按需过滤
            if (slot == IN_PUT1 || slot == IN_PUT2 || slot == IN_PUT3 || slot == IN_PUT4) {
                updateRecipeCache();
                setChanged();
            }
        }
    };

    private static final int IN_PUT1 = 0;
    private static final int IN_PUT2 = 1;
    private static final int IN_PUT3 = 2;
    private static final int IN_PUT4 = 3;
    private static final int OUT_PUT = 4;

    private static final int[] INPUT_SLOTS = new int[]{IN_PUT1, IN_PUT2, IN_PUT3, IN_PUT4};

    private static final Comparator<crazyMachineRecipe> RECIPE_PRIORITY_COMPARATOR =
            Comparator.<crazyMachineRecipe>comparingInt(r -> -r.getDistinctIngredientTypeCountSimple()) // ≤—— 核心：种类越多越靠前
                    .thenComparingInt(r -> -r.getStrictIngredientCount())    // 次要：严格匹配条件更多的靠前
                    .thenComparingInt(r -> -r.getTotalRequiredItemCount())   // 次次要：总个数更多的靠前
                    .thenComparing(r -> r.getId().toString());


    private LazyOptional<IItemHandler> lazyOptional = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private  List<crazyMachineRecipe> sortedRecipeCache = null;
    private static int cachedRecipeCount = -1;
    private @Nullable ResourceLocation currentRecipeId = null;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyOptional.cast();
        }

        return super.getCapability(cap, side);
    }


    @Override
    public void onLoad() {
        super.onLoad();
        lazyOptional = LazyOptional.of(() -> itemStackHandler);
        updateRecipeCache();
    }


    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyOptional.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, itemStackHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.marcusmod.crazy_machine");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemStackHandler.serializeNBT());
        pTag.putInt("crazy_machine.progress", progress);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemStackHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("crazy_machine.progress");
    }


    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new crazyMachineMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) return;
        if (hasRecipe()) {
            Optional<crazyMachineRecipe> recipe = getCurrentRecipe();
            if (changedRecipe(recipe.map(crazyMachineRecipe::getId).orElse(null))) {
                resetProgress();
                return;
            }
            this.maxProgress = recipe.get().getProcessTime();
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);
            if (hasProgressFinished(this.maxProgress)) {
                craftingItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private boolean changedRecipe(ResourceLocation newRecipeId) {
        if (!Objects.equals(this.currentRecipeId, newRecipeId)) {
            this.currentRecipeId = newRecipeId;
            return true;
        }
        return false;
    }


    public crazyMachineBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(modblockentities.CRAZY_MACHINE_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> crazyMachineBlockEntity.this.progress;
                    case 1 -> crazyMachineBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> crazyMachineBlockEntity.this.progress = pValue;
                    case 1 -> crazyMachineBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftingItem() {
        Optional<crazyMachineRecipe> opt = getCurrentRecipe();
        if (opt.isEmpty()) return;
        crazyMachineRecipe recipe = opt.get();

        ItemStack result = recipe.getResultItem(this.level.registryAccess());

        int[] inputSlots = new int[]{IN_PUT1, IN_PUT2, IN_PUT3, IN_PUT4};

        // 输出槽容纳性检查（保留 NBT，且计算最大堆大小）
        ItemStack currentOut = this.itemStackHandler.getStackInSlot(OUT_PUT);
        if (!currentOut.isEmpty() && !ItemStack.isSameItemSameTags(currentOut, result)) return;
        int finalCount = (currentOut.isEmpty() ? 0 : currentOut.getCount()) + result.getCount();
        int maxStack = Math.min((currentOut.isEmpty() ? result.getMaxStackSize() : currentOut.getMaxStackSize()), result.getMaxStackSize());
        if (finalCount > maxStack) return;

        // 构造临时可用池（和 matches 一样的副本）
        List<ItemStack> availableStacks = new ArrayList<>();
        for (int slot : inputSlots) {
            ItemStack stack = this.itemStackHandler.getStackInSlot(slot);
            availableStacks.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }

        // 记录要扣除的数量（按真实槽索引）
        Map<Integer, Integer> toExtract = new HashMap<>();

        // 与 matches 相同的消耗顺序：逐个 CountedIngredient 消耗单位
        for (crazyMachineRecipe.CountedIngredient ci : recipe.countedIngredients) {
            int need = ci.count;
            for (int i = 0; i < inputSlots.length && need > 0; i++) {
                int slotIndex = inputSlots[i];
                ItemStack stack = availableStacks.get(i);
                if (stack.isEmpty()) continue;
                if (!ci.ingredient.test(stack)) continue;

                int provide = Math.min(stack.getCount(), need);
                need -= provide;

                // 模拟扣除
                stack.shrink(provide);
                availableStacks.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack);

                // 记录实际要从真实容器扣除的数量
                toExtract.put(slotIndex, toExtract.getOrDefault(slotIndex, 0) + provide);
            }

            if (need > 0) {
                // 理论上 matches() 已经保证不会走到这里；保险起见直接退出
                return;
            }
        }

        // 真正从 itemStackHandler 扣除
        for (Map.Entry<Integer, Integer> e : toExtract.entrySet()) {
            int slot = e.getKey();
            int amt = e.getValue();
            if (amt > 0) {
                this.itemStackHandler.extractItem(slot, amt, false);
            }
        }

        // 放入输出（保留 result 的 NBT）
        if (currentOut.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUT_PUT, result.copy());
        } else {
            ItemStack outCopy = currentOut.copy();
            outCopy.grow(result.getCount());
            this.itemStackHandler.setStackInSlot(OUT_PUT, outCopy);
        }
    }


    private boolean hasRecipe() {
        Optional<crazyMachineRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().getResultItem(this.level.registryAccess());
        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }


    // 3. 匹配方法
    public Optional<crazyMachineRecipe> getCurrentRecipe() {
        if (this.level == null || sortedRecipeCache == null || sortedRecipeCache.isEmpty()) {
            return Optional.empty();
        }

        SimpleContainer check = makeCheckContainerFromInputs();
        for (crazyMachineRecipe recipe : sortedRecipeCache) {
            if (recipe.matches(check, this.level)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }


    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemStackHandler.getStackInSlot(OUT_PUT).isEmpty() || this.itemStackHandler.getStackInSlot(OUT_PUT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemStackHandler.getStackInSlot(OUT_PUT).getCount() + count <= this.itemStackHandler.getStackInSlot(OUT_PUT).getMaxStackSize();
    }

    private boolean hasProgressFinished(int maxProgressTime) {
        return progress >= maxProgressTime;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private void updateRecipeCache() {
        if (this.level == null) return;
        List<crazyMachineRecipe> allRecipes = this.level.getRecipeManager()
                .getAllRecipesFor(crazyMachineRecipe.Type.INSTANCE);

        // 可选轻量筛选（mightMatch）可以减少后面 matches 的负担
        SimpleContainer check = makeCheckContainerFromInputs();
        List<crazyMachineRecipe> filtered = new ArrayList<>();
        for (crazyMachineRecipe r : allRecipes) {
            if (mightMatch(r, check)) filtered.add(r);
        }

        // 排序并缓存
        filtered.sort(RECIPE_PRIORITY_COMPARATOR);
        this.sortedRecipeCache = Collections.unmodifiableList(filtered);
    }
    // 轻量级预检查：只检测类型是否都有对应槽（不验证数量）
    private boolean mightMatch(crazyMachineRecipe r, SimpleContainer check) {
        for (crazyMachineRecipe.CountedIngredient ci : r.countedIngredients) {
            boolean found = false;
            for (int i = 0; i < check.getContainerSize(); i++) {
                if (ci.ingredient.test(check.getItem(i))) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    // ---------- 构造用于检查的容器 ----------
    private SimpleContainer makeCheckContainerFromInputs() {
        SimpleContainer container = new SimpleContainer(4);
        ItemStack s1 = this.itemStackHandler.getStackInSlot(IN_PUT1);
        ItemStack s2 = this.itemStackHandler.getStackInSlot(IN_PUT2);
        ItemStack s3 = this.itemStackHandler.getStackInSlot(IN_PUT3);
        ItemStack s4 = this.itemStackHandler.getStackInSlot(IN_PUT4);
        container.setItem(0, s1.isEmpty() ? ItemStack.EMPTY : s1.copy());
        container.setItem(1, s2.isEmpty() ? ItemStack.EMPTY : s2.copy());
        container.setItem(2, s3.isEmpty() ? ItemStack.EMPTY : s3.copy());
        container.setItem(3, s4.isEmpty() ? ItemStack.EMPTY : s4.copy());
        return container;
    }
}
