package myshampooisdrunk.incantatium.multiblock.inventory;

import com.ibm.icu.util.ICUCloneNotSupportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class MultiblockInventory implements Inventory {
    public static final Entry EMPTY = new Entry(ItemStack.EMPTY, 0);
    private final int size;
    private final DefaultedList<Entry> heldStacks;
    private final DynamicRegistryManager registryManager;

    public MultiblockInventory(int size, DynamicRegistryManager registryManager) {
        this.size = size;
        heldStacks = DefaultedList.ofSize(size, EMPTY);
        this.registryManager = registryManager;
    }

    public MultiblockInventory(DynamicRegistryManager registryManager, ItemStack... stacks) {
        size = stacks.length;
        Entry[] entries = new Entry[size];
        for (int i = 0; i < stacks.length; i++) {
            entries[i] = new Entry(stacks[i], stacks[i].getCount());
        }
        heldStacks = DefaultedList.copyOf(EMPTY, entries);
        this.registryManager = registryManager;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return heldStacks.isEmpty();
    }

    public Entry get(int slot) {
        return slot >= 0 && slot < this.heldStacks.size() ? this.heldStacks.get(slot) : EMPTY;
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
        if(slot < 0 || slot >= this.heldStacks.size()) return;
        this.heldStacks.set(slot, new Entry(stack, stack.getCount()));
    }

    public List<ItemStack> toStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        heldStacks.forEach(e -> {
            if(e.isEmpty()) return;
            int max = e.stack.getMaxCount(), count = e.count();
            while (count > 0) {
                // count = big number (count >= max) --> add max; subtract count
                // count = small nunmber (0 < count < max --> add count
                // count = bad number (count <= 0) --> stop
                if (count > max) stacks.add(e.stack.copyWithCount(max));
                else stacks.add(e.stack.copyWithCount(count));
                count -= max;
            }
        });
        return stacks;
    }

    public boolean addStack(int slot, ItemStack stack) {
        if(slot < 0 || slot >= this.heldStacks.size()) return false;
        Entry e;
        if(ItemStack.areItemsAndComponentsEqual((e = heldStacks.get(slot)).stack, stack)) {
            this.heldStacks.set(slot, new Entry(stack, e.count + stack.getCount()));
            return true;
        }
        return false;
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

    public String toString() {
        return this.heldStacks.stream().filter(stack -> !stack.isEmpty()).toList().toString();
    }

    public NbtList toNbt() {
        NbtList list = new NbtList();
        for (int i = 0; i < heldStacks.size(); i++) {
            NbtCompound c = new NbtCompound();
            c.putByte("Slot", (byte)i);
            Entry e = heldStacks.get(i);
            c.putInt("Count", e.count);
            list.add(e.stack.copyWithCount(1).toNbt(registryManager, c));
        }
//        NbtCompound ret = new NbtCompound();
//        ret.put("Inventory", list);
        return list;
    }

    public void readNbt(NbtCompound compound) {
        this.heldStacks.clear();
        NbtList list = compound.getList("Inventory", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound nbtCompound = list.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromNbt(registryManager, nbtCompound).orElse(ItemStack.EMPTY);
            if (j < this.heldStacks.size()) {
                int count = nbtCompound.getInt("Count");
                Entry e = new Entry(itemStack, count);
                this.heldStacks.set(j, e);
            }
        }
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

        public static Entry fromNbt(NbtCompound compound, RegistryWrapper.WrapperLookup registries) {
            if(!compound.contains("Count")) return EMPTY;
            ItemStack stack = ItemStack.fromNbt(registries, compound).orElse(ItemStack.EMPTY);
            int count = compound.getInt("Count");
            if(stack.isEmpty() || count <= 0) return EMPTY;
            return new Entry(stack,count);
        }
    }
}
