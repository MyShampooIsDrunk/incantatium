package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntityGenerator;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.UUID;

public interface PedestalDisplay extends Component {
    void update();
    void setStorageSlot(int slot);
    int getStorageSlot();
}
