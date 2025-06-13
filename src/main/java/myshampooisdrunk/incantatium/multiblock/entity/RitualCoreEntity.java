package myshampooisdrunk.incantatium.multiblock.entity;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockCoreEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RitualCoreEntity extends MultiblockCoreEntity {
    public RitualCoreEntity(String id) {
        super(id);//I need to figure out what item ima use for ts
    }

    @Override
    public DisplayEntity.ItemDisplayEntity create(ServerWorld world, MultiblockStructure structure, BlockPos center, Vec3d relative) {
        DisplayEntity.ItemDisplayEntity ret = super.create(world, structure, center, relative);
//        ret.getComponent(Incantatium.INVENTORY_STORAGE_COMPONENT_KEY);
        return ret;
        //TODO: inventory stuff
    }
}
