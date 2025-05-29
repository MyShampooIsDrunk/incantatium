package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class HydrousOrnamentItem extends AbstractOrnamentItem{
    public HydrousOrnamentItem() {
        super(Incantatium.id("hydrous_ornament"), "Hydrous", 2400);
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public boolean canUse(PlayerEntity p, Hand hand) {
        return p.isSneaking() && super.canUse(p, hand);
    }

    @Override
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.use(world, user, hand, cir);
        if(canUse(user, hand)) user.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 400, 2));
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return AttributeModifiersComponent.builder().add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY,
                new EntityAttributeModifier(this.identifier, 0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                AttributeModifierSlot.OFFHAND).build();
    }
}
