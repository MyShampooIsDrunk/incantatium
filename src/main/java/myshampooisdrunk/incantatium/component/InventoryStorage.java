package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.item.ItemStack;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public interface InventoryStorage extends ServerTickingComponent {

    MultiblockInventory getInventory();

    default MultiblockInventory.Singleton getStack(int slot) {
        return getInventory().get(slot);
    }

    default void setSlot(MultiblockInventory.Singleton singleton, int slot) {
        getInventory().set(singleton, slot);
    }

    void update();

    void startTimer();

    boolean isTicking();

    void cancel();

    default boolean tryAddStack(int slot, ItemStack stack) {
        assert 0 <= slot && slot < 8;
        return this.getInventory().addStack(slot, stack);
    }
}
