package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.component.MultiblockData;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.registry.MultiblockRegistry;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.RitualMultiblock;
import myshampooisdrunk.incantatium.multiblock.entity.RitualCoreEntity;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;

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
            String id;
            MultiblockData data = display.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY);
            if((id = data.getEntityId()) != null) {
                AbstractMultiblockStructureEntity<? extends Entity> structureEntity = MultiblockRegistry.ENTITY_TYPES.get(id).defaultEntity();
                if ((MultiblockRegistry.STRUCTURES.get(data.getMultiblockID()) instanceof RitualMultiblock r) &&
                        (display.getWorld() instanceof ServerWorld sw) &&
                        r.detectBuiltMultiblock(sw, r.centerFromPedestal(display.getPos(), slot))) {
                    DisplayEntity.ItemDisplayEntity core = r.getRitualCoreEntity(sw, r.centerFromPedestal(display.getPos(), slot));
                    if(core == null) return;
                    else core.getComponent(Incantatium.INVENTORY_STORAGE_COMPONENT_KEY).setSlot(entry, slot);
                }




            }
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
