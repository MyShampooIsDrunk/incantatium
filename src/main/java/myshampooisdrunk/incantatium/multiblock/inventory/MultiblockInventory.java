package myshampooisdrunk.incantatium.multiblock.inventory;

import myshampooisdrunk.incantatium.multiblock.recipe.MultiblockRecipeInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultiblockInventory implements MultiblockRecipeInput {
    public static final Singleton EMPTY = new Singleton(ItemStack.EMPTY, 0);
    private final int size;
    private final DefaultedList<Singleton> heldStacks;

    public MultiblockInventory(int size) {
        this.size = size;
        heldStacks = DefaultedList.ofSize(size, EMPTY);
    }

    @Override
    public Entry getEntryInSlot(int slot) {
        return get(slot);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if(slot >= 0 && slot < this.heldStacks.size()) return ItemStack.EMPTY;
        Entry e = get(slot);
        ItemStack temp = e.stack().copyWithCount(1);
        NbtCompound custom = new NbtCompound();
        if(temp.contains(DataComponentTypes.CUSTOM_DATA)) custom = Objects.requireNonNull(temp.get(DataComponentTypes.CUSTOM_DATA)).copyNbt();
        custom.putInt("singletonItemCount", e.count());
        temp.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(custom));
        return temp;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (Singleton e : this.heldStacks) {
            if (!e.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Singleton get(int slot) {
        return slot >= 0 && slot < this.heldStacks.size() ? this.heldStacks.get(slot) : EMPTY;
    }

    public ItemStack getStack(int slot) {
        return getStackInSlot(slot);
    }

    public void set(Singleton singleton, int slot) {
        this.heldStacks.set(slot, singleton);
    }

    public Singleton getStackQuantity(int slot) {
        return slot >= 0 && slot < this.heldStacks.size() ? this.heldStacks.get(slot) : EMPTY;
    }

    public ItemStack removeStack(int slot, int amount) {
//        System.out.println("I am " + this + " and im trying to remove " + amount);
        if(slot < 0 || slot >= this.heldStacks.size()) return ItemStack.EMPTY;
        Singleton e = this.heldStacks.get(slot);
        if(amount <= 0 || e.stack == null || e.stack.isEmpty() || amount > e.stack.getMaxCount()) return ItemStack.EMPTY;
        amount = Math.min(amount, e.count);
//        if (e.count == amount) {
//            heldStacks.set(slot, EMPTY);
//            return e.stack.copyWithCount(e.count);
//        }
        heldStacks.set(slot, e.withCount(e.count - amount));
        return e.stack.copyWithCount(amount);
    }

    public ItemStack removeStack(int slot) {
        if(slot < 0 || slot >= this.heldStacks.size()) return ItemStack.EMPTY;
        Singleton e = this.heldStacks.get(slot);
        if(e.stack == null || e.stack.isEmpty()) return ItemStack.EMPTY;
        return removeStack(slot, e.stack.getMaxCount());
    }

    public void setStack(int slot, ItemStack stack) {
        if(slot < 0 || slot >= this.heldStacks.size()) return;
        this.heldStacks.set(slot, new Singleton(stack, stack.getCount()));
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
//        System.out.println("tried adding stack: " + slot + ", " + stack);
        if(slot < 0 || slot >= this.heldStacks.size() || stack.isEmpty()) return false;
        Singleton e;
        if((e = this.heldStacks.get(slot)).isEmpty()) {
            this.heldStacks.set(slot, new Singleton(stack));
            return true;
        }
        if(ItemStack.areItemsAndComponentsEqual(e.stack, stack)) {
            this.heldStacks.set(slot, new Singleton(e.stack, e.count + stack.getCount()));
            return true;
        }
        return false;
    }

    public void markDirty() {
    }

    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    public void clear() {
        this.heldStacks.clear();
    }

    public String toString() {
        return this.heldStacks.stream().filter(stack -> !stack.isEmpty()).toList().toString();
    }

    public WriteView.ListView writeData(WriteView writeView) {
        WriteView.ListView list = writeView.getList("Inventory");
        for (int i = 0; i < heldStacks.size(); i++) {
            WriteView slot = list.add();
            slot.putByte("Slot", (byte) i);
            Singleton e = heldStacks.get(i);
            slot.putInt("Count", e.count);
            if(!e.isEmpty()) {
                e.writeData(writeView);
            }
        }
        return list;
    }

    public void readData(ReadView readView) {
        this.heldStacks.clear();
        ReadView.ListReadView list = readView.getListReadView("Inventory");
        for (ReadView view : list) {
            int j = readView.getByte("Slot", (byte) -1) & 255;
            if (j < this.heldStacks.size()) {
                this.heldStacks.set(j, Singleton.parseData(readView));
            }
        }
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        for (int i = 0; i < size; i++) {
            finder.addInput(getStackInSlot(i));
        }
    }

    public static abstract class Entry implements Cloneable {
        public abstract int count();
        public abstract ItemStack stack();
        public abstract boolean isEmpty();
        public abstract Entry withCount(int count);
        public abstract Entry clone();
        public abstract void writeData(WriteView writeView);
        public abstract Entry readData(ReadView readView);
//        public abstract void toNbt(NbtCompound compound, RegistryWrapper.WrapperLookup registries);
    }

    public static class Stack extends Entry {
        private final ItemStack stack;
        private Stack(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int count() {
            return stack.getCount();
        }

        @Override
        public ItemStack stack() {
            return stack;
        }

        @Override
        public boolean isEmpty() {
            return stack.isEmpty();
        }

        @Override
        public Stack withCount(int count) {
            return new Stack(stack.copyWithCount(count));
        }

        @Override
        public Stack clone() {
            return new Stack(stack);
        }

        public static Stack create(ItemStack stack) {
            return new Stack(stack);
        }

        @Override
        public void writeData(WriteView writeView) {
            if(this.isEmpty()) return;
            writeView.put("Item", ItemStack.CODEC, stack);
        }

        @Override
        public Stack readData(ReadView readView) {
            return new Stack(readView.read("Item",ItemStack.CODEC).orElse(ItemStack.EMPTY));
        }
    }

    public static class Capped extends Entry {
        public static final Capped EMPTY = new Capped(ItemStack.EMPTY,0,0);
        private final ItemStack stack;
        private final int count;
        private final int maxCount;

        private Capped(ItemStack stack, int count, int maxCount) {
            this.stack = stack;
            this.count = count;
            this.maxCount = maxCount;
        }

        private Capped(ItemStack stack, int maxCount) {
            this(stack, stack.getCount(), maxCount);
        }

        private Capped(ItemStack stack) {
            this(stack, stack.getCount(), stack.getMaxCount());
        }

        public int count(){return count;}

        public int getMaxCount(){return maxCount;}

        public ItemStack stack(){return stack;}

        public boolean isEmpty() {
            return count == 0 || stack == null || stack.isEmpty();
        }

        public Capped withCount(int count) {
            if(count == 0) return EMPTY;
            return new Capped(stack, count);
        }

        public Capped clone() {
            return new Capped(stack, count, maxCount);
        }

        public static Capped create(ItemStack stack, int count, int maxCount) {
            if(stack.isEmpty() || count <= 0) return EMPTY;
            else return new Capped(stack, count, maxCount);
        }

        @Override
        public String toString() {
            if(this.isEmpty()) return "EMPTY";
            return "Item: " + this.stack.getItem() + " | count: " + this.count;
        }

        @Override
        public void writeData(WriteView writeView) {
            if(this.isEmpty()) return;
            writeView.putInt("Count", count);
            writeView.putInt("MaxCount", maxCount);
            writeView.put("Item", ItemStack.CODEC, stack);
        }

        @Override
        public Capped readData(ReadView readView) {
            return new Capped(readView.read("Item",ItemStack.CODEC).orElse(ItemStack.EMPTY),
                    readView.getInt("Count",0),
                    readView.getInt("MaxCount",0));
        }
    }

    public static class Singleton extends Entry {
        private final ItemStack stack;
        private final int count;

        private Singleton(ItemStack stack, int count) {
            this.stack = stack;
            this.count = count;
        }

        private Singleton(ItemStack stack) {
            this(stack, stack.getCount());
        }

        public int count(){return count;}

        public ItemStack stack(){return stack;}

        public boolean isEmpty() {
            return count == 0 || stack == null || stack.isEmpty();
        }

        public Singleton withCount(int count) {
            if(count == 0) return EMPTY;
            return new Singleton(stack, count);
        }

        public Singleton clone() {
            return new Singleton(stack, count);
        }

        public static Singleton create(ItemStack stack, int count) {
            if(stack.isEmpty() || count <= 0) return EMPTY;
            else return new Singleton(stack, count);
        }

        @Override
        public String toString() {
            if(this.isEmpty()) return "EMPTY";
            return "Item: " + this.stack.getItem() + " | count: " + this.count + " | components: " + stack.getComponentChanges();
        }

        public static Singleton parseData(ReadView readView) {
            return EMPTY.readData(readView);
        }

        public Singleton readData(ReadView readView) {
            int count = readView.getInt("Count", 0);
            if(count == 0) return EMPTY;
            ItemStack stack = readView.read("Item",ItemStack.CODEC).orElse(ItemStack.EMPTY);
            if(stack.isEmpty() || count <= 0) return EMPTY;
            return new Singleton(stack,count);
        }

        @Override
        public void writeData(WriteView writeView) {
            if(this.isEmpty()) return;
            writeView.putInt("Count", count);
            writeView.put("Item",ItemStack.CODEC, stack);
        }
    }
}
