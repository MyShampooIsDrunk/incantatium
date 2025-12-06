package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.incantatium.util.TickHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
//    @Shadow @Final protected ServerPlayerEntity player;
//
//    @Inject(method="update",at=@At("HEAD"), cancellable = true)
//    public void shouldActuallyUpdate(CallbackInfo ci){
//        if(!TickHelper.shouldTick(this.player,player.getEntityWorld().getWorldChunk(player.getBlockPos()))) ci.cancel();
//    }
//
//    @Inject(method="processBlockBreakingAction",at=@At("HEAD"),cancellable = true)
//    public void actuallyProcessBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci){
//        if(!TickHelper.shouldTick(this.player,player.getEntityWorld().getWorldChunk(player.getBlockPos()))) ci.cancel();
//    }
//
//    @Inject(method="interactBlock",at=@At("HEAD"),cancellable = true)
//    public void actuallyInteractBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
//        if(!TickHelper.shouldTick(this.player,player.getEntityWorld().getWorldChunk(player.getBlockPos()))) cir.setReturnValue(ActionResult.FAIL);
//    }
//
//    @Inject(method="interactItem",at=@At("HEAD"),cancellable = true)
//    public void actuallyInteractItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir){
//        if(!TickHelper.shouldTick(this.player,player.getEntityWorld().getWorldChunk(player.getBlockPos()))) cir.setReturnValue(ActionResult.FAIL);
//    }

}
