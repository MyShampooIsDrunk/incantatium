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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    private final ServerPlayerEntity dis = (ServerPlayerEntity)(Object)this;

    @Inject(method="onDeath",at=@At("HEAD"))
    public void updateCache(DamageSource damageSource, CallbackInfo ci){
        ServerChunkTickManager man = ((ServerChunkTickManagerInterface)dis.getServerWorld().getServer()).getServerChunkTickManager();
        for(ServerChunkTickManager.ChunkTickManager chunk : man.getChunks()){
            if(chunk.getPlayerCache().containsKey(this.getUuidAsString())){
                chunk.untrackPlayer(this);
            }
        }
    }

    @Inject(method = "attack", at=@At("HEAD"), cancellable = true)
    public void dontAttackIfEndurance(Entity target, CallbackInfo ci){
        EnduranceEffect e = this.getComponent(Incantatium.ENDURANCE_COMPONENT_KEY);
        if(e.getActive()) ci.cancel();
    }

    @Redirect(method="damage",at=@At(value = "INVOKE",target = "Lnet/minecraft/server/network/ServerPlayerEntity;isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z"))
    public boolean dontDamageIfEndurance(ServerPlayerEntity instance, DamageSource damageSource){
        EnduranceEffect e = instance.getComponent(Incantatium.ENDURANCE_COMPONENT_KEY);
        return instance.isInvulnerableTo(damageSource) || e.getActive();
    }

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }
}
