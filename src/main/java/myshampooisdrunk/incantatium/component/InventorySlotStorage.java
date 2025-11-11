package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntity;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.decoration.DisplayEntity;

public interface InventorySlotStorage extends PedestalDisplay<DisplayEntity.ItemDisplayEntity, PedestalEntity.PedestalItemEntity> {
    int getSlot();
    boolean isDirty();
    void markDirty(boolean dirty);
    MultiblockInventory.Singleton getEntry();
    void setEntry(MultiblockInventory.Singleton singleton);
}
