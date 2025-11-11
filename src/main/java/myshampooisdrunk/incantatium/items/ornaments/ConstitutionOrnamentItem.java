package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ConstitutionOrnamentItem extends AbstractOrnamentItem{
    public ConstitutionOrnamentItem() {
        super(Incantatium.id("constitution_ornament"), "Constitution", 6000);
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public void use(World world, LivingEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.use(world, user, hand, cir);
        if(user instanceof PlayerEntity p && canUse(p, hand)) {
            float initialAbsorption = p.getActiveStatusEffects().containsKey(StatusEffects.ABSORPTION) ? p.getAbsorptionAmount() : 0;
            p.setAbsorptionAmount(initialAbsorption + 20);
            cooldownItems(p);
        }
    }

    @Override
    public boolean canUse(PlayerEntity p, Hand hand) {
        return p.isSneaking() && super.canUse(p, hand);
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return AttributeModifiersComponent.builder().add(EntityAttributes.MAX_HEALTH,
                new EntityAttributeModifier(this.identifier, 10, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.OFFHAND).build();
    }
}
