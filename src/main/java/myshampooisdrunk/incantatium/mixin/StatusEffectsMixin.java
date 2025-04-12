package myshampooisdrunk.incantatium.mixin;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(StatusEffects.class)
public class StatusEffectsMixin {
    @Redirect(method = "<clinit>", at=@At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/effect/StatusEffect;addAttributeModifier(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/util/Identifier;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)Lnet/minecraft/entity/effect/StatusEffect;",
            ordinal = 9
    ))
    private static StatusEffect modifyAttribute(StatusEffect instance, RegistryEntry<EntityAttribute> attribute, Identifier id, double amount, EntityAttributeModifier.Operation operation){
        return instance.addAttributeModifier(EntityAttributes.GENERIC_ARMOR, id, -2.5, operation);
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 3.0f))
    private static double modifyStrength(double constant){
        return 2;
    }
}
