package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.RemoveEffectsConsumeEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.entity.effect.StatusEffects.*;
import static net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE;

public class SalvationOrnamentItem extends AbstractOrnamentItem{
    private static final List<RegistryEntry<StatusEffect>> negative = List.of(BLINDNESS, WEAKNESS, HUNGER,
            POISON, WITHER, GLOWING, LEVITATION, UNLUCK, DARKNESS, WIND_CHARGED, WEAVING, OOZING, INFESTED, SLOWNESS, NAUSEA, MINING_FATIGUE);

    public SalvationOrnamentItem() {
        super(Incantatium.id("salvation_ornament"), "Salvation", 9000);
        addComponent(DataComponentTypes.DEATH_PROTECTION, new DeathProtectionComponent(List.of(
                new RemoveEffectsConsumeEffect(RegistryEntryList.of(negative)),
                new ApplyEffectsConsumeEffect(List.of(
                        new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1),
                        new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1),
                        new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0)))
        )));
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public void use(World world, LivingEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        cir.setReturnValue(ActionResult.PASS);
    }
}
