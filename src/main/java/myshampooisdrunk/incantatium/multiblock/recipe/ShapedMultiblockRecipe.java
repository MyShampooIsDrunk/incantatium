package myshampooisdrunk.incantatium.multiblock.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ShapedMultiblockRecipe extends AbstractMultiblockRecipe{
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
        for (Integer i : pattern.keySet()) {
            if(!pattern.get(i).test(input.getEntryInSlot(i))) return false;
        }
        return true;
    }
}
