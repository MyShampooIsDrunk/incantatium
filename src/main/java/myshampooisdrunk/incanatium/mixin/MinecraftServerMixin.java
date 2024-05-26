package myshampooisdrunk.incanatium.mixin;

import com.mojang.datafixers.DataFixer;
import myshampooisdrunk.incanatium.server.ServerChunkTickManager;
import myshampooisdrunk.incanatium.server.ServerChunkTickManagerInterface;
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
    @Override
    public ServerChunkTickManager getServerChunkTickManager(){
        return serverChunkTickManager;
    }
}
