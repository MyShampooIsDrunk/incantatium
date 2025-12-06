package myshampooisdrunk.incantatium.mixin;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DispenserBlock.class)
public interface DispenserBlockInvoker {
    @Invoker("getBehaviorForItem")
    DispenserBehavior invokeGetBehaviorForItem(World world, ItemStack stack);
}
