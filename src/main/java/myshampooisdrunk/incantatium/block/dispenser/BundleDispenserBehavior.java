package myshampooisdrunk.incantatium.block.dispenser;

import myshampooisdrunk.incantatium.mixin.DispenserBlockInvoker;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.List;

public class BundleDispenserBehavior extends ItemDispenserBehavior {
    private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        BlockPos facing = pointer.pos().offset(direction);
        BundleContentsComponent contents = stack.get(DataComponentTypes.BUNDLE_CONTENTS);

        if(contents == null) return this.fallbackBehavior.dispense(pointer, stack);


        List<ItemEntity> itemEntities = pointer.world().getEntitiesByType(EntityType.ITEM, new Box(facing), __ -> true);

        if(itemEntities.isEmpty()) {
            if(!contents.isEmpty()) {
                BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(contents);
                ItemStack ret = builder.removeSelected();
                if(ret != null && pointer.state().getBlock() instanceof DispenserBlock dispenser) {
                    ItemStack postDispense = ((DispenserBlockInvoker) dispenser).invokeGetBehaviorForItem(pointer.world(), ret).dispense(pointer, ret);
                    builder.add(postDispense);
                    stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                }
            } else {
                return this.fallbackBehavior.dispense(pointer, stack);
            }
        } else {
            ItemEntity entity = itemEntities.getFirst();
            ItemStack ret = entity.getStack();
            int count = ret.getCount();
            int space = contents.getOccupancy().getDenominator() - contents.getOccupancy().getProperNumerator();
            if(space > 0 && BundleContentsComponent.canBeBundled(ret)) {
                BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(contents);
                int added = builder.add(ret);
                if (added == count) {
                    stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                    entity.discard();
//                        return stack;
                }
                else if (added < count ) {
                    stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
                    ret = ret.copyWithCount(count - added);
                    entity.setStack(ret);
//                        return Objects.requireNonNull(stack.get(DataComponentTypes.BUNDLE_CONTENTS)).get(0);
                }
            }
        }

        return stack;
    }
}
