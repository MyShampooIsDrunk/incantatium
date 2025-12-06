package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;

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
//        Incantatium.LOGGER.info("ACTIVATED!!!!");
        this.tick = ticks;
        if(player.getEntityWorld() instanceof ServerWorld sw)
            sw.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1.5f, 2.5f);
    }

    @Override
    public int getTicks() {
        return tick;
    }

    @Override
    public void serverTick() {
        if(tick > -1) tick--;
    }

    @Override
    public void readData(ReadView readView) {
        tick = readView.getInt("endurance_ticks", -1);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putInt("endurance_ticks", tick);
    }
}
