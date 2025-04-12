package myshampooisdrunk.incantatium.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;

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
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        state = tag.getBoolean("toggle_state");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putBoolean("toggle_state", state);
    }
}
