package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class PedestalInteraction implements PedestalDisplay<InteractionEntity, PedestalEntity.PedestalInteractionEntity> {
    private int slot;

    public PedestalInteraction(InteractionEntity entity) {
        this.slot = -1;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void initialize(PedestalEntity.PedestalInteractionEntity entity) {
        this.slot = entity.getSlot();
    }

    @Override
    public void readData(ReadView readView) {
        this.slot = readView.getInt("Slot",-1);
    }

    @Override
    public void writeData(WriteView writeView) {
        if(slot != -1) writeView.putInt("Slot",slot);
    }
}
