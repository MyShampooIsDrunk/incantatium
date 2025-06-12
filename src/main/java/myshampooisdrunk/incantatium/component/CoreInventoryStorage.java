package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public class CoreInventoryStorage implements InventoryStorage {
    private final MultiblockInventory inventory;
    public CoreInventoryStorage(DisplayEntity.ItemDisplayEntity display) {
        inventory = new MultiblockInventory(8, display.getRegistryManager());
    }

    @Override
    public MultiblockInventory getInventory() {
        return inventory;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.inventory.readNbt(nbtCompound);
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("Inventory",this.inventory.toNbt());
    }
}
