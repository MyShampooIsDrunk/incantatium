package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.drunk_server_toolkit.world.MultiblockCacheI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InteractionEntity.class)
public abstract class InteractionEntityMixin extends Entity {

    public InteractionEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at=@At("HEAD"), method = "interact", cancellable = true)
    public void injectMultiblockEntityInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if(!getEntityWorld().isClient()){
            MultiblockEntity<?,?> s = ((MultiblockCacheI) getEntityWorld()).drunk_server_toolkit$getMultiblockEntity(this.getUuid());
            if(s != null) s.onInteract(player, hand, cir);
        }
    }

    @Inject(at=@At("HEAD"), method = "handleAttack", cancellable = true)
    public void injectMultiblockEntityHandleAttack(Entity attacker, CallbackInfoReturnable<Boolean> cir){
        if(!getEntityWorld().isClient()) {
            MultiblockEntity<?,?> s = ((MultiblockCacheI) getEntityWorld()).drunk_server_toolkit$getMultiblockEntity(this.getUuid());
            if(s != null) s.handleAttack(attacker, cir);
        }
    }
}
