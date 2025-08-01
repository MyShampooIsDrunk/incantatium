package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;

public interface InventorySlotStorage extends PedestalDisplay {
    int getSlot();
    boolean isDirty();
    void markDirty(boolean dirty);
    MultiblockInventory.Singleton getEntry();
    void setEntry(MultiblockInventory.Singleton singleton);
}
