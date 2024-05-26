package myshampooisdrunk.incanatium.mixin;

import myshampooisdrunk.incanatium.server.ServerChunkTickManager;
import myshampooisdrunk.incanatium.server.ServerChunkTickManagerInterface;
import myshampooisdrunk.incanatium.util.TickHelper;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler {
    @Redirect(
            method="tick",
            at=@At(value = "INVOKE",target = "Lnet/minecraft/server/network/ServerPlayerEntity;updatePositionAndAngles(DDDFF)V")
    )
    public void actuallyUpdatePositionAndAngles(ServerPlayerEntity player, double x, double y, double z, float yaw, float pitch){
        ServerChunkTickManager manager = ((ServerChunkTickManagerInterface) Objects.requireNonNull(player.getServer())).getServerChunkTickManager();
        boolean b = true;
        for(ServerChunkTickManager.ChunkTickManager man : manager.getChunks()){
            if(man.getPlayerCache().containsKey(player.getUuidAsString())){
                Pair<Vec3d, Vec2f> pair = man.getPlayerCache().get(player.getUuidAsString());
                player.updatePositionAndAngles(pair.getLeft().x,pair.getLeft().y,pair.getLeft().z,pair.getRight().x,pair.getRight().y);

                b = false;
            }
        }
        if(b || TickHelper.shouldTick(player,player.getWorld().getWorldChunk(player.getBlockPos()))){
            player.updatePositionAndAngles(x,y,z,yaw,pitch);
        }
        //this.lastTickX, this.lastTickY, this.lastTickZ, this.player.getYaw(), this.player.getPitch()
        //if(!) ;
    }

    @Inject(method="onPlayerInput",at=@At("HEAD"), cancellable = true)
    public void shouldOnPlayerInput(PlayerInputC2SPacket packet, CallbackInfo ci){
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).getPlayer();
        if(!TickHelper.shouldTick(player,player.getWorld().getWorldChunk(player.getBlockPos())))ci.cancel();
    }
    //onHandSwing, onClientCommand, onPlayerInteractEntity,
    @Inject(method="onPlayerInteractBlock",at=@At("HEAD"), cancellable = true)
    public void shouldOnPlayerInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo ci){
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).getPlayer();
        if(!TickHelper.shouldTick(player,player.getWorld().getWorldChunk(player.getBlockPos())))ci.cancel();
    }
    @Inject(method="onPlayerInteractItem",at=@At("HEAD"), cancellable = true)
    public void shouldOnPlayerInteractItem(PlayerInteractItemC2SPacket packet, CallbackInfo ci){
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).getPlayer();
        if(!TickHelper.shouldTick(player,player.getWorld().getWorldChunk(player.getBlockPos())))ci.cancel();
    }
    @Inject(method="onPlayerInteractEntity",at=@At("HEAD"), cancellable = true)
    public void shouldOnPlayerInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci){
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).getPlayer();
        if(!TickHelper.shouldTick(player,player.getWorld().getWorldChunk(player.getBlockPos())))ci.cancel();
    }

    public ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }
}
