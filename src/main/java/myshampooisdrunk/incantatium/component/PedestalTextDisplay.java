package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.List;

public class PedestalTextDisplay implements PedestalDisplay {
    private final DisplayEntity.TextDisplayEntity entity;
    private int slot;

    public PedestalTextDisplay(DisplayEntity.TextDisplayEntity entity) {
        this.entity = entity;
        this.slot = -1;
    }

    @Override
    public void update() {
        if(entity.getWorld() instanceof ServerWorld s) {
            List<DisplayEntity.ItemDisplayEntity> disps = s.getEntitiesByType(EntityType.ITEM_DISPLAY, Box.of(entity.getPos(), 2, 2,2),
                    e -> {
                        InventorySlotStorage st = e.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
                        return slot != -1 && st.isDirty() && st.getSlot() == slot;
                    });
            disps.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(entity)));
            if(!disps.isEmpty()) {
                int count = disps.getFirst().getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).getEntry().count();
                entity.setText(Text.literal(count == 0 ? "" : String.valueOf(count)));
            }
        }
    }

    @Override
    public void setStorageSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public int getStorageSlot() {
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
