package myshampooisdrunk.incantatium.multiblock;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockCoreEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.multiblock.entity.SolidHitboxGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class TestMultiblock extends MultiblockStructure {
    public TestMultiblock(Identifier id) {
        super(id, Set.of(Blocks.ENCHANTING_TABLE));
        SolidHitboxGenerator generator = SolidHitboxGenerator.builder().add(
                new Box(0,0,0,1,1,1), new Box(0,0,0,-1.2,2,2)).add(
                new Box(2,2,2,3,3,3), Blocks.DIAMOND_BLOCK.getDefaultState()).add(
                new Box(2,2,2,1.8,4,4), Blocks.NETHERITE_BLOCK.getDefaultState()
        ).build();
        generator.createAndGetEntities(new Vec3d(0,0,0)).forEach((v,list) -> list.forEach(e->attachEntity(v,e)));
        addBlock(0,-1,0,Blocks.DIAMOND_BLOCK);
        this.setCore(new MultiblockCoreEntity("core"));
    }
}
