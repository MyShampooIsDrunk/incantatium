package myshampooisdrunk.incantatium.world;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockCoreEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface IMultiblockView {
     default DisplayEntity.ItemDisplayEntity getMultiblockCoreEntity(BlockPos pos, BlockView view) {
         List<DisplayEntity.ItemDisplayEntity> entities = new ArrayList<>();
         if(view instanceof World world)
             entities = world.getEntitiesByType(EntityType.ITEM_DISPLAY,new Box(pos),
                 (t)->!t.getComponent(DST.MULTIBLOCK_CORE_DATA_COMPONENT_KEY).getBlockstateData().isEmpty());
         if(entities.isEmpty()) return null;
         entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(pos.toCenterPos())));
         return entities.getFirst();
     }
}
