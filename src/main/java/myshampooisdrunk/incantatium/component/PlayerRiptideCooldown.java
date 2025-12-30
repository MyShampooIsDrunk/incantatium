package myshampooisdrunk.incantatium.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
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
                StringBuilder txt = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    if(i < charges) {
                        txt.append('\uEa01');
                    } else txt.append('\uEa02');
                    if(i < 2) txt.append(" | ");
                }
                player.sendMessage(
                        Text.literal("RIPTIDE RECHARGED | ")
                                .append(Text.literal(txt.toString()).setStyle(
                                        Style.EMPTY.withBold(true).withColor(switch(charges){
                                            case 0 -> Colors.RED;
                                            case 1 -> Colors.LIGHT_RED;
                                            case 2 -> Colors.YELLOW;
                                            default -> Colors.GREEN;
                                        })
                                )), true
                );
            } else if(charges > MAX_CHARGES) {
                charges = MAX_CHARGES;
            }
        }
    }

    @Override
    public void readData(ReadView readView) {
        lastUse = readView.getInt("last_use", 0);
        charges = readView.getInt("riptide_charges", 0);
        cooldown = readView.getInt("riptide_cooldown", 0);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putInt("last_use", lastUse);
        writeView.putInt("riptide_charges", charges);
        writeView.putInt("riptide_cooldown", cooldown);
    }

    @Override
    public boolean useRiptide(ItemStack tridentStack){//returns false if the player cant use riptide
        if(charges <= 0) return false;
        if(cooldown == 0) return true;
        if(cooldown > 0) charges--;
        if(charges == 0) player.getItemCooldownManager().set(tridentStack, cooldown);
        lastUse = 0;
        return true;
    }
}
