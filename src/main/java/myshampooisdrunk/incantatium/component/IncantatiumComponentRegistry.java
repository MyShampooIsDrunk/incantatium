package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class IncantatiumComponentRegistry implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, Incantatium.RIPTIDE_COOLDOWN_COMPONENT_KEY, PlayerRiptideCooldown::new);
        registry.registerFor(PlayerEntity.class, Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY, PlayerOrnamentAbilities::new);
        registry.registerFor(PlayerEntity.class, Incantatium.TOGGLE_COMPONENT_KEY, PlayerToggle::new);
        registry.registerFor(PlayerEntity.class, Incantatium.ENDURANCE_COMPONENT_KEY, PlayerEnduranceEffect::new);
    }
}
