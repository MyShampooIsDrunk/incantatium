package myshampooisdrunk.incantatium.mixin;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(StatusEffects.class)
public abstract class StatusEffectsMixin {

    @Shadow
    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return null;
    }

    @Redirect(method = "<clinit>", at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffects;register(Ljava/lang/String;Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/registry/entry/RegistryEntry;", ordinal = 28))
    private static RegistryEntry<StatusEffect> redirectConduitPower(String id, StatusEffect statusEffect) {
//        System.out.println(id);
        return register(id, statusEffect.addAttributeModifier(EntityAttributes.ATTACK_SPEED, Identifier.ofVanilla("effect.conduit_power"), 0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 3.0f))
    private static double modifyStrength(double constant){
        return 2;
    }
}
