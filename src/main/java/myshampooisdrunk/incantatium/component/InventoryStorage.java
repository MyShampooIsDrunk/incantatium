package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.item.ItemStack;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface InventoryStorage extends Component {

    MultiblockInventory getInventory();
    default MultiblockInventory.Entry getStack(int slot) {
        return getInventory().get(slot);
    }

    default boolean tryAddStack(int slot, ItemStack stack) {
        assert 0 <= slot && slot < 8;
        return this.getInventory().addStack(slot, stack);
    }
}
