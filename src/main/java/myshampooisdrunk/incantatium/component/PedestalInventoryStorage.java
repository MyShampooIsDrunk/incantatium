package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.component.MultiblockCoreData;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.List;

public class PedestalInventoryStorage implements InventorySlotStorage {
    private int slot;
    private boolean dirty;
    private MultiblockInventory.Entry entry = MultiblockInventory.EMPTY;
    private final DisplayEntity.ItemDisplayEntity display;
    public PedestalInventoryStorage(DisplayEntity.ItemDisplayEntity display) {
        this.slot = -1;
        this.dirty = false;
        this.display = display;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void update() {
        if(dirty && slot != -1) {
            display.setItemStack(entry.stack());
            this.markDirty(false);
            MultiblockStructure struc =
//            List<? extends EntityType<?>> types = entityList.keySet().stream()
//                    .map(AbstractMultiblockStructureEntity::getType).toList();
//            List<Entity> entities = new ArrayList<>();
//            types.forEach(t -> entities.addAll(world.getEntitiesByType(
//                    t,
//                    entityBox.offset(pos).expand(1),
//                    e -> {
//                        if(e instanceof DisplayEntity.ItemDisplayEntity core) {
//                            MultiblockCoreData coreData = core.getComponent(DST.MULTIBLOCK_CORE_DATA_COMPONENT_KEY);
//                            return !coreData.getBlockstateData().isEmpty();
//                        }
//                        return false;
//                    }))
//            );
        }
    }

    @Override
    public void setStorageSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void markDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public MultiblockInventory.Entry getEntry() {
        return entry;
    }

    @Override
    public void setEntry(MultiblockInventory.Entry entry) {
        this.entry = entry;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if(nbtCompound.contains("Slot")) {
            this.slot = nbtCompound.getInt("Slot");
            this.dirty = nbtCompound.getBoolean("Dirty");
            this.entry = MultiblockInventory.Entry.fromNbt(nbtCompound, wrapperLookup);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if(slot != -1) {
            nbtCompound.putInt("Slot", slot);
            nbtCompound.putBoolean("Dirty", dirty);
            nbtCompound.putInt("Count", this.entry.count());
            entry.stack().toNbt(wrapperLookup, nbtCompound);
        }
    }
}
