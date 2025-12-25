package myshampooisdrunk.incantatium.multiblock.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ShapedMultiblockRecipe extends AbstractMultiblockRecipe {
    private final Map<Integer, MultiblockEntryIngredient> pattern;
    public ShapedMultiblockRecipe(ItemStack result) {
        super(result);
        this.pattern = new HashMap<>();
    }

    public void addIngredient(int slot, MultiblockEntryIngredient ingredient) {
        this.pattern.put(slot, ingredient);
    }

    @Override
    public boolean matches(MultiblockRecipeInput input, World world) {
        int size = input.size();
        for (int j = 0; j < size; j++) {//makes it not reliant on specific directions lol
            boolean matches = true;
            for (Integer i : pattern.keySet()) {
                if(!pattern.get((i + j + size) % size).test(input.getEntryInSlot(i), world.getRegistryManager())) {
                    matches = false;
                    break;
                }
            }
            if(matches) return true;
        }
        return false;
    }
}
