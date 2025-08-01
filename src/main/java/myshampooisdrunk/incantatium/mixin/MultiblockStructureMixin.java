package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.drunk_server_toolkit.multiblock.registry.MultiblockRegistry;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.multiblock.ShrineMultiblock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiblockStructure.class)
public abstract class MultiblockStructureMixin {
    @Inject(method = "remove",at=@At("HEAD"), cancellable = true)
    public void injectPrint(ServerWorld world, BlockPos pos, CallbackInfo ci){
        MultiblockRegistry.STRUCTURES.forEach((i,s) -> {
            //i shouldnt need this code but for some fucking reason inheritance breaks down here
            if (s instanceof ShrineMultiblock ss) {
                ss.remove(world,pos);
                ci.cancel();
            }
        });
    }
}
