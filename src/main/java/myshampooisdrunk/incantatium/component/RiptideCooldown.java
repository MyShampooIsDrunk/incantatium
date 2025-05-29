package myshampooisdrunk.incantatium.component;

import net.minecraft.item.ItemStack;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public interface RiptideCooldown extends ServerTickingComponent {
    int getLastUse();
    void setLastUse(int tick);
    int getCooldown();
    void setCooldown(int ticks);
    int get();
    void set(int charges);
    boolean useRiptide(ItemStack tridentStack);
}
