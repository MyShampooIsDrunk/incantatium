package myshampooisdrunk.incantatium.component;

import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class EvokerStrongerEvokers implements StrongerEvokers {
    private int buffLevel = -1;
    private int ticksInWater = 0;
    private int ticksWithoutAttacking = 0;
    private final EvokerEntity evoker;

    @Override
    public void serverTick() {
        if(evoker.isTouchingWater() && buffLevel >= 0 && ticksInWater != 0) ticksInWater++;
        else ticksInWater = 0;
    }

    public EvokerStrongerEvokers(EvokerEntity evoker){
        this.evoker = evoker;
    }

    @Override
    public int getBuffLevel() {
        return buffLevel;
    }

    @Override
    public void setBuffLevel(int level) {
        this.buffLevel = level;
    }

    @Override
    public int getTicksInWater() {
        return ticksInWater;
    }

    @Override
    public int getTicksWithoutAttack() {
        return ticksWithoutAttacking;
    }

    @Override
    public void setTicksWithoutAttack(int ticks) {
        ticksWithoutAttacking = ticks;
    }

    @Override
    public void readData(ReadView readView) {
        buffLevel = readView.getInt("buffLevel", -1);
        ticksInWater = readView.getInt("buffLevel", 0);
        ticksWithoutAttacking = readView.getInt("buffLevel", 0);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putInt("buffLevel", buffLevel);
        writeView.putInt("ticksInWater", ticksInWater);
        writeView.putInt("ticksWithoutAttacking", ticksWithoutAttacking);
    }
}