package net.marcus.marcusmod.entity;

import net.marcus.marcusmod.recipe.crazyMachineRecipe;
import net.marcus.marcusmod.screen.crazyMachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class crazyMachineBlockEntity extends BlockEntity  implements MenuProvider {
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(5);

    private static final int IN_PUT1 = 0;
    private static final int IN_PUT2 = 1;
    private static final int IN_PUT3 = 2;
    private static final int IN_PUT4 = 3;
    private static final int OUT_PUT = 4;


    private LazyOptional<IItemHandler> lazyOptional = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyOptional.cast();
        }

        return super.getCapability(cap, side);
    }


    @Override
    public void onLoad() {
        super.onLoad();
        lazyOptional = LazyOptional.of(() -> itemStackHandler);
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
        if(hasRecipe()) {
            Optional<crazyMachineRecipe> recipe = getCurrentRecipe();
            this.maxProgress = recipe.get().getProcessTime();
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);
            if(hasProgressFinished(this.maxProgress)) {
                craftingItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
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

        ItemStack result = recipe.getResultItem(getLevel().registryAccess());

        int[] inputSlots = new int[] { IN_PUT1, IN_PUT2, IN_PUT3, IN_PUT4 };

        Map<Integer, Integer> toExtract = new HashMap<>(); // slot -> amount to extract

        // 为每个 counted ingredient 分配要从哪些槽扣多少
        for (crazyMachineRecipe.CountedIngredient ci : recipe.countedIngredients) {
            int need = ci.count;
            for (int slot : inputSlots) {
                if (need <= 0) break;
                ItemStack stack = this.itemStackHandler.getStackInSlot(slot);
                if (stack.isEmpty()) continue;
                if (!ci.ingredient.test(stack)) continue;

                int alreadyPlanned = toExtract.getOrDefault(slot, 0);
                int avail = stack.getCount() - alreadyPlanned;
                if (avail <= 0) continue;

                int take = Math.min(avail, need);
                toExtract.put(slot, alreadyPlanned + take);
                need -= take;
            }
            if (need > 0) {
                // 理论上不会发生（matches 已经验证过），但加个保护
                return;
            }
        }

        // 输出槽可容纳性检查（和之前相同）
        ItemStack currentOut = this.itemStackHandler.getStackInSlot(OUT_PUT);
        if (!currentOut.isEmpty() && !ItemStack.isSameItemSameTags(currentOut, result)) return;
        int finalCount = currentOut.getCount() + result.getCount();
        int maxStack = Math.min(currentOut.getMaxStackSize(), result.getMaxStackSize());
        if (finalCount > maxStack) return;

        // 执行抽取
        for (Map.Entry<Integer,Integer> e : toExtract.entrySet()) {
            int slot = e.getKey();
            int amount = e.getValue();
            if (amount > 0) {
                this.itemStackHandler.extractItem(slot, amount, false);
            }
        }

        // 放入输出
        this.itemStackHandler.setStackInSlot(OUT_PUT, new ItemStack(result.getItem(), finalCount));
    }

    private boolean hasRecipe() {
        Optional<crazyMachineRecipe> recipe = getCurrentRecipe();

        if(recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());
        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<crazyMachineRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemStackHandler.getSlots());

        for(int i = 0;i < itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemStackHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(crazyMachineRecipe.Type.INSTANCE, inventory, level);
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
}
