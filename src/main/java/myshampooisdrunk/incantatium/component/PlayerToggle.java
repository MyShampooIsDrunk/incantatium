package myshampooisdrunk.incantatium.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class PlayerToggle implements Toggle{
    private boolean state;
    public PlayerToggle(PlayerEntity p){
        state = false;
    }
    @Override
    public boolean get() {
        return state;
    }

    @Override
    public void set(boolean state) {
        this.state = state;
    }

    @Override
    public void readData(ReadView readView) {
        state = readView.getBoolean("ToggleState", false);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putBoolean("ToggleState",state);
    }
}
