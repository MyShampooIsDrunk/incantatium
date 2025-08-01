package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.List;

public class PedestalTextDisplay implements PedestalDisplay {
    private final DisplayEntity.TextDisplayEntity entity;
    private DisplayEntity.ItemDisplayEntity itemDisplay;
    private int slot;

    public PedestalTextDisplay(DisplayEntity.TextDisplayEntity entity) {
        this.entity = entity;
        this.slot = -1;
    }

    public PedestalTextDisplay withItemDisplay(DisplayEntity.ItemDisplayEntity itemDisplay) {
        this.itemDisplay = itemDisplay;
        return this;
    }

    @Override
    public void update() {
        if(entity.getWorld() instanceof ServerWorld s) {
            DisplayEntity.ItemDisplayEntity itemDisp = null;
            if(this.itemDisplay != null) itemDisp = this.itemDisplay;
            else {
                List<DisplayEntity.ItemDisplayEntity> disps = s.getEntitiesByType(EntityType.ITEM_DISPLAY, Box.of(entity.getPos(), 2, 2,2),
                        e -> {
                            InventorySlotStorage st = e.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
                            return slot != -1 && st.getSlot() == slot;
                        });
                disps.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(entity)));
                if(!disps.isEmpty()) itemDisp = disps.getFirst();
            }

            if(itemDisp != null) {
                MultiblockInventory.Singleton e = itemDisp.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).getEntry();
                MutableText ret = Text.empty();
                if(!e.isEmpty()) {
                    ret.append(e.stack().getItemName()).append(Text.literal("\n"+e.count()));
                }

                entity.setText(ret);
            }
        }
    }

    @Override
    public void setStorageSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if(nbtCompound.contains("Slot"))
            this.slot = nbtCompound.getInt("Slot");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if(slot != -1)
            nbtCompound.putInt("Slot", slot);
    }
}
