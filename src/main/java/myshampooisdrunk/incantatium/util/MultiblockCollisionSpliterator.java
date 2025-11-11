//package myshampooisdrunk.incantatium.util;
//
//import com.google.common.collect.AbstractIterator;
//import myshampooisdrunk.drunk_server_toolkit.DST;
//import myshampooisdrunk.drunk_server_toolkit.component.MultiblockData;
//import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
//import myshampooisdrunk.drunk_server_toolkit.multiblock.registry.MultiblockRegistry;
//import myshampooisdrunk.incantatium.mixin.CollisionViewMixin;
//import myshampooisdrunk.incantatium.multiblock.AbstractMultiblockHitboxStructure;
//import myshampooisdrunk.incantatium.world.IMultiblockView;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.block.ShapeContext;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.decoration.DisplayEntity;
//import net.minecraft.util.CuboidBlockIterator;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.function.BooleanBiFunction;
//import net.minecraft.util.math.*;
//import net.minecraft.util.shape.VoxelShape;
//import net.minecraft.util.shape.VoxelShapes;
//import net.minecraft.world.BlockView;
//import net.minecraft.world.CollisionView;
//import net.minecraft.world.World;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.function.BiFunction;
//
//public class MultiblockCollisionSpliterator<T> extends AbstractIterator<T> {
//    private final Box box;
//    private final ShapeContext context;
//    private final CuboidBlockIterator blockIterator;
//    private final BlockPos.Mutable pos;
//    private final VoxelShape boxShape;
//    private final CollisionView world;
//    private final boolean forEntity;
//    @Nullable
//    private BlockView chunk;
//    private long chunkPos;
//    private final BiFunction<BlockPos.Mutable, VoxelShape, T> resultFunction;
//
//    public MultiblockCollisionSpliterator(
//            CollisionView world, @Nullable Entity entity, Box box, boolean forEntity, BiFunction<BlockPos.Mutable, VoxelShape, T> resultFunction
//    ) {
//        this(world, entity == null ? ShapeContext.absent() : ShapeContext.of(entity), box, forEntity, resultFunction);
//    }
//
//    public MultiblockCollisionSpliterator(
//            CollisionView world, ShapeContext context, Box box, boolean forEntity, BiFunction<BlockPos.Mutable, VoxelShape, T> resultFunction
//    ) {
//        this.context = context;
//        this.pos = new BlockPos.Mutable();
//        this.boxShape = VoxelShapes.cuboid(box);
//        this.world = world;
//        this.box = box;
//        this.forEntity = forEntity;
//        this.resultFunction = resultFunction;
//        int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
//        int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
//        int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
//        int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
//        int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
//        int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
//        this.blockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
//    }
//
//    @Nullable
//    private BlockView getChunk(int x, int z) {
//        int i = ChunkSectionPos.getSectionCoord(x);
//        int j = ChunkSectionPos.getSectionCoord(z);
//        long l = ChunkPos.toLong(i, j);
//        if (this.chunk != null && this.chunkPos == l) {
//            return this.chunk;
//        } else {
//            BlockView blockView = this.world.getChunkAsView(i, j);
//            this.chunk = blockView;
//            this.chunkPos = l;
//            return blockView;
//        }
//    }
//
//    @Override
//    protected T computeNext() {
//        while (this.blockIterator.step()) {
//            int i = this.blockIterator.getX();
//            int j = this.blockIterator.getY();
//            int k = this.blockIterator.getZ();
//            int l = this.blockIterator.getEdgeCoordinatesCount();
//            if (l != 3000) {
//                BlockView blockView = this.getChunk(i, k);
//                if (blockView != null) {
//                    this.pos.set(i, j, k);
//                    DisplayEntity.ItemDisplayEntity display = ((IMultiblockView)(blockView)).getMultiblockCoreEntity(pos, world);
//                    if(display == null) continue;
//
//                    if(!this.forEntity) {
//                        Identifier id;
//                        MultiblockData data = display.getComponent(DST.ENTITY_MULTIBLOCK_DATA_COMPONENT_KEY);
//                        if((id = data.getMultiblockID()) != null) {
//                            if(MultiblockRegistry.STRUCTURES.get(id) instanceof AbstractMultiblockHitboxStructure struc) {
//                                VoxelShape voxelShape = struc.getGenerator().getShape();
//                                voxelShape = voxelShape.offset(i,j-1,k);
//                                if(voxelShape.isEmpty()) System.out.println("voxelShape was empty");
//                                if (voxelShape == VoxelShapes.fullCube()) {
//                                    if (this.box.intersects(i, j, k, i + 1.0, j + 1.0, k + 1.0)) {
//                                        return (T)this.resultFunction.apply(this.pos, voxelShape);
//                                    }
//                                } else {
////                                    System.out.println("boxes: " + voxelShape.getBoundingBoxes());
//                                    if (!voxelShape.isEmpty() && VoxelShapes.matchesAnywhere(voxelShape, this.boxShape, BooleanBiFunction.AND)) {
//                                        return (T)this.resultFunction.apply(this.pos, voxelShape);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return this.endOfData();
//    }
//}
