package myshampooisdrunk.incantatium.multiblock.inventory;

import com.ibm.icu.impl.coll.SharedObject;
import com.ibm.icu.util.ICUCloneNotSupportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiblockInventory implements Inventory, RecipeInputProvider {
    private static final Entry EMPTY = new Entry(ItemStack.EMPTY, 0);
    private final int size;
    private final DefaultedList<Entry> heldStacks;

    public MultiblockInventory(int size) {
        this.size = size;
        heldStacks = DefaultedList.ofSize(size, EMPTY);
    }

    public MultiblockInventory(ItemStack... stacks) {
        size = stacks.length;
        Entry[] entries = new Entry[size];
        for (int i = 0; i < stacks.length; i++) {
            entries[i] = new Entry(stacks[i], stacks[i].getCount());
        }
        heldStacks = DefaultedList.copyOf(EMPTY, entries);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return heldStacks.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < this.heldStacks.size() ? this.heldStacks.get(slot).stack : ItemStack.EMPTY;
    }

    public Entry getStackQuantity(int slot) {
        return slot >= 0 && slot < this.heldStacks.size() ? this.heldStacks.get(slot) : EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if(slot < 0 || slot >= this.heldStacks.size()) return ItemStack.EMPTY;
        Entry e = this.heldStacks.get(slot);
        if(amount <= 0 || e.stack == null || e.stack.isEmpty() || amount > e.stack.getMaxCount()) return ItemStack.EMPTY;
        amount = Math.min(amount, e.count);
        heldStacks.set(slot, e.withCount(e.count - amount));
        return e.stack.copyWithCount(amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        if(slot < 0 || slot >= this.heldStacks.size()) return ItemStack.EMPTY;
        Entry e = this.heldStacks.get(slot);
        if(e.stack == null || e.stack.isEmpty()) return ItemStack.EMPTY;
        return removeStack(slot, e.stack.getMaxCount());
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.heldStacks.set(slot, new Entry(stack, stack.getMaxCount()));
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        this.heldStacks.clear();
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        for (Entry e : this.heldStacks) {
            finder.addInput(e.stack);
        }
    }

    public String toString() {
        return ((List)this.heldStacks.stream().filter(stack -> !stack.isEmpty()).toList()).toString();
    }

    public NbtCompound toNbt() {
        NbtCompound compound = new NbtCompound();
        NbtList list = new NbtList();
        for (int i = 0; i < heldStacks.size(); i++) {
//            list.add(i, NbtHelper.);
        }
        return compound;
    }

    public static MultiblockInventory loadFromNbt(NbtCompound compound) {
        compound.getList("items", NbtElement.COMPOUND_TYPE);
        return null;
    }

    public static class Entry implements Cloneable {
        private final ItemStack stack;
        private final int count;

        private Entry(ItemStack stack, int count) {
            this.stack = stack;
            this.count = count;
        }

        public int count(){return count;}

        public ItemStack stack(){return stack;}

        public boolean isEmpty() {
            return count == 0 || stack == null || stack.isEmpty();
        }

        public Entry withCount(int count) {
            return new Entry(stack, count);
        }

        public Entry clone() {
            Entry c;
            try {
                c = (Entry)super.clone();
            } catch (CloneNotSupportedException e) {
                // Should never happen.
                throw new ICUCloneNotSupportedException(e);
            }
            return new Entry(stack, count);
        }
    }
}
