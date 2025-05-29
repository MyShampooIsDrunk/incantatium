package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import myshampooisdrunk.drunk_server_toolkit.item.CustomItemHelper;
import myshampooisdrunk.incantatium.items.AbstractCustomArmorItem;
import myshampooisdrunk.incantatium.items.ornaments.SalvationOrnamentItem;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.type.DeathProtectionComponent;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.entity.effect.StatusEffects.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private static final List<RegistryEntry<StatusEffect>> negative = List.of(BLINDNESS, WEAKNESS, HUNGER,
            POISON, WITHER, GLOWING, LEVITATION, UNLUCK, DARKNESS, WIND_CHARGED, WEAVING, OOZING, INFESTED, SLOWNESS, NAUSEA, MINING_FATIGUE);

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow public abstract boolean removeStatusEffect(RegistryEntry<StatusEffect> effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

//    @Inject(method="getPreferredEquipmentSlot",at=@At("HEAD"), cancellable = true)
//    public void addPreferredEquipmentSlotCustomArmorItem(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir){
//        CustomItemHelper.getCustomItem(stack).ifPresent(custom -> {
//            if(custom instanceof AbstractCustomArmorItem i) cir.setReturnValue(i.getSlotType());
//        });
//    }

    private final LivingEntity dis = (LivingEntity) (Object) this;

    @Inject(method="tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void cancelReviveIfOnCooldown(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local ItemStack stack, @Local DeathProtectionComponent component){
        if(dis instanceof ServerPlayerEntity p){
            if(p.getItemCooldownManager().isCoolingDown(stack)) cir.setReturnValue(false);
        }
    }

    @Redirect(method = "tryUseDeathProtector",at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    public void useSalvationOrnament(ItemStack instance, int amount){
        AtomicBoolean bl = new AtomicBoolean(true);
        CustomItemHelper.getCustomItem(instance).ifPresent(custom -> {
            if(custom instanceof SalvationOrnamentItem o && dis instanceof ServerPlayerEntity p) {
                o.cooldownItems(p);
                bl.set(false);
            }
        });
        if(bl.get()) instance.decrement(amount);
    }

}
