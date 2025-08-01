package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.component.MultiblockData;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.registry.MultiblockRegistry;
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
        if(!getWorld().isClient()){
            String id;
            MultiblockData data = this.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY);
            if((id = data.getEntityId()) != null) {
                AbstractMultiblockStructureEntity<? extends Entity> structureEntity = MultiblockRegistry.ENTITY_TYPES.get(id).defaultEntity();
                structureEntity.onInteract(player, this, hand, cir);
            }
        }
    }

    @Inject(at=@At("HEAD"), method = "handleAttack", cancellable = true)
    public void injectMultiblockEntityHandleAttack(Entity attacker, CallbackInfoReturnable<Boolean> cir){
        if(!getWorld().isClient()) {
            String id;
            MultiblockData data = this.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY);
            if ((id = data.getEntityId()) != null) {
                AbstractMultiblockStructureEntity<? extends Entity> structureEntity = MultiblockRegistry.ENTITY_TYPES.get(id).defaultEntity();
                structureEntity.handleAttack(attacker, this, cir);
            }
        }
    }
}
