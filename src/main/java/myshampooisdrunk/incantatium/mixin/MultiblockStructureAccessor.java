package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiblockStructure.class)
public interface MultiblockStructureAccessor {
    @Accessor
    Box getEntityBox();
}
