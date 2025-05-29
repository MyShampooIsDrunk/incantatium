package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ConstitutionOrnamentItem extends AbstractOrnamentItem{
    public ConstitutionOrnamentItem() {
        super(Incantatium.id("constitution_ornament"), "Constitution", 1);
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public boolean canUse(PlayerEntity p, Hand hand) {
        return false;
    }

    @Override
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        cir.setReturnValue(ActionResult.PASS);
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return AttributeModifiersComponent.builder().add(EntityAttributes.MAX_HEALTH,
                new EntityAttributeModifier(this.identifier, 10, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.OFFHAND).build();
    }
}
