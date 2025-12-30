package myshampooisdrunk.incantatium;

import com.mojang.datafixers.util.Either;
import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.incantatium.block.dispenser.BundleDispenserBehavior;
import myshampooisdrunk.incantatium.command.BalanceCommand;
import myshampooisdrunk.incantatium.component.*;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import myshampooisdrunk.incantatium.util.PostDeathHelper;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BundleItem;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Incantatium implements ModInitializer {

	public static final boolean DEV_MODE = Boolean.parseBoolean(System.getenv("DEV_MODE"));

    public static final Logger LOGGER = LoggerFactory.getLogger("incantatium");

	public static final FoodComponent MOD_GOD_APPLE = new FoodComponent.Builder()
			.nutrition(4)
			.saturationModifier(1.4F)
			.alwaysEdible()
			.build();
	public static final ConsumableComponent MOD_GOD_APPLE_EFFECTS = ConsumableComponents.food().consumeEffect(
			new ApplyEffectsConsumeEffect(List.of(
					new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2),
					new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0),
					new StatusEffectInstance(StatusEffects.ABSORPTION, 4800, 3)
			))).build();
	public static final RegistryKey<DamageType> TRIDENT_BYPASS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("trident_bypass"));
	public static final TagKey<EntityType<?>> CYCLONE_NO_DEFLECT = TagKey.of(RegistryKeys.ENTITY_TYPE, id("cyclone_no_deflect"));

	public static final ComponentKey<PlayerRiptideCooldown> RIPTIDE_COOLDOWN_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("riptide_cooldown"), PlayerRiptideCooldown.class);
	public static final ComponentKey<PlayerOrnamentAbilities> ORNAMENT_ABILITIES_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("ornament_abilities"), PlayerOrnamentAbilities.class);
	public static final ComponentKey<PlayerToggle> TOGGLE_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("toggle_state"), PlayerToggle.class);
	public static final ComponentKey<PlayerEnduranceEffect> ENDURANCE_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("endurance_effect"), PlayerEnduranceEffect.class);
	public static final ComponentKey<CoreInventoryStorage> INVENTORY_STORAGE_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("inventory_storage"), CoreInventoryStorage.class);
	public static final ComponentKey<PedestalInventoryStorage> PEDESTAL_STORAGE_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("pedestal_storage"), PedestalInventoryStorage.class);
	public static final ComponentKey<PedestalTextDisplay> PEDESTAL_TEXT_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("pedestal_text"), PedestalTextDisplay.class);
	public static final ComponentKey<PedestalInteraction> PEDESTAL_INTERACTION_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("pedestal_interaction"), PedestalInteraction.class);
	public static final ComponentKey<PlayerBankAccount> PLAYER_BANK_ACCOUNT_COMPONENT_KEY = ComponentRegistry.getOrCreate(
			id("bank"), PlayerBankAccount.class);

	@Override
	public void onInitialize() {
		if(DEV_MODE) LOGGER.info("NOTE: DEV MODE IS ON");
		ServerPlayerEvents.COPY_FROM.register(PostDeathHelper::copyBankAccount);
		ServerPlayerEvents.COPY_FROM.register(PostDeathHelper::copySoulBoundItems);
		ServerPlayerEvents.AFTER_RESPAWN.register(PostDeathHelper::damageOrnament);
		CommandRegistrationCallback.EVENT.register((d,r,e) -> {
			BalanceCommand.register(d);
		});
		IncantatiumRegistry.init();
		DST.initializeCommands();
		for (BundleItem bundle : BundleItem.getBundles()) {
			DispenserBlock.registerBehavior(bundle, new BundleDispenserBehavior());
		}

	}
	public static Identifier id(String path){
		return Identifier.of("incantatium", path);
	}

	public static Either<CustomModelDataComponent, Identifier> getModel(Identifier path){
		return Either.right(path);
	}
}