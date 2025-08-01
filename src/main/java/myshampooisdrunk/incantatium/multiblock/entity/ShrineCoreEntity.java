package myshampooisdrunk.incantatium.multiblock.entity;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockCoreEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static myshampooisdrunk.incantatium.registry.IncantatiumRegistry.SHRINE_DISPLAY_ITEM;

public class ShrineCoreEntity extends MultiblockCoreEntity {
    public ShrineCoreEntity(String id) {
        super(id);//I need to figure out what item ima use for ts
    }

    @Override
    public DisplayEntity.ItemDisplayEntity create(ServerWorld world, MultiblockStructure structure, BlockPos center, Vec3d relative) {
        DisplayEntity.ItemDisplayEntity ret = super.create(world, structure, center, relative);
        ret.setTransformation(new AffineTransformation(new Vector3f(0,-2.5f,0), new Quaternionf(), new Vector3f(2,2,2), new Quaternionf()));
        ret.setItemStack(SHRINE_DISPLAY_ITEM.create());
//        ret.getComponent(Incantatium.INVENTORY_STORAGE_COMPONENT_KEY);
        return ret;
        //TODO: inventory stuff
    }
}
