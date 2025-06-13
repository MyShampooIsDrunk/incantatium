package myshampooisdrunk.incantatium.multiblock;

import io.netty.buffer.ByteBuf;
import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.component.MultiblockCoreData;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.mixin.MultiblockStructureAccessor;
import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntityGenerator;
import myshampooisdrunk.incantatium.multiblock.entity.RitualCoreEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.IntFunction;

public class RitualMultiblock extends MultiblockStructure {
    private static final double RT2 = Math.sqrt(2);

    private final Map<Integer, Vec3d> slotEntityOffsets;

    public RitualMultiblock(Identifier id) {
        super(id, Blocks.ENCHANTING_TABLE);
        this.slotEntityOffsets = new HashMap<>();
        addBlock(0,-1,0, Blocks.NETHERITE_BLOCK);
        addBlock(-1,-1,0, Blocks.ENDER_CHEST);
        addBlock(1,-1,0, Blocks.ENDER_CHEST);
        addBlock(0,-1,-1, Blocks.ENDER_CHEST);
        addBlock(0,-1,1, Blocks.ENDER_CHEST);
        addBlock(-2,-1,0, Blocks.BREWING_STAND);
        addBlock(2,-1,0, Blocks.BREWING_STAND);
        addBlock(0,-1,-2, Blocks.BREWING_STAND);
        addBlock(0,-1,2, Blocks.BREWING_STAND);
        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(i != 0 && j != 0) addBlock(i,-2,j, Blocks.ANCIENT_DEBRIS);
        addBlock(0,-2,0, Blocks.BARREL);
        for(int i = -2; i <= 2; i++)
            for(int j = -2; j<= 2; j++){
                int flag = -1;
                if(i == 0 && j == 0) flag = 1;
                else if(Math.abs(i) == 1 || Math.abs(j) == 1) flag = 2;
                else if(Math.abs(i) == 2 && Math.abs(j) == 2) flag = 3;
//                switch(flag):
            }
        setCore(new RitualCoreEntity("ritual_core"));
    }

    public void attachPedestalEntity(Vec3d relative, PedestalEntityGenerator.PedestalEntity p,
                                     PedestalEntityGenerator.PedestalEntityText t, int slot) {
        slotEntityOffsets.put(slot, relative);
        attachEntity(relative, p);
        attachEntity(relative, t);
    }

    public BlockPos centerFromPedestal(Vec3d pedestal, int slot) {
        Vec3d relative = slotEntityOffsets.get(slot);
        return BlockPos.ofFloored(pedestal.subtract(relative));
    }

    public Box getEntityBox() {
        return ((MultiblockStructureAccessor)this).getEntityBox();
    }

    public DisplayEntity.ItemDisplayEntity getRitualCoreEntity(ServerWorld world, BlockPos pos) {
        List<DisplayEntity.ItemDisplayEntity> entities = world.getEntitiesByType(
                EntityType.ITEM_DISPLAY,
                this.getEntityBox().offset(pos).expand(1),
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
