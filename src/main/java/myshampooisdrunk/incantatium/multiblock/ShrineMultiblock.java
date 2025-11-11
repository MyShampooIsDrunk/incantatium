package myshampooisdrunk.incantatium.multiblock;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockCoreEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockDisplayEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructureType;
import myshampooisdrunk.drunk_server_toolkit.util.BlockUtil;
import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntity;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Predicate;

public class ShrineMultiblock extends MultiblockStructure {
    public static final double RT2 = Math.sqrt(2);
    public static final Vec3d[] offsets = PedestalEntity.offsets(2);

    private final PedestalEntry[] entries;

    public ShrineMultiblock(MultiblockStructureType<ShrineMultiblock> type, MultiblockCoreEntity core) {
        super(type, core);
        entries = new PedestalEntry[8];
    }

    public Optional<PedestalEntry> getEntry(int i) {
        PedestalEntry ret = null;
        if(i < 8 && i >= 0) {
            ret = entries[i];
        }
        return Optional.ofNullable(ret);
    }

    @Override
    public boolean spawnStructure(BlockPos pos) {
        boolean ret = super.spawnStructure(pos);
        if(ret) {
            MultiblockDisplayEntity display = new MultiblockDisplayEntity(IncantatiumMultiblockRegistry.ITEM_DISPLAY, world, null);
            display.set(IncantatiumRegistry.SHRINE_DISPLAY_ITEM.create(), null);
            display.getEntity().setTransformation(new AffineTransformation(new Vector3f(0,-2.5f,0), new Quaternionf(), new Vector3f(2,2,2), new Quaternionf()));
            MultiblockEntity.spawnEntity(display, this, pos, new Vec3d(0.5,1.5,0.5));

            for (int i = 0; i < offsets.length; i++) {
                PedestalEntity.PedestalItemEntity item = new PedestalEntity.PedestalItemEntity(IncantatiumMultiblockRegistry.PEDESTAL_ITEM,
                        this.world, i);

                PedestalEntity.PedestalTextEntity text = new PedestalEntity.PedestalTextEntity(IncantatiumMultiblockRegistry.PEDESTAL_TEXT,
                        this.world, i);

                PedestalEntity.PedestalInteractionEntity interaction = new PedestalEntity.PedestalInteractionEntity(IncantatiumMultiblockRegistry.PEDESTAL_INTERACTION,
                        this.world, i);

                MultiblockEntity.spawnEntity(item, this, pos, offsets[i]);
                MultiblockEntity.spawnEntity(text, this, pos, offsets[i]);
                MultiblockEntity.spawnEntity(interaction, this, pos, offsets[i]);

                PedestalEntry entry = new PedestalEntry(text, interaction, item);
                entries[i] = entry;
            }
        }
        return ret;
    }

    public static Template getTemplate() {
        char[][] layer1 = new char[][]{
                "n n n".toCharArray(),
                "  P  ".toCharArray(),
                "nPnPn".toCharArray(),
                "  P  ".toCharArray(),
                "n n n".toCharArray()
        };

        char[][] layer2 = new char[][]{
                "b b b".toCharArray(),
                "  p  ".toCharArray(),
                "bpnpb".toCharArray(),
                "  p  ".toCharArray(),
                "b b b".toCharArray()
        };

        char[][] layer3 = new char[][]{
                "     ".toCharArray(),
                "     ".toCharArray(),
                "  e  ".toCharArray(),
                "     ".toCharArray(),
                "     ".toCharArray()
        };
        TemplateBuilder builder = Template.builder();

        Predicate<BlockState> n = BlockUtil.simpleLookup(Blocks.NETHERITE_BLOCK);
        Predicate<BlockState> b = BlockUtil.simpleLookup(Blocks.BREWING_STAND);
        Predicate<BlockState> p = BlockUtil.simpleLookup(Blocks.PURPUR_STAIRS);
        Predicate<BlockState> P = BlockUtil.simpleLookup(Blocks.PURPUR_PILLAR);

        builder.add(0,-1,0, 'n', n);
        builder.add(0,-2,0, 'n', n);

        builder.add(0,-2,-2, 'n', n);
        builder.add(0,-2,2, 'n', n);
        builder.add(-2,-2,0, 'n', n);
        builder.add(2,-2,0, 'n', n);
        builder.add(-2,-2,-2, 'n', n);
        builder.add(2,-2,2, 'n', n);
        builder.add(2,-2,-2, 'n', n);
        builder.add(-2,-2,2, 'n', n);

        builder.add(-1,-2,0, 'P', P);
        builder.add(1,-2,0, 'P', P);
        builder.add(0,-2,-1, 'P', P);
        builder.add(0,-2,1, 'P', P);

        builder.add(-1,-1,0, 'p', p);
        builder.add(1,-1,0, 'p', p);
        builder.add(0,-1,-1, 'p', p);
        builder.add(0,-1,1, 'p', p);

        builder.add(0,-1,-2, 'b', b);
        builder.add(0,-1,2, 'b', b);
        builder.add(-2,-1,0, 'b', b);
        builder.add(2,-1,0, 'b', b);
        builder.add(-2,-1,-2, 'b', b);
        builder.add(2,-1,2, 'b', b);
        builder.add(2,-1,-2, 'b', b);
        builder.add(-2,-1,2, 'b', b);

        return builder.build();
    }

    public void setEntry(int i, PedestalEntry entry) {
        if(i < 8 && i >= 0) {
            entries[i] = entry;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    //    private final Map<AbstractMultiblockStructureEntity<?>, Vec3d> entityList;

//    public ShrineMultiblock(Identifier id) {
//        super(id, Set.of(Blocks.ENCHANTING_TABLE));
//        entityList = new HashMap<>();
//        this.slotEntityOffsets = new HashMap<>();
//
//        //temp
//        addBlock(0,-1,0, Blocks.NETHERITE_BLOCK);
//        addBlock(0,-2,0, Blocks.NETHERITE_BLOCK);
//        addBlock(0,-3,0, Blocks.NETHERITE_BLOCK);
//
//        //initialize blocks VVV
////        addBlock(0,-1,0, Blocks.NETHERITE_BLOCK);
////        addBlock(-1,-1,0, Blocks.ENDER_CHEST);
////        addBlock(1,-1,0, Blocks.ENDER_CHEST);
////        addBlock(0,-1,-1, Blocks.ENDER_CHEST);
////        addBlock(0,-1,1, Blocks.ENDER_CHEST);
////        addBlock(-2,-1,0, Blocks.BREWING_STAND);
////        addBlock(2,-1,0, Blocks.BREWING_STAND);
////        addBlock(0,-1,-2, Blocks.BREWING_STAND);
////        addBlock(0,-1,2, Blocks.BREWING_STAND);
////        for(int i = -1; i <= 1; i++)
////            for(int j = -1; j <= 1; j++)
////                if(i != 0 && j != 0) addBlock(i,-2,j, Blocks.ANCIENT_DEBRIS);
////        addBlock(0,-2,0, Blocks.BARREL);
////        for(int i = -2; i <= 2; i++)
////            for(int j = -2; j<= 2; j++){
////                int flag = -1;
////                if(i == 0 && j == 0) flag = 1;
////                else if(Math.abs(i) == 1 || Math.abs(j) == 1) flag = 2;
////                else if(Math.abs(i) == 2 && Math.abs(j) == 2) flag = 3;
//////                switch(flag):
////            }
//        setCore(new ShrineCoreEntity("ritual_core"));
//        PedestalEntityGenerator.attach(2,this);
//    }
//
//    @Override
//    public void remove(ServerWorld world, BlockPos pos) {
//        List<? extends EntityType<?>> types = entityList.keySet().stream()
//                .map(AbstractMultiblockStructureEntity::getType).toList();
//
//        List<Entity> entities = new ArrayList<>();
//        UUID coreUUID = this.getCoreEntityUUID(world, pos);
//        if (coreUUID == null) return;
//        types.forEach(t -> entities.addAll(world.getEntitiesByType(
//                t,
//                entityBox.offset(pos).expand(1),
//                e -> coreUUID.equals(e.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY).getUUID())))
//        );
//        for (Entity e : entities) {
//            if(e instanceof DisplayEntity.ItemDisplayEntity core) {
//                MultiblockCoreData coreData = core.getComponent(DST.MULTIBLOCK_CORE_DATA_COMPONENT_KEY);
//                coreData.getBlockstateData().forEach(world::setBlockState);
//            }
//            e.remove(Entity.RemovalReason.DISCARDED);
//        }
//    }
//
//    @Override
//    public MultiblockStructure attachEntity(Vec3d relative, AbstractMultiblockStructureEntity<?> entity) {
//        super.attachEntity(relative, entity);
//        entityList.put(entity, relative);
//        return this;
//    }
//
//    public void attachPedestalEntities(Vec3d relative, PedestalEntityGenerator.PedestalEntity p,
//                                       PedestalEntityGenerator.PedestalEntityText t, PedestalEntityGenerator.PedestalEntityInteraction i, int slot) {
//        slotEntityOffsets.put(slot, relative);
//        attachEntity(relative.add(0,0.5,0), p);
//        attachEntity(relative.add(0,1,0), t);
//        attachEntity(relative.subtract(0,1.5,0), i);
//    }
//
//    public BlockPos centerFromPedestal(Vec3d pedestal, int slot) {
//        Vec3d relative = slotEntityOffsets.get(slot);
//        return BlockPos.ofFloored(pedestal.subtract(relative));
//    }
//
//    public DisplayEntity.ItemDisplayEntity getPedestalEntity(ServerWorld world, int slot, DisplayEntity.ItemDisplayEntity core) {
//        MultiblockData coreData = core.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY);
//        Vec3d offset = slotEntityOffsets.get(slot);
//        Vec3d pos = core.getPos().add(offset);
//        List<DisplayEntity.ItemDisplayEntity> entities = world.getEntitiesByType(
//                EntityType.ITEM_DISPLAY,
//                Box.of(pos,1,1,1).expand(1),
//                e -> {
//                    if(e instanceof DisplayEntity.ItemDisplayEntity pedestal) {
//                        PedestalDisplay pedestalData = pedestal.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
//                        return pedestalData.getSlot() == slot && pedestal.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY).getUUID().equals(coreData.getUUID());
//                    }
//                    return false;
//                });
//        entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(pos)));
//        if(entities.isEmpty()) return null;
//        return entities.getFirst();
//    }
//
//    public DisplayEntity.ItemDisplayEntity getRitualCoreEntity(ServerWorld world, BlockPos pos) {
//        List<DisplayEntity.ItemDisplayEntity> entities = world.getEntitiesByType(
//                EntityType.ITEM_DISPLAY,
//                this.entityBox.offset(pos).expand(1),
//                e -> {
//                    if(e instanceof DisplayEntity.ItemDisplayEntity core) {
//                        MultiblockCoreData coreData = core.getComponent(DST.MULTIBLOCK_CORE_DATA_COMPONENT_KEY);
//                        return !coreData.getBlockstateData().isEmpty();
//                    }
//                    return false;
//                });
//        entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(pos.toCenterPos())));
//        if(entities.isEmpty()) return null;
//        return entities.getFirst();
//    }
    public record PedestalEntry(PedestalEntity.PedestalTextEntity textEntity, PedestalEntity.PedestalInteractionEntity interactionEntity,
                                PedestalEntity.PedestalItemEntity itemEntity) {

        public PedestalEntry withText(@NotNull PedestalEntity.PedestalTextEntity text) {
            if(this.textEntity != null) return this;
            else return new PedestalEntry(text, interactionEntity, itemEntity);
        }

        public PedestalEntry withInteraction(@NotNull PedestalEntity.PedestalInteractionEntity interaction) {
            if(this.interactionEntity != null) return this;
            else return new PedestalEntry(textEntity, interaction, itemEntity);
        }

        public PedestalEntry withItem(@NotNull PedestalEntity.PedestalItemEntity item) {
            if(this.itemEntity != null) return this;
            else return new PedestalEntry(textEntity, interactionEntity, item);
        }

        public boolean isEmpty() {
            return textEntity == null && interactionEntity == null && itemEntity == null;
        }
    }
}
