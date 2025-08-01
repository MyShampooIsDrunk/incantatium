package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.component.MultiblockData;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.registry.MultiblockRegistry;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.ShrineMultiblock;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class PedestalInventoryStorage implements InventorySlotStorage {
    private int slot;
    private boolean dirty;
    private MultiblockInventory.Singleton singleton = MultiblockInventory.EMPTY;
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
//        System.out.println("UPDATE ALERT!!!");
        if(dirty && slot != -1) {
            display.setItemStack(singleton.stack().copyWithCount(1));
            this.markDirty(false);
            String id;
            MultiblockData data = display.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY);
            if((id = data.getEntityId()) != null) {
                AbstractMultiblockStructureEntity<? extends Entity> structureEntity = MultiblockRegistry.ENTITY_TYPES.get(id).defaultEntity();
                if ((MultiblockRegistry.STRUCTURES.get(data.getMultiblockID()) instanceof ShrineMultiblock r) &&
                        (display.getWorld() instanceof ServerWorld sw)) {
                    DisplayEntity.ItemDisplayEntity core = r.getRitualCoreEntity(sw, r.centerFromPedestal(display.getPos(), slot));
                    if(core != null) {
                        InventoryStorage storage = core.getComponent(Incantatium.INVENTORY_STORAGE_COMPONENT_KEY);
                        storage.setSlot(singleton, slot);
                        storage.update();
                    }
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
    public MultiblockInventory.Singleton getEntry() {
        return singleton;
    }

    @Override
    public void setEntry(MultiblockInventory.Singleton singleton) {
        this.singleton = singleton;
    }

    @Override
    public void readData(ReadView readView) {
        int slot;
        if((slot = readView.getInt("Slot", -1)) >= 0) {
            this.slot = slot;
            this.dirty = readView.getBoolean("Dirty", false);
            this.singleton = MultiblockInventory.Singleton.parseData(readView);
            this.display.setItemStack(singleton.stack());
        }
    }

    @Override
    public void writeData(WriteView writeView) {
        if(slot != -1) {
            writeView.putInt("Slot", slot);
            writeView.putBoolean("Dirty", dirty);
            singleton.writeData(writeView);
        }
    }
}
