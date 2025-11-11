package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntity;
import net.minecraft.entity.Entity;
import org.ladysnake.cca.api.v3.component.Component;

public interface PedestalDisplay<E extends Entity, T extends PedestalEntity<E, T>> extends Component {
    int getSlot();
    void initialize(T entity);
}
