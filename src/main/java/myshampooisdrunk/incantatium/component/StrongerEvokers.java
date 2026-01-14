package myshampooisdrunk.incantatium.component;

import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public interface StrongerEvokers extends ServerTickingComponent {
    int getBuffLevel();
    void setBuffLevel(int level);
    int getTicksInWater();
    int getTicksWithoutAttack();
    void setTicksWithoutAttack(int ticks);
}