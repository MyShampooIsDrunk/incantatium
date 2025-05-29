package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class EntityMixin {

    private final Entity dis = (Entity) (Object) this;

    @Inject(method = "getProjectileDeflection", at=@At("HEAD"), cancellable = true)
    public void cycloneModifyProjectileDeflection(ProjectileEntity projectileEntity, CallbackInfoReturnable<ProjectileDeflection> cir){
        if(dis instanceof PlayerEntity p && !p.getWorld().isClient() && p.getWorld() instanceof ServerWorld sWorld){
            if(p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY).isActive(IncantatiumRegistry.CYCLONE_ORNAMENT.getIdentifier()))
                cir.setReturnValue((projectile, hitEntity, random) -> {
                    sWorld.playSoundFromEntity(p, hitEntity, SoundEvents.ITEM_TRIDENT_THUNDER.value(), SoundCategory.PLAYERS, 1F, 2F);
                    //piercing 7 to go through shield + cyclone or pierce 4 to go through just cyclone
                    if(projectile instanceof ArrowEntity a && (a.getPierceLevel() <= 3 || (p.isBlocking() && a.getPierceLevel() <= 6))) ProjectileDeflection.SIMPLE.deflect(a, hitEntity, random);
                    else {
                        float f = 170.0F + random.nextFloat() * 20.0F;
                        projectile.setVelocity(projectile.getVelocity().multiply(-1.2));
                        projectile.setYaw(projectile.getYaw() + f);
                        projectile.prevYaw += f;
                        projectile.velocityDirty = true;
                    }
                });
        }
    }
}
