package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.component.MultiblockData;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.registry.MultiblockRegistry;
import myshampooisdrunk.incantatium.multiblock.entity.SolidHitboxGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerEntity.class)
public abstract class ShulkerEntityMixin extends GolemEntity{

//    @Redirect(method = "setPosition",at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ShulkerEntity;hasVehicle()Z", ordinal = 0))
//    public boolean modifyHasVehicle(ShulkerEntity instance){
//        if(!instance.getWorld().isClient()) {
//            String id;
//            if(this.getAttributes() != null && this.brain != null && this.writeNbt(new NbtCompound()).contains("cardinal_components")) {
//                MultiblockData data = this.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY);
//                if ((id = data.getEntityId()) != null) {
//                    AbstractMultiblockStructureEntity<? extends Entity> structureEntity = MultiblockRegistry.ENTITY_TYPES.get(id).defaultEntity();
//                    if (structureEntity instanceof SolidHitboxGenerator.SolidHitboxEntity s) {
//                        return true;
//                    }
//                }
//            }
//            return this.hasVehicle();
//        }
//        return this.hasVehicle();
//    }

    public ShulkerEntityMixin(EntityType<? extends GolemEntity> type, World world) {
        super(type, world);
    }
}
