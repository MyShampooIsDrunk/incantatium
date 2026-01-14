package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.util.RavagerSmashHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import java.util.Objects;

public class RavagerCustomAttacks implements RavagerCooldowns{

    public static final int BREAK_BLOCK_COOLDOWN = 10;// 0.5 sec
    public static final int SMASH_ATTACK_COOLDOWN = 1800;//3600;// 3 min
    public static final int SMASH_ATTACK_VERTICAL_THRESHOLD = 0;
    public static final int SMASH_ATTACK_HORIZONTAL_THRESHOLD = 12; // entity is farther than this -> eligible for smash attack
    public static final int SMASH_ATTACK_MAX_DISTANCE = 26;

    private int timeSinceLastBreak = 0;
    private int timeSinceLastSmashAttack = 0;
    private boolean isSmashAttacking = false;
    private final RavagerEntity ravager;

    @Override
    public void serverTick() {

        Objects.requireNonNull(this.ravager.getAttributeInstance(EntityAttributes.SAFE_FALL_DISTANCE)).setBaseValue(75);

        timeSinceLastSmashAttack++;
        timeSinceLastBreak++;
        if(RavagerSmashHelper.canSmash(ravager)){
            RavagerSmashHelper.runSmash(ravager, ravager.getTarget());
        }
        if(hasStartedSmashAttack()){
            RavagerSmashHelper.runSmash(ravager, ravager.getTarget());
        }
    }
    public RavagerCustomAttacks(RavagerEntity ravager){
        this.ravager = ravager;
    }

    @Override
    public int getLastBreak() {
        return timeSinceLastBreak;
    }

    @Override
    public void setLastBreak(int tick) {
        this.timeSinceLastBreak = tick;
    }

    @Override
    public int getLastSmashAttack() {
        return timeSinceLastSmashAttack;
    }

    @Override
    public void setLastSmashAttack(int tick) {
        this.timeSinceLastSmashAttack = tick;
    }

    @Override
    public boolean hasStartedSmashAttack() {
        return isSmashAttacking;
    }

    @Override
    public void setStartedSmashAttack(boolean bl) {
        this.isSmashAttacking = bl;
    }

    @Override
    public void readData(ReadView readView) {
        timeSinceLastBreak = readView.getInt("lastBreak", 0);
        timeSinceLastSmashAttack = readView.getInt("lastSmash", 0);
        isSmashAttacking = readView.getBoolean("isSmashAttacking", false);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putInt("lastBreak",timeSinceLastBreak);
        writeView.putInt("lastSmash",timeSinceLastSmashAttack);
        writeView.putBoolean("isSmashAttacking", isSmashAttacking);
    }
}