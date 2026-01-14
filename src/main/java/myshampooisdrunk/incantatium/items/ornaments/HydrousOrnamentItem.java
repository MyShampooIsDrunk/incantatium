package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.multiblock.recipe.ShapelessMultiblockRecipe;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
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
    public void use(World world, LivingEntity l, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.use(world, l, hand, cir);
        if(l instanceof PlayerEntity user && canUse(user, hand)) user.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 600, 2));
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return AttributeModifiersComponent.builder().add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY,
                new EntityAttributeModifier(this.identifier, 0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                AttributeModifierSlot.OFFHAND).build();
    }

    @Override
    public AbstractMultiblockRecipe recipe() {
        return new ShapelessMultiblockRecipe(this.create())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.TRIDENT, 4).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.CONDUIT, 1).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.SPONGE, 32).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, 16).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.TURTLE_SCUTE, 64).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.NAUTILUS_SHELL, 64).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.SEA_LANTERN, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.GOLD_BLOCK, 256).build());
    }
}
