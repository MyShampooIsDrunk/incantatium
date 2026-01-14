package myshampooisdrunk.incantatium.component;

import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public interface RavagerCooldowns extends ServerTickingComponent {
    int getLastBreak();
    void setLastBreak(int tick);
    int getLastSmashAttack();
    void setLastSmashAttack(int tick);
    boolean hasStartedSmashAttack();
    void setStartedSmashAttack(boolean bl);
}