package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.multiblock.recipe.ShapelessMultiblockRecipe;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CycloneOrnamentItem extends AbstractOrnamentItem{
    public CycloneOrnamentItem() {
        super(Incantatium.id("cyclone_ornament"), "Cyclone", 100); // 5 sec instead of 7.5
    }

    @Override
    public void use(World world, LivingEntity e, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.use(world, e, hand, cir);
        if(e instanceof PlayerEntity user && canUse(user, hand)) {
            if (world instanceof ServerWorld serverWorld) {
                ProjectileEntity.spawnWithVelocity(
                        (world2, shooter, stack) -> new WindChargeEntity(user, world, user.getX(), user.getEyePos().getY(), user.getZ()),
                        serverWorld,
                        user.getStackInHand(hand),
                        user,
                        0.0F,
                        1.5F,
                        1.0F
                );
            }

            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.ENTITY_WIND_CHARGE_THROW,
                    SoundCategory.NEUTRAL,
                    0.5F,
                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
            );
        }
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public AbstractMultiblockRecipe recipe() {
        return new ShapelessMultiblockRecipe(this.create())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 1).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addStack(
                        AbstractMultiblockRecipe.IngredientProvider.enchantedBook(Enchantments.PROTECTION, 4), 2).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, 16).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, 16).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.SCULK_CATALYST, 32).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.BREEZE_ROD, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addTag(ItemTags.WOOL, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.ENDER_EYE, 128).build());
    }

}
