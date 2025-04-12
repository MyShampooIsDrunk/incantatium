package myshampooisdrunk.incantatium.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class PlayerEnduranceEffect implements EnduranceEffect{

    private int tick;
    private final PlayerEntity player;

    public PlayerEnduranceEffect(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public boolean getActive() {
        return tick > -1;
    }

    @Override
    public void activate(int ticks) {
        this.tick = ticks;
        if(player.getWorld() instanceof ServerWorld sw) {
            sw.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS);
        }
    }

    @Override
    public void serverTick() {
        if(tick == 0){
            if(player.getWorld() instanceof ServerWorld sw) {
                sw.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS);
            }
        }
        if(tick > -1) tick--;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tick = tag.getInt("endurance_ticks");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("endurance_ticks",tick);
    }
}
