package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.OrnamentAbilities;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.multiblock.recipe.ShapelessMultiblockRecipe;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

public class EnduranceOrnamentItem extends AbstractOrnamentItem{
    private static final float SHIELD_COOLDOWN_MULTIPLITER = 2F;

    public EnduranceOrnamentItem() {
        super(Incantatium.id("endurance_ornament"), "Endurance", (int) (SHIELD_COOLDOWN_MULTIPLITER * 100), false);

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

        Vec3d rot = EntityAnchorArgumentType.EntityAnchor.FEET.positionAt(attacker).subtract(user.getEntityPos()).normalize().multiply(0.3).add(0,0.25,0);


        if (f > 0.0F && blocksAttacksComponent != null && user instanceof PlayerEntity p) {
            p.getComponent(Incantatium.ENDURANCE_COMPONENT_KEY).activate(100);
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0));
            cooldownItems(p);
            ItemStack shield = Items.SHIELD.getDefaultStack();
            BlocksAttacksComponent b = shield != null ? shield.get(DataComponentTypes.BLOCKS_ATTACKS) : null;
            if(b != null) b.applyShieldCooldown(world, p, f, shield);
            rot = rot.multiply(2);
        }

        attacker.setVelocity(rot);
        attacker.velocityDirty = true;
        attacker.velocityModified = true;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
//        super.inventoryTick(stack, world, entity, slot, ci);
        if(entity instanceof PlayerEntity player) {
            OrnamentAbilities abilities = player.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);

            if(abilities.isActive(identifier) && stack.getDamage() < stack.getMaxDamage()) {
                if(slot != null && slot.equals(EquipmentSlot.OFFHAND) && !stack.contains(DataComponentTypes.BLOCKS_ATTACKS))
                        stack.set(DataComponentTypes.BLOCKS_ATTACKS, new BlocksAttacksComponent(0.25F, SHIELD_COOLDOWN_MULTIPLITER,
                                List.of(new BlocksAttacksComponent.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                                new BlocksAttacksComponent.ItemDamage(0, 0, 0),
                                Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                                Optional.of(SoundEvents.ITEM_SHIELD_BLOCK),
                                Optional.of(SoundEvents.ITEM_SHIELD_BREAK)));

                if(!Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
                    stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                    player.currentScreenHandler.sendContentUpdates();
                }
            }
            else if(!abilities.isActive(identifier) && Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
                stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
                player.currentScreenHandler.sendContentUpdates();
            }

            if((!abilities.isActive(identifier) || stack.getDamage() == stack.getMaxDamage() || (slot != null && !slot.equals(EquipmentSlot.OFFHAND))) && stack.contains(DataComponentTypes.BLOCKS_ATTACKS))
                stack.remove(DataComponentTypes.BLOCKS_ATTACKS);

            switch(player.getComponent(Incantatium.ENDURANCE_COMPONENT_KEY).getTicks()) {
                case 99:
                    stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.KNOCKBACK_RESISTANCE,
                            new EntityAttributeModifier(this.identifier, 0.25, EntityAttributeModifier.Operation.ADD_VALUE),
                            AttributeModifierSlot.OFFHAND).build());
                    break;
                case 0:
                    stack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            }
        }
    }

    @Override
    public AbstractMultiblockRecipe recipe() {
        return new ShapelessMultiblockRecipe(this.create())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.NETHERITE_BLOCK, 2).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addStack(
                        AbstractMultiblockRecipe.IngredientProvider.enchantedBook(Enchantments.PROTECTION, 4), 2).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, 16).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.TURTLE_SCUTE, 64).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.ARMADILLO_SCUTE, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.SHULKER_SHELL, 16).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.IRON_BLOCK, 256).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.OBSIDIAN, 256).build());
    }
}
