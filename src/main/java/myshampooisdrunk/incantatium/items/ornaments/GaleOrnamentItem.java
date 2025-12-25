package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.multiblock.recipe.ShapelessMultiblockRecipe;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;

public class GaleOrnamentItem extends AbstractOrnamentItem{
    public GaleOrnamentItem() {
        super(Incantatium.id("gale_ornament"), "Gale", 900);
    }

    @Override
    public boolean canUse(PlayerEntity p, Hand hand) {
        return super.canUse(p, hand) && p.isSneaking();
    }

    @Override
    public void use(World world, LivingEntity l, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.use(world, l, hand, cir);
        if(l instanceof PlayerEntity user && canUse(user, hand)){

            if(user instanceof ServerPlayerEntity p) {
                Vec3d rot = p.getRotationVector().normalize();
                double d = 2;
                Vec3d vf = rot.multiply(d);
//                Vec3d vel = p.getVelocity();
//                Vec3d vf = rot.multiply(rot.dotProduct(vel)/2+d); //projection of existing velocity vector onto rotation vector divided by 2 + d * rot
//                System.out.println("set velocity to " + vf);
                p.setVelocity(vf);
                p.velocityDirty = true;
                p.velocityModified = true;
            }
        }
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return AttributeModifiersComponent.builder().add(EntityAttributes.FALL_DAMAGE_MULTIPLIER,
                new EntityAttributeModifier(this.identifier, -1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                AttributeModifierSlot.OFFHAND).build();
    }

    @Override
    public AbstractMultiblockRecipe recipe() {

        return new ShapelessMultiblockRecipe(this.create())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addStack(
                        AbstractMultiblockRecipe.IngredientProvider.enchantedBook(Enchantments.WIND_BURST, 3), 2).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addStack(
                        AbstractMultiblockRecipe.IngredientProvider.enchantedBook(Enchantments.SWIFT_SNEAK, 3), 2).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.HEAVY_CORE, 2).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.BREEZE_ROD, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.COBWEB, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.AMETHYST_BLOCK, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.BLUE_ICE, 256).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.FEATHER, 256).build());
    }
}
