//package myshampooisdrunk.incantatium.mixin;
//
//import myshampooisdrunk.incantatium.util.TickHelper;
//import net.minecraft.entity.SpawnGroup;
//import net.minecraft.server.world.ChunkHolder;
//import net.minecraft.server.world.ServerChunkLoadingManager;
//import net.minecraft.server.world.ServerChunkManager;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.world.SpawnHelper;
//import net.minecraft.world.World;
//import net.minecraft.world.chunk.ChunkManager;
//import net.minecraft.world.chunk.WorldChunk;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//
//import java.util.List;
//import java.util.function.Consumer;
//
//@Mixin(ServerChunkManager.class)
//public abstract class ServerChunkManagerMixin extends ChunkManager {
//	@Shadow protected abstract void tickSpawningChunk(WorldChunk chunk, long timeDelta, List<SpawnGroup> spawnableGroups, SpawnHelper.Info info);
//
//	@Shadow @Final private ServerWorld world;
//
//	@Redirect(
//			method = "tickChunks(Lnet/minecraft/util/profiler/Profiler;J)V",
//			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkLoadingManager;forEachBlockTickingChunk(Ljava/util/function/Consumer;)V")
//	)
//	private void shouldActuallyTickChunks(ServerChunkLoadingManager instance, Consumer<WorldChunk> chunkConsumer) {
//		instance.getLevelManager().forEachBlockTickingChunk(chunkPos -> {
//			ChunkHolder chunkHolder = instance.getCurrentChunkHolder(chunkPos);
//			if (chunkHolder != null) {
//				WorldChunk worldChunk = chunkHolder.getWorldChunk();
//				if (worldChunk != null && TickHelper.shouldTick(world.getServer(), worldChunk)) {
//					chunkConsumer.accept(worldChunk);
//				}
//			}
//		});
//	}
//
//	@Redirect(
//			method="tickChunks(Lnet/minecraft/util/profiler/Profiler;J)V",
//			at=@At(value="INVOKE",target = "Lnet/minecraft/server/world/ServerChunkManager;tickSpawningChunk(Lnet/minecraft/world/chunk/WorldChunk;JLjava/util/List;Lnet/minecraft/world/SpawnHelper$Info;)V")
//	)
//	private void shouldActuallyTickChunks2(ServerChunkManager instance, WorldChunk chunk, long timeDelta, List<SpawnGroup> spawnableGroups, SpawnHelper.Info info){
//		if(TickHelper.shouldTick(chunk.getWorld().getServer(),chunk)) tickSpawningChunk(chunk, timeDelta, spawnableGroups, info);
//	}
//}
