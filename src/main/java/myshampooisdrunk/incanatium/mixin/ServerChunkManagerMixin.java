package myshampooisdrunk.incanatium.mixin;

import myshampooisdrunk.incanatium.util.TickHelper;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin extends ChunkManager {
	@Redirect(
			method = "tickChunks",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V")
	)
	private void shouldActuallyTickChunks(ServerWorld instance, WorldChunk chunk, int j) {
		if(TickHelper.shouldTick(instance.getServer(),chunk))instance.tickChunk(chunk, j);
	}
	@Redirect(
			method="tickChunks",
			at=@At(value="INVOKE",target = "Lnet/minecraft/world/chunk/WorldChunk;increaseInhabitedTime(J)V")
	)
	private void shouldActuallyTickChunks2(WorldChunk chunk, long l){
		World instance = chunk.getWorld();
		if(!instance.isClient()){
			if(TickHelper.shouldTick(chunk.getWorld().getServer(),chunk))chunk.increaseInhabitedTime(l);
		}

	}
}
