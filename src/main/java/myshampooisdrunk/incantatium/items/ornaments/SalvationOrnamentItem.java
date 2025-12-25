package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.multiblock.recipe.ShapelessMultiblockRecipe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.RemoveEffectsConsumeEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

import static net.minecraft.entity.effect.StatusEffects.*;
import static net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE;

public class SalvationOrnamentItem extends AbstractOrnamentItem {
    private static final List<RegistryEntry<StatusEffect>> negative = List.of(BLINDNESS, WEAKNESS, HUNGER,
            POISON, WITHER, GLOWING, LEVITATION, UNLUCK, DARKNESS, WIND_CHARGED, WEAVING, OOZING, INFESTED, SLOWNESS, NAUSEA, MINING_FATIGUE);

    public SalvationOrnamentItem() {
        super(Incantatium.id("salvation_ornament"), "Salvation", 9000, false);
        addComponent(DataComponentTypes.DEATH_PROTECTION, new DeathProtectionComponent(List.of(
                new RemoveEffectsConsumeEffect(RegistryEntryList.of(negative)),
                new ApplyEffectsConsumeEffect(List.of(
                        new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1),
                        new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1),
                        new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0)))
        )));
    }

    @Override
    public NbtCompound getCustomNbt() {
        NbtCompound ret = super.getCustomNbt();
        ret.putInt("charges", 3);
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
            int charges = nbt.copyNbt().getInt("charges").orElse(0);
            return charges < 3 && super.canUse(p, hand) && p.getMainHandStack() != null && p.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING);
        }
        return false;
    }

    //TODO: THIS PLEASE PLEASE PLEASE
    @Override
    public void use(World world, LivingEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {

        if(user instanceof PlayerEntity p && canUse(p, hand) && p.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            ItemStack stack = p.getOffHandStack();
            NbtComponent nbt;
            if(stack != null && (nbt = stack.get(DataComponentTypes.CUSTOM_DATA)) != null) {
                int charges = nbt.copyNbt().getInt("charges").orElse(0);
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
