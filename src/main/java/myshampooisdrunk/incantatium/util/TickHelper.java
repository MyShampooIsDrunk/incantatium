package myshampooisdrunk.incantatium.util;

import myshampooisdrunk.incantatium.server.ServerChunkTickManager;
import myshampooisdrunk.incantatium.server.ServerChunkTickManagerInterface;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.world.chunk.WorldChunk;

public class TickHelper {
    public static boolean shouldTick(MinecraftServer server, WorldChunk chunk){
        if(server == null)return false;
        ServerChunkTickManager man = ((ServerChunkTickManagerInterface) server).getServerChunkTickManager();
        if(man.getChunks().isEmpty())return true;
        boolean shouldTick = true;
        for(ServerChunkTickManager.ChunkTickManager i: man.getChunks()){
            if(i.isFrozen() && i.getChunkSet().contains(chunk)){
                shouldTick = false;
                break;
            }
        }
        return shouldTick;
    }

    public static boolean shouldTick(Entity entity, WorldChunk chunk){
        if(entity instanceof ServerPlayerEntity p){
            if(p.interactionManager.getGameMode().equals(GameMode.CREATIVE) || p.interactionManager.getGameMode().equals(GameMode.SPECTATOR)){
                return true;
            }
        }
        if(entity.getEntityWorld().isClient())return true;
        MinecraftServer server = entity.getEntityWorld().getServer();
        if(server == null)return true;
        ServerChunkTickManager man = ((ServerChunkTickManagerInterface) server).getServerChunkTickManager();
        if(man.getChunks().isEmpty()) return true;
        boolean shouldTick = true;
        for(ServerChunkTickManager.ChunkTickManager i: man.getChunks()){
            if(i.getExempt().contains(entity)) break;

            if(i.isFrozen() && i.getChunkSet().contains(chunk)){
                shouldTick = false;
                break;
            }
        }
        return shouldTick;
    }
}
