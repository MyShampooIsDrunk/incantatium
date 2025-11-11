package myshampooisdrunk.incantatium.multiblock.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class ShapelessMultiblockRecipe extends AbstractMultiblockRecipe {
    private final Set<MultiblockEntryIngredient> ingredients;

    public ShapelessMultiblockRecipe(ItemStack result) {
        super(result);
        ingredients = new HashSet<>();
    }

    public ShapelessMultiblockRecipe addIngredient(MultiblockEntryIngredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    @Override
    public boolean matches(MultiblockRecipeInput input, World world) {
        if(Integer.bitCount(input.emptySlots()) != ingredients.size()) return false;
        Set<MultiblockEntryIngredient> remaining = new HashSet<>(ingredients);
        for (int i = 0; i < input.size(); i++) {
            if(input.getEntryInSlot(i).isEmpty()) continue;
            boolean temp = false;
            for (MultiblockEntryIngredient ingredient : ingredients) {
                System.out.println("checking ingredient + entry " + input.getEntryInSlot(i));
                if(!remaining.contains(ingredient)) continue;
                if(ingredient.test(input.getEntryInSlot(i))) {
                    temp = true;
                    remaining.remove(ingredient);
                }
                else if(input.getEntryInSlot(i).isEmpty()) {
                    temp = true;
                }

            }
            if(!temp) return false;
        }
        return remaining.isEmpty();
    }
}
