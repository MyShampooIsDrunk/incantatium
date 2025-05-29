package myshampooisdrunk.incantatium.multiblock;

import myshampooisdrunk.drunk_server_toolkit.multiblock.registry.MultiblockRegistry;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.Incantatium;

public class IncantatiumMultiblockRegistry {
    public static final MultiblockStructure TEST = MultiblockRegistry.register(new TestMultiblock(Incantatium.id("test")));
    public static void init(){}
}
