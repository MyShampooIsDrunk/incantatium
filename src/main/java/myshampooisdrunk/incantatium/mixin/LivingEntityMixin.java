package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.drunk_server_toolkit.item.CustomItemHelper;
import myshampooisdrunk.incantatium.items.AbstractCustomArmorItem;
import myshampooisdrunk.incantatium.items.ornaments.SalvationOrnamentItem;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.entity.effect.StatusEffects.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    private static final List<RegistryEntry<StatusEffect>> negative = List.of(BLINDNESS, WEAKNESS, HUNGER,
            POISON, WITHER, GLOWING, LEVITATION, UNLUCK, DARKNESS, WIND_CHARGED, WEAVING, OOZING, INFESTED, SLOWNESS, NAUSEA, MINING_FATIGUE);

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow public abstract boolean removeStatusEffect(RegistryEntry<StatusEffect> effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method="getPreferredEquipmentSlot",at=@At("HEAD"), cancellable = true)
    public void addPreferredEquipmentSlotCustomArmorItem(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir){
        CustomItemHelper.getCustomItem(stack).ifPresent(custom -> {
            if(custom instanceof AbstractCustomArmorItem i) cir.setReturnValue(i.getSlotType());
        });
    }

    private final LivingEntity dis = (LivingEntity) (Object) this;

    @Inject(method = "tryUseTotem",at=@At("RETURN"),cancellable = true)
    public void useSalvationOrnament(DamageSource source, CallbackInfoReturnable<Boolean> cir){
        ItemStack itemStack = null;
        if(IncantatiumRegistry.SALVATION_ORNAMENT instanceof SalvationOrnamentItem orn && dis instanceof ServerPlayerEntity p
                && orn.canUse(p, Hand.OFF_HAND)){
            itemStack = p.getOffHandStack();
            CustomItemHelper.getCustomItem(itemStack).ifPresent(custom -> {
                if(custom == orn && !p.getItemCooldownManager().isCoolingDown(orn.getItem())){
                    emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
                    setHealth(1.0F);
                    negative.forEach(this::removeStatusEffect);
                    addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                    addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                    addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                    getWorld().sendEntityStatus(this, EntityStatuses.USE_TOTEM_OF_UNDYING);
                    orn.cooldownItems(p);
                }
            });
        }


        cir.setReturnValue(cir.getReturnValue() || itemStack != null);
    }

}
