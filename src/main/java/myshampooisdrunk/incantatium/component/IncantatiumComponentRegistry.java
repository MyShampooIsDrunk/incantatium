package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
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
        registry.registerFor(MarkerEntity.class, Incantatium.INVENTORY_STORAGE_COMPONENT_KEY, CoreInventoryStorage::new); // for core
        registry.registerFor(DisplayEntity.ItemDisplayEntity.class, Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY, PedestalInventoryStorage::new);
        registry.registerFor(DisplayEntity.TextDisplayEntity.class, Incantatium.PEDESTAL_TEXT_COMPONENT_KEY, PedestalTextDisplay::new);
        registry.registerFor(InteractionEntity.class, Incantatium.PEDESTAL_INTERACTION_COMPONENT_KEY, PedestalInteraction::new);
    }
}
