package myshampooisdrunk.incanatium.mixin;

import myshampooisdrunk.incanatium.server.ServerChunkTickManager;
import myshampooisdrunk.incanatium.server.ServerChunkTickManagerInterface;
import myshampooisdrunk.incanatium.util.TickHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {

    @Inject(method="tickEntity", at=@At("HEAD"), cancellable = true)
    public void shouldActuallyTickEntities(Entity entity, CallbackInfo ci) {
        if(!entity.getWorld().isClient()){
            if(entity instanceof PlayerEntity p){
                WorldChunk chunk = p.getWorld().getWorldChunk(p.getBlockPos());
                ServerChunkTickManager man = ((ServerChunkTickManagerInterface) Objects.requireNonNull(entity.getWorld().getServer())).getServerChunkTickManager();
                for(ServerChunkTickManager.ChunkTickManager i: man.getChunks()){
                    if(i.getPlayerCache().isEmpty() || !i.getPlayerCache().containsKey(p.getUuidAsString())) {
                        if (i.isFrozen() && i.getChunk().equals(chunk)) {
                            i.addPlayer(p);
                        }
                    }
                }
            }
        }
        if(!TickHelper.shouldTick(entity,entity.getWorld().getWorldChunk(entity.getBlockPos())))ci.cancel();
    }
    @Inject(method="tickBlock", at=@At("HEAD"), cancellable = true)
    public void shouldActuallyTickBlocks(BlockPos pos, Block block, CallbackInfo ci) {
        if(!TickHelper.shouldTick(((ServerWorld)(Object)this).getServer(), this.getWorldChunk(pos)))ci.cancel();
    }
    @Inject(method="tickFluid", at=@At("HEAD"), cancellable = true)
    public void shouldActuallyTickBlocks(BlockPos pos, Fluid fluid, CallbackInfo ci) {
        if(!TickHelper.shouldTick(((ServerWorld)(Object)this).getServer(), this.getWorldChunk(pos)))ci.cancel();
    }
    @Redirect(method="tick",at=@At(value="INVOKE",target="Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"))
    public void shouldActuallyTickBlockEntities(ServerWorld instance){
        List<BlockEntityTickInvoker> shouldRemove = new ArrayList<>();
        for(BlockEntityTickInvoker inv: ((PendingTickersAccessor)this).getPendingBlockEntityTickers()){
            if(!TickHelper.shouldTick(((ServerWorld)(Object)this).getServer(), this.getWorldChunk(inv.getPos())))
                shouldRemove.add(inv);
        }//inv.getPos()
        ((PendingTickersAccessor)this).getPendingBlockEntityTickers().removeAll(shouldRemove);
        this.tickBlockEntities();
    }


    @Inject(
            method="tick",
            at=@At("RETURN")
    )
    private void tickServerChunkTickManagers(CallbackInfo ci){
        ServerChunkTickManager man = ((ServerChunkTickManagerInterface)((ServerWorld)(Object)this).getServer()).getServerChunkTickManager();
        for(ServerChunkTickManager.ChunkTickManager tick : man.getChunks()){
            tick.tick();
            if(tick.getFrozenTicks() == 0){
                man.queueChunk(tick);
            }
        }
        man.untrackChunks(man.getQueue());
    }
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }
}
