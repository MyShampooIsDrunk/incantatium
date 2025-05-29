package myshampooisdrunk.incantatium.multiblock;

import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

public class RitualMultiblock extends MultiblockStructure {
    public RitualMultiblock(Identifier id) {
        super(id, Blocks.ENCHANTING_TABLE);
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
    }
}
