package myshampooisdrunk.incantatium.multiblock.recipe;

import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.input.RecipeInput;

public interface MultiblockRecipeInput extends RecipeInputProvider, RecipeInput {

    MultiblockInventory.Entry getEntryInSlot(int slot);

    default int emptySlots() {
        int ret = 0;
        for (int i = 0; i < size(); i++) {
            if(!getEntryInSlot(i).isEmpty())
                ret |= (1 << i);
        }
        return ret;
    }
}
