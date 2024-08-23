package myshampooisdrunk.incantatium.mixin;

import com.mojang.datafixers.DataFixer;
import myshampooisdrunk.incantatium.server.ServerChunkTickManager;
import myshampooisdrunk.incantatium.server.ServerChunkTickManagerInterface;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.ServerTask;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(value = MinecraftServer.class,priority = Integer.MAX_VALUE-10)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements ServerChunkTickManagerInterface {

    private ServerChunkTickManager serverChunkTickManager;

    public MinecraftServerMixin(String string) {
        super(string);
    }

    @Inject(at=@At("RETURN"), method="<init>")
    private void onInit(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci){
        serverChunkTickManager = new ServerChunkTickManager((MinecraftServer)(Object)this);
    }
    @Inject(at=@At("HEAD"),method="tick")
    private void tickServerChunkTickManagers(CallbackInfo ci){
        ServerChunkTickManager man = getServerChunkTickManager();
        for(ServerChunkTickManager.ChunkTickManager tick : man.getChunks()){
            tick.tick();
            if(tick.getFrozenTicks() == 0){
                man.queueChunk(tick);
            }
        }
        man.untrackChunks(man.getQueue());
    }
    @Override
    public ServerChunkTickManager getServerChunkTickManager(){
        return serverChunkTickManager;
    }
}
