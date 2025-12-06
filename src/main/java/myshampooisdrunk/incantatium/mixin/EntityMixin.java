package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private World world;

    @Shadow public boolean horizontalCollision;

    @Shadow public abstract Box getBoundingBox();

    @Shadow public abstract boolean hasControllingPassenger();

    @Shadow public abstract boolean hasVehicle();

    @Shadow public abstract @Nullable Entity getVehicle();

    private final Entity dis = (Entity) (Object) this;

    @Inject(method = "move", at = @At("RETURN"))
    public void injectCollision2(MovementType type, Vec3d movement, CallbackInfo ci) {
        if(!this.world.isClient() && this.world instanceof ServerWorld sw) {
            if(this.hasVehicle() && this.getVehicle() instanceof LivingEntity living) {
                Box box = living.getBoundingBox().expand(0.3,0.8,0.3).offset(0,0.8,0);
                if (living.hasStackEquipped(EquipmentSlot.BODY) && living.getEquippedStack(EquipmentSlot.BODY).getEnchantments().getEnchantments()
                        .contains(this.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(IncantatiumRegistry.RAVAGING))
                        && !sw.isSpaceEmpty(box)) {
                    boolean bl = false;

                    for (BlockPos blockPos : BlockPos.iterate(box)) {
                        BlockState blockState = sw.getBlockState(blockPos);
                        Block block = blockState.getBlock();
                        if (block instanceof LeavesBlock && !blockState.get(Properties.PERSISTENT)) {
                            bl = sw.breakBlock(blockPos, true, living) || bl;
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "getProjectileDeflection", at=@At("HEAD"), cancellable = true)
    public void cycloneModifyProjectileDeflection(ProjectileEntity projectileEntity, CallbackInfoReturnable<ProjectileDeflection> cir){
        if(dis instanceof PlayerEntity p && !world.isClient() && world instanceof ServerWorld sWorld){
            if(p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY).isActive(IncantatiumRegistry.CYCLONE_ORNAMENT.getIdentifier()))
                cir.setReturnValue((projectile, hitEntity, random) -> {
                    //piercing 7 to go through shield + cyclone or pierce 4 to go through just cyclone
                    //in no deflect tag --> no deflect
                    if(!(projectileEntity.getType().isIn(Incantatium.CYCLONE_NO_DEFLECT)) && (!(projectile instanceof ArrowEntity a) || ((a.getPierceLevel() <= 3 && !p.isBlocking()) || a.getPierceLevel() <= 6))) {
                        sWorld.playSoundFromEntity(p, hitEntity, SoundEvents.ITEM_TRIDENT_THUNDER.value(), SoundCategory.PLAYERS, 1F, 2F);
                        float f = 170.0F + random.nextFloat() * 20.0F;
                        projectile.setVelocity(projectile.getVelocity().multiply(-1.2));
                        projectile.setYaw(projectile.getYaw() + f);
                        projectile.lastYaw += f;
                        projectile.velocityDirty = true;
                    }
                });
        }
    }
}
