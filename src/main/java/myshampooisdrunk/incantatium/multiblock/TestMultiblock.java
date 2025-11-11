package myshampooisdrunk.incantatium.multiblock;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockCoreEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.hitbox.SolidHitboxGenerator;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructureType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class TestMultiblock extends MultiblockStructure {
    private final SolidHitboxGenerator generator;
    public TestMultiblock(MultiblockStructureType<?> type, MultiblockCoreEntity core) {
        super(type, core);
        this.generator = new SolidHitboxGenerator.Builder().add(
                new Box(0,0,0,1,1,1),Blocks.GOLD_BLOCK.getDefaultState()).add(new Box(0,0,0,-1.2,2,2),Blocks.EMERALD_BLOCK.getDefaultState()).add(
                new Box(2,2,2,3,3,3), Blocks.DIAMOND_BLOCK.getDefaultState()).add(
                new Box(2,2,2,1.8,4,4), Blocks.NETHERITE_BLOCK.getDefaultState()
        ).build();
    }

    @Override
    public boolean spawnStructure(BlockPos pos) {
        if (super.spawnStructure(pos)) {
            this.generator.get(world, new Vec3d(0.5,0.6,2)).forEach((v, list) -> {
                list.forEach(e -> MultiblockEntity.spawnEntity(e, this, pos, v)); //this feels kinda clunky tbh
            });
            return true;
        }
        return false;
    }

    //    private final SolidHitboxGenerator generator;
//    public TestMultiblock(Identifier id) {
//        super(id, Set.of(Blocks.ENCHANTING_TABLE));
//        generator = SolidHitboxGenerator.builder().add(
//                new Box(0,0,0,1,1,1),Blocks.GOLD_BLOCK.getDefaultState()).add(new Box(0,0,0,-1.2,2,2),Blocks.EMERALD_BLOCK.getDefaultState()).add(
//                new Box(2,2,2,3,3,3), Blocks.DIAMOND_BLOCK.getDefaultState()).add(
//                new Box(2,2,2,1.8,4,4), Blocks.NETHERITE_BLOCK.getDefaultState()
//        ).build();
//        generator.createAndGetEntities(new Vec3d(0,0,0)).forEach((v,list) -> list.forEach(e->attachEntity(v,e)));
//        addBlock(0,-1,0,Blocks.DIAMOND_BLOCK);
//        this.setCore(new MultiblockCoreEntity("core"));
//    }
//    public SolidHitboxGenerator getGenerator() {
//        return generator;
//    }

}
