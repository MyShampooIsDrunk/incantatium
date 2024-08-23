package myshampooisdrunk.incantatium.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class PlayerRiptideCooldown implements RiptideCooldown{

    public static final int MAX_CHARGES = 3;

    private final PlayerEntity player;
    private int lastUse;
    private int charges;
    private int cooldown;

    public PlayerRiptideCooldown(PlayerEntity player){
        this.player = player;
        charges = MAX_CHARGES;
    }

    @Override
    public int getLastUse() {
        return lastUse;
    }

    @Override
    public void setLastUse(int tick) {
        lastUse = tick;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public void setCooldown(int ticks) {
        cooldown = ticks;
    }

    @Override
    public int get() {
        return charges;
    }

    @Override
    public void set(int charges) {
        this.charges = charges;
    }

    @Override
    public void serverTick() {
        if(charges < MAX_CHARGES) lastUse++;
        if(lastUse == cooldown) {
            lastUse = 0;
            if(charges < MAX_CHARGES){//should always be true but just in case
                charges++;
                player.sendMessage(
                        Text.literal("RIPTIDE RECHARGED | ")
                                .append(Text.literal(charges + " / " + MAX_CHARGES).setStyle(
                                        Style.EMPTY.withBold(true).withColor(switch(charges){
                                            case 0 -> Colors.RED;
                                            case 1 -> Colors.LIGHT_RED;
                                            case 2 -> Colors.YELLOW;
                                            default -> Colors.GREEN;
                                        })
                                )).append(Text.literal(" REMAINING")), true
                );
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        lastUse = tag.getInt("lastUse");
        charges = tag.getInt("riptideCharges");
        cooldown = tag.getInt("riptideCooldown");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("lastUse", lastUse);
        tag.putInt("riptideCharges", charges);
        tag.putInt("riptideCooldown", cooldown);
    }

    @Override
    public boolean useRiptide(){//returns false if the player cant use riptide
        if(charges <= 0) return false;
        if(cooldown == 0) return true;
        if(cooldown > 0) charges--;
        if(charges == 0) player.getItemCooldownManager().set(Items.TRIDENT, cooldown);
        lastUse = 0;
        return true;
    }
}
