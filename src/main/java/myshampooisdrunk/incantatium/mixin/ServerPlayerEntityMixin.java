package myshampooisdrunk.incantatium.mixin;

import com.mojang.authlib.GameProfile;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.EnduranceEffect;
import myshampooisdrunk.incantatium.server.ServerChunkTickManager;
import myshampooisdrunk.incantatium.server.ServerChunkTickManagerInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

//    @Shadow public abstract ServerWorld getEntityWorld();
//
//    @Inject(method="onDeath",at=@At("HEAD"))
//    public void updateCache(DamageSource damageSource, CallbackInfo ci){
//        ServerChunkTickManager man = ((ServerChunkTickManagerInterface) getEntityWorld().getServer()).getServerChunkTickManager();
//        for(ServerChunkTickManager.ChunkTickManager chunk : man.getChunks()){
//            if(chunk.getPlayerCache().containsKey(this.getUuidAsString())){
//                chunk.untrackPlayer(this);
//            }
//        }
//    }

//    @Inject(method = "attack", at=@At("HEAD"), cancellable = true)
//    public void dontAttackIfEndurance(Entity target, CallbackInfo ci){
//        EnduranceEffect e = this.getComponent(Incantatium.ENDURANCE_COMPONENT_KEY);
//        if(e.getActive()) ci.cancel();
//    }

//    @Redirect(method="damage",at=@At(value = "INVOKE",target = "Lnet/minecraft/server/network/ServerPlayerEntity;isInvulnerableTo(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)Z"))
//    public boolean dontDamageIfEndurance(ServerPlayerEntity instance, ServerWorld world, DamageSource source){
//        EnduranceEffect e = instance.getComponent(Incantatium.ENDURANCE_COMPONENT_KEY);
//        if(e.getActive()) return true;
//        return instance.isInvulnerableTo(world, source);
//    }

    private ServerPlayerEntityMixin(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }
}
