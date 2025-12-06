package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntity;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.List;

public class PedestalTextDisplay implements PedestalDisplay<DisplayEntity.TextDisplayEntity, PedestalEntity.PedestalTextEntity> {
    private int slot;

    public PedestalTextDisplay(DisplayEntity.TextDisplayEntity entity) {
        this.slot = -1;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void initialize(PedestalEntity.PedestalTextEntity entity) {
        this.slot = entity.getSlot();
    }

    @Override
    public void readData(ReadView readView) {
        this.slot = readView.getInt("slot",-1);
    }

    @Override
    public void writeData(WriteView writeView) {
        if(slot != -1) writeView.putInt("slot", slot);
    }
}
