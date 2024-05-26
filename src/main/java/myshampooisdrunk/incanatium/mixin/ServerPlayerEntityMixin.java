package myshampooisdrunk.incanatium.mixin;

import com.mojang.authlib.GameProfile;
import myshampooisdrunk.incanatium.server.ServerChunkTickManager;
import myshampooisdrunk.incanatium.server.ServerChunkTickManagerInterface;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Inject(method="onDeath",at=@At("HEAD"))
    public void updateCache(DamageSource damageSource, CallbackInfo ci){
        ServerChunkTickManager man = ((ServerChunkTickManagerInterface)((ServerPlayerEntity)(Object)this).getServerWorld().getServer()).getServerChunkTickManager();
        for(ServerChunkTickManager.ChunkTickManager chunk : man.getChunks()){
            if(chunk.getPlayerCache().containsKey(this.getUuidAsString())){
                chunk.untrackPlayer(this);
            }
        }
    }

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }
}
