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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
        if(user instanceof PlayerEntity p && canUse(p, hand) && world instanceof ServerWorld sw) {
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1800, 4));
            sw.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK, SoundCategory.PLAYERS, 1.25f, 0.75f);
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

    @Override
    public AbstractMultiblockRecipe recipe() {
        return new ShapelessMultiblockRecipe(this.create())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.NETHER_STAR, 4).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.ENCHANTED_GOLDEN_APPLE, 6).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.DIAMOND_BLOCK, 32).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.GOLDEN_APPLE, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.HONEY_BLOCK, 64).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.CRYING_OBSIDIAN, 64).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.REDSTONE_BLOCK, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.GLISTERING_MELON_SLICE, 256).build());
    }
}
