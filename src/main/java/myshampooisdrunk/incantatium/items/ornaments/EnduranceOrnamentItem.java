package myshampooisdrunk.incantatium.items.ornaments;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.EnduranceEffect;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

public class EnduranceOrnamentItem extends AbstractOrnamentItem{
    public EnduranceOrnamentItem() {
        super(Incantatium.id("endurance_ornament"), "Endurance", 3600);
        addComponent(DataComponentTypes.BLOCKS_ATTACKS, new BlocksAttacksComponent(0.25F, 1.5F,
                List.of(new BlocksAttacksComponent.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                new BlocksAttacksComponent.ItemDamage(3.0F, 1.0F, 1.0F),
                Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                Optional.of(SoundEvents.ITEM_SHIELD_BLOCK),
                Optional.of(SoundEvents.ITEM_SHIELD_BREAK)));
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public boolean canUse(PlayerEntity p, Hand hand) {
        return super.canUse(p, hand) && p.isSneaking();
    }

    @Override
    public void use(World world, LivingEntity l, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.use(world, l, hand, cir);
        if(l instanceof PlayerEntity user && canUse(user, hand)){
            StackReference ref = l.getStackReference(hand.getEquipmentSlot().getOffsetEntitySlotId(98));
            ItemStack stack = ref.get();

        }
    }
}
