package myshampooisdrunk.incantatium.multiblock;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.component.MultiblockCoreData;
import myshampooisdrunk.drunk_server_toolkit.component.MultiblockData;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.IncantatiumComponentRegistry;
import myshampooisdrunk.incantatium.component.PedestalDisplay;
import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntityGenerator;
import myshampooisdrunk.incantatium.multiblock.entity.ShrineCoreEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShrineMultiblock extends MultiblockStructure {
    public static final double RT2 = Math.sqrt(2);

    private final Map<Integer, Vec3d> slotEntityOffsets;
    private final Map<AbstractMultiblockStructureEntity<?>, Vec3d> entityList;

    public ShrineMultiblock(Identifier id) {
        super(id, Set.of(Blocks.ENCHANTING_TABLE));
        entityList = new HashMap<>();
        this.slotEntityOffsets = new HashMap<>();

        //temp
        addBlock(0,-1,0, Blocks.NETHERITE_BLOCK);
        addBlock(0,-2,0, Blocks.NETHERITE_BLOCK);
        addBlock(0,-3,0, Blocks.NETHERITE_BLOCK);

        //initialize blocks VVV
//        addBlock(0,-1,0, Blocks.NETHERITE_BLOCK);
//        addBlock(-1,-1,0, Blocks.ENDER_CHEST);
//        addBlock(1,-1,0, Blocks.ENDER_CHEST);
//        addBlock(0,-1,-1, Blocks.ENDER_CHEST);
//        addBlock(0,-1,1, Blocks.ENDER_CHEST);
//        addBlock(-2,-1,0, Blocks.BREWING_STAND);
//        addBlock(2,-1,0, Blocks.BREWING_STAND);
//        addBlock(0,-1,-2, Blocks.BREWING_STAND);
//        addBlock(0,-1,2, Blocks.BREWING_STAND);
//        for(int i = -1; i <= 1; i++)
//            for(int j = -1; j <= 1; j++)
//                if(i != 0 && j != 0) addBlock(i,-2,j, Blocks.ANCIENT_DEBRIS);
//        addBlock(0,-2,0, Blocks.BARREL);
//        for(int i = -2; i <= 2; i++)
//            for(int j = -2; j<= 2; j++){
//                int flag = -1;
//                if(i == 0 && j == 0) flag = 1;
//                else if(Math.abs(i) == 1 || Math.abs(j) == 1) flag = 2;
//                else if(Math.abs(i) == 2 && Math.abs(j) == 2) flag = 3;
////                switch(flag):
//            }
        setCore(new ShrineCoreEntity("ritual_core"));
        PedestalEntityGenerator.attach(2,this);
    }

    @Override
    public void remove(ServerWorld world, BlockPos pos) {
        List<? extends EntityType<?>> types = entityList.keySet().stream()
                .map(AbstractMultiblockStructureEntity::getType).toList();

        List<Entity> entities = new ArrayList<>();
        UUID coreUUID = this.getCoreEntityUUID(world, pos);
        if (coreUUID == null) return;
        types.forEach(t -> entities.addAll(world.getEntitiesByType(
                t,
                entityBox.offset(pos).expand(1),
                e -> coreUUID.equals(e.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY).getUUID())))
        );
        for (Entity e : entities) {
            if(e instanceof DisplayEntity.ItemDisplayEntity core) {
                MultiblockCoreData coreData = core.getComponent(DST.MULTIBLOCK_CORE_DATA_COMPONENT_KEY);
                coreData.getBlockstateData().forEach(world::setBlockState);
            }
            e.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @Override
    public MultiblockStructure attachEntity(Vec3d relative, AbstractMultiblockStructureEntity<?> entity) {
        super.attachEntity(relative, entity);
        entityList.put(entity, relative);
        return this;
    }

    public void attachPedestalEntities(Vec3d relative, PedestalEntityGenerator.PedestalEntity p,
                                       PedestalEntityGenerator.PedestalEntityText t, PedestalEntityGenerator.PedestalEntityInteraction i, int slot) {
        slotEntityOffsets.put(slot, relative);
        attachEntity(relative.add(0,0.5,0), p);
        attachEntity(relative.add(0,1,0), t);
        attachEntity(relative.subtract(0,1.5,0), i);
    }

    public BlockPos centerFromPedestal(Vec3d pedestal, int slot) {
        Vec3d relative = slotEntityOffsets.get(slot);
        return BlockPos.ofFloored(pedestal.subtract(relative));
    }

    public DisplayEntity.ItemDisplayEntity getPedestalEntity(ServerWorld world, int slot, DisplayEntity.ItemDisplayEntity core) {
        MultiblockData coreData = core.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY);
        Vec3d offset = slotEntityOffsets.get(slot);
        Vec3d pos = core.getPos().add(offset);
        List<DisplayEntity.ItemDisplayEntity> entities = world.getEntitiesByType(
                EntityType.ITEM_DISPLAY,
                Box.of(pos,1,1,1).expand(1),
                e -> {
                    if(e instanceof DisplayEntity.ItemDisplayEntity pedestal) {
                        PedestalDisplay pedestalData = pedestal.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
                        return pedestalData.getSlot() == slot && pedestal.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY).getUUID().equals(coreData.getUUID());
                    }
                    return false;
                });
        entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(pos)));
        if(entities.isEmpty()) return null;
        return entities.getFirst();
    }

    public DisplayEntity.ItemDisplayEntity getRitualCoreEntity(ServerWorld world, BlockPos pos) {
        List<DisplayEntity.ItemDisplayEntity> entities = world.getEntitiesByType(
                EntityType.ITEM_DISPLAY,
                this.entityBox.offset(pos).expand(1),
                e -> {
                    if(e instanceof DisplayEntity.ItemDisplayEntity core) {
                        MultiblockCoreData coreData = core.getComponent(DST.MULTIBLOCK_CORE_DATA_COMPONENT_KEY);
                        return !coreData.getBlockstateData().isEmpty();
                    }
                    return false;
                });
        entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(pos.toCenterPos())));
        if(entities.isEmpty()) return null;
        return entities.getFirst();
    }

    public enum Direction implements StringIdentifiable {

        NORTH(0,-1,"north",0),EAST(1,0,"east",2),SOUTH(0,1,"south",4),WEST(-1,0,"west",6),
        NORTHEAST(1,-1,"northeast",1),NORTHWEST(-1,-1,"northwest",7),SOUTHEAST(1,1,"southeast",3),
        SOUTHWEST(-1,1,"southwest",5);

        public static final StringIdentifiable.EnumCodec<Direction> CODEC = StringIdentifiable.createCodec(Direction::values);
        public static final Direction[] ALL = values();
        private static final Direction[] VALUES = Arrays.stream(ALL).sorted(Comparator.comparingInt(direction -> direction.id)).toArray(Direction[]::new);

        private final Vec2f vec;
        private final String name;
        private final int id;

        public static Direction get(int id) {
            return VALUES[Math.abs(id % 8)];
        }

        @Nullable
        public static Direction get(@Nullable String name) {
            return CODEC.byId(name);
        }

        Direction(int x, int z, String name, int id){
            if(Math.abs(x) + Math.abs(z) == 2) this.vec = new Vec2f(x/(float)RT2, z/(float)RT2);
            else this.vec = new Vec2f(x,z);
            this.name = name;
            this.id = id;
        }

        public float getX() {
            return vec.x;
        }

        public float getZ() {
            return vec.y;
        }

        public Vec2f getVec() {
            return vec;
        }

        public int getId() {
            return id;
        }

        @Override
        public String asString() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
