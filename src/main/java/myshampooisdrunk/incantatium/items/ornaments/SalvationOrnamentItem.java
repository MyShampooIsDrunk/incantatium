package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.OrnamentAbilities;
import myshampooisdrunk.incantatium.items.abilities.Abilities;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.multiblock.recipe.ShapelessMultiblockRecipe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.RemoveEffectsConsumeEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.entity.effect.StatusEffects.*;
import static net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE;

public class SalvationOrnamentItem extends AbstractOrnamentItem {

    private static final DeathProtectionComponent DEATH_PROTECTION = new DeathProtectionComponent(List.of(
            new RemoveEffectsConsumeEffect(RegistryEntryList.of(List.of(BLINDNESS, WEAKNESS, HUNGER,
                    POISON, WITHER, GLOWING, LEVITATION, UNLUCK, DARKNESS, WIND_CHARGED, WEAVING, OOZING, INFESTED, SLOWNESS,
                    NAUSEA, MINING_FATIGUE, SLOW_FALLING))),
            new ApplyEffectsConsumeEffect(List.of(
                    new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1),
                    new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1),
                    new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0)))
    ));


    public SalvationOrnamentItem() {
//        super(Incantatium.id("salvation_ornament"), "Salvation", 9000);
        super(Incantatium.id("salvation_ornament"), "Salvation", 9000, false);
        addComponent(DataComponentTypes.USE_COOLDOWN, new UseCooldownComponent(0.0001f, Optional.of(Incantatium.id("ornament_cooldown"))));
    }

    @Override
    public NbtCompound getCustomNbt() {
        NbtCompound ret = super.getCustomNbt();
        ret.putInt("charges", 2);
        return ret;
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public boolean canUse(PlayerEntity p, Hand hand) {
        ItemStack stack = p.getOffHandStack();
        NbtComponent nbt;
        if(stack != null && (nbt = stack.get(DataComponentTypes.CUSTOM_DATA)) != null) {
            int charges = nbt.copyNbt().getInt("charges").orElse(-1);
            return charges >= 0 && charges < 2 && super.canUse(p, hand) && p.getMainHandStack() != null && p.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING);
        }
        return false;
    }

    public static int getCharges(PlayerEntity player, ItemStack stack) {
        if(stack == null || player == null) return -1;
        NbtComponent nbt;
        int charges = 0;
        if((nbt = stack.get(DataComponentTypes.CUSTOM_DATA)) != null) {
            charges = nbt.copyNbt().getInt("charges").orElse(-1);
            if(charges == -1) return -1;
        }
        if(player.getItemCooldownManager().getCooldownProgress(stack, 0.0F) == 0.0F) charges += 10;
        return charges;
    }

//    private boolean hasCharges(PlayerEntity player, ItemStack stack) {
//        if(stack == null || player == null) return false;
//        NbtComponent nbt;
//        int charges = 0;
//        if((nbt = stack.get(DataComponentTypes.CUSTOM_DATA)) != null) {
//            charges = nbt.copyNbt().getInt("charges").orElse(-1);
//        }
//        return charges > 0 || player.getItemCooldownManager().getCooldownProgress(stack, 0.0F) == 0.0F;
//    }

    @Override
    public void use(World world, LivingEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack totemStack;
        if(!world.isClient() && world instanceof ServerWorld sw && user instanceof PlayerEntity p && canUse(p, hand) && (totemStack = p.getMainHandStack()).isOf(Items.TOTEM_OF_UNDYING)) {
            ItemStack stack = p.getOffHandStack();
            NbtComponent nbt;
            if(stack != null && (nbt = stack.get(DataComponentTypes.CUSTOM_DATA)) != null) {
                int charges = nbt.copyNbt().getInt("charges").orElse(-1);
                if(charges >= 0 && charges < 2) {
                    totemStack.decrement(1);
                    sw.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_FLUTE,
                            SoundCategory.PLAYERS, 1, 1);
                    NbtCompound comp = nbt.copyNbt();
                    comp.putInt("charges", charges + 1);
                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(comp));
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
//        if(user.isSneaking() && user.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
//            int charges;
//            if(user.getStackInHand(hand).contains(DataComponentTypes.CUSTOM_DATA)
//                    && Objects.requireNonNull(user.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA)).copyNbt().contains("charges")
//                    && (charges = Objects.requireNonNull(user.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("charges").orElse(0)) < 3) {
//                NbtComponent nbt = user.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA);
//                assert nbt != null;
//                NbtCompound compound = nbt.copyNbt();
//                compound.putInt("charges", charges + 1);
//                user.getStackInHand(hand).set();
//            }
//        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        if(entity instanceof ServerPlayerEntity player) {
            OrnamentAbilities abilities = player.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            int charges = getCharges(player, stack);
            boolean charged = charges > 0;
            if (abilities.isActive(identifier) && stack.getDamage() < stack.getMaxDamage()) {
                if(slot != null && slot.equals(EquipmentSlot.OFFHAND) && player.isSneaking()) {
                    Abilities.updateAbility(player, Abilities.Type.SALVATION);
                }

                if (slot != null && slot.equals(EquipmentSlot.OFFHAND) && !stack.contains(DataComponentTypes.DEATH_PROTECTION)
                        && charged) {
                    stack.set(DataComponentTypes.DEATH_PROTECTION, DEATH_PROTECTION);
                }

                if (!Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
                    stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                    player.currentScreenHandler.sendContentUpdates();
                }
            } else if (!abilities.isActive(identifier) && Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
                stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
                player.currentScreenHandler.sendContentUpdates();
            }

            if ((!abilities.isActive(identifier) || stack.getDamage() == stack.getMaxDamage() || (slot != null && !slot.equals(EquipmentSlot.OFFHAND)) || !charged) && stack.contains(DataComponentTypes.DEATH_PROTECTION))
                stack.remove(DataComponentTypes.DEATH_PROTECTION);
        }
    }

    @Override
    public AbstractMultiblockRecipe recipe() {
        return new ShapelessMultiblockRecipe(this.create())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.NETHER_STAR, 4).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.ENCHANTED_GOLDEN_APPLE, 8).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, 16).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.RECOVERY_COMPASS, 1).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.RESPAWN_ANCHOR, 64).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.GHAST_TEAR, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.GOLD_BLOCK, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.SOUL_SAND, 256).build());
    }
}
