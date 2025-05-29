package myshampooisdrunk.incantatium.component;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.Map;
import java.util.Set;

public interface OrnamentAbilities extends ServerTickingComponent {
    Map<Identifier, Integer> getCooldowns();
    Set<Identifier> getActive();
    boolean isActive(Identifier id);
}
