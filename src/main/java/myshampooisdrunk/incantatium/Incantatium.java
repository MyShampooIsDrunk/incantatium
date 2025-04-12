package myshampooisdrunk.incantatium;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.incantatium.component.*;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import myshampooisdrunk.incantatium.util.SoulboundHelper;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Incantatium implements ModInitializer {

	public static final boolean DEV_MODE = true;

    public static final Logger LOGGER = LoggerFactory.getLogger("incantatium");

	public static final FoodComponent MOD_GOD_APPLE = new FoodComponent.Builder()
			.nutrition(8)
			.saturationModifier(2F)
			.statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2), 1.0F)
			.statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0), 1.0F)
			.statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 4800, 2), 1.0F)
			.alwaysEdible()
			.build();
	public static final RegistryKey<DamageType> TRIDENT_BYPASS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("trident_bypass"));

	public static final ComponentKey<PlayerRiptideCooldown> RIPTIDE_COOLDOWN_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("riptide_cooldown"), PlayerRiptideCooldown.class);
	public static final ComponentKey<PlayerOrnamentAbilities> ORNAMENT_ABILITIES_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("ornament_abilities"), PlayerOrnamentAbilities.class);
	public static final ComponentKey<PlayerToggle> TOGGLE_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("toggle_state"), PlayerToggle.class);
	public static final ComponentKey<PlayerEnduranceEffect> ENDURANCE_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("endurance_effect"), PlayerEnduranceEffect.class);

	@Override
	public void onInitialize() {
		ServerPlayerEvents.COPY_FROM.register(SoulboundHelper::copySoulBoundItems);
		IncantatiumRegistry.init();
		DST.initializeCommands();
	}
	public static Identifier id(String path){
		return Identifier.of("incantatium", path);
	}
}