package myshampooisdrunk.incantatium.component;

import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public interface EnduranceEffect extends ServerTickingComponent {
    boolean getActive();
    void activate(int ticks);
}
