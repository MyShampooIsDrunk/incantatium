package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CycloneOrnamentItem extends AbstractOrnamentItem{
    public CycloneOrnamentItem() {
        super(Incantatium.id("cyclone_ornament"), "Cyclone", 150); // 7.5 sec instead of 5
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
}
