package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import org.ladysnake.cca.api.v3.component.Component;

public interface InventorySlotStorage extends PedestalDisplay {
    int getSlot();
    boolean isDirty();
    void markDirty(boolean dirty);
    MultiblockInventory.Entry getEntry();
    void setEntry(MultiblockInventory.Entry entry);
}
