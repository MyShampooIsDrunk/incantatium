package myshampooisdrunk.incantatium.component;

import net.minecraft.item.ItemStack;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface InventoryStorage extends Component {

    Map<ItemStack, Integer>[] getInventory();
    default Map<ItemStack, Integer> getStack(int slot) {
        return getInventory()[slot];
    }

    default boolean matches(ItemStack stack, ItemStack slot) {
        return ItemStack.areItemsAndComponentsEqual(stack, slot);
    }

    default boolean tryAddStack(ItemStack stack, int slot) {
        assert 0 <= slot && slot < 8;
        Map<ItemStack, Integer>
        return true;
    }

//    void setInventory(List<ItemStack> stacks);
//    default void addStack(ItemStack stack) {
//        List<ItemStack> inv = new ArrayList<>(getInventory());
//        int maxCount = inv.getFirst().getMaxCount();
//        if(matches(stack, inv.getFirst())) {
//            int count = stack.getCount();
//            Map<Integer,ItemStack> stacks = new HashMap<>();
//            for (int i = 0; i < inv.size(); i++) {
//                ItemStack ind = inv.get(i);
//                int count2 = ind.getCount();
//                if(count2 == ind.getMaxCount()) continue;
//                count2 += count;
//                if(count2 <= ind.getMaxCount()) {
//                    count = count2;
//                    break;
//                }
//            }
//            stacks.forEach(inv::add);
//        }
//    }
}
