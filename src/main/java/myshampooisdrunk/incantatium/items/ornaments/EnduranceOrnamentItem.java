package myshampooisdrunk.incantatium.items.ornaments;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.EnduranceEffect;
import myshampooisdrunk.incantatium.component.OrnamentAbilities;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

public class EnduranceOrnamentItem extends AbstractOrnamentItem{
    private static final float SHIELD_COOLDOWN_MULTIPLITER = 3F;

    public EnduranceOrnamentItem() {
        super(Incantatium.id("endurance_ornament"), "Endurance", (int) (SHIELD_COOLDOWN_MULTIPLITER * 100), false);
        addComponent(DataComponentTypes.BLOCKS_ATTACKS, new BlocksAttacksComponent(0.25F, SHIELD_COOLDOWN_MULTIPLITER,
                List.of(new BlocksAttacksComponent.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                new BlocksAttacksComponent.ItemDamage(0, 0, 0),
                Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                Optional.of(SoundEvents.ITEM_SHIELD_BLOCK),
                Optional.of(SoundEvents.ITEM_SHIELD_BREAK)));

        addComponent(DataComponentTypes.USE_COOLDOWN, new UseCooldownComponent(0.0001f, Optional.of(Incantatium.id("ornament_cooldown"))));
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public boolean canUse(PlayerEntity p, Hand hand) {
        return super.canUse(p, hand) && p.isSneaking();
    }

    @Override
    public void onTakeShieldHit(ServerWorld world, LivingEntity attacker, LivingEntity user, CallbackInfo ci) {
        super.onTakeShieldHit(world, attacker, user, ci);

        ItemStack stack = user.getStackInHand(user.getActiveHand());
        BlocksAttacksComponent blocksAttacksComponent = stack != null ? stack.get(DataComponentTypes.BLOCKS_ATTACKS) : null;
        float f = attacker.getWeaponDisableBlockingForSeconds();

        Vec3d diff = EntityAnchorArgumentType.EntityAnchor.FEET.positionAt(attacker).subtract(user.getEntityPos()).add(0,1,0);
        Vec3d rot = diff.normalize();
        attacker.setVelocity(rot);
        attacker.velocityDirty = true;
        attacker.velocityModified = true;

        if (f > 0.0F && blocksAttacksComponent != null && user instanceof PlayerEntity p) {
            p.getComponent(Incantatium.ENDURANCE_COMPONENT_KEY).activate(100);
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.KNOCKBACK_RESISTANCE,
                    new EntityAttributeModifier(this.identifier, 0.6, EntityAttributeModifier.Operation.ADD_VALUE),
                    AttributeModifierSlot.OFFHAND).build());//TODO: fix ts
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0));
            cooldownItems(p);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        super.inventoryTick(stack, world, entity, slot, ci);
        if(entity instanceof ServerPlayerEntity p && !world.isClient()){

            EnduranceEffect effect = p.getComponent(Incantatium.ENDURANCE_COMPONENT_KEY);
            if(effect.getActive() && effect.getTicks() == 0) {
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
            }
        }
        //if(player.getEntityWorld() instanceof ServerWorld sw) {
        //            linkedStack = player.getBlockingItem();
        //            AttributeModifiersComponent comp = AttributeModifiersComponent.builder().add(EntityAttributes.KNOCKBACK_RESISTANCE,
        //                    new EntityAttributeModifier(Incantatium.id))
        //        }
    }
}
