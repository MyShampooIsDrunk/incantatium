package myshampooisdrunk.incantatium.multiblock;

import myshampooisdrunk.drunk_server_toolkit.multiblock.registry.MultiblockRegistry;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.drunk_server_toolkit.register.CustomItemRegistry;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntityGenerator;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.multiblock.recipe.ShapelessMultiblockRecipe;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class IncantatiumMultiblockRegistry {
    public static final MultiblockStructure TEST = MultiblockRegistry.register(new TestMultiblock(Incantatium.id("test")));
    public static final MultiblockStructure SHRINE = MultiblockRegistry.register(new ShrineMultiblock(Incantatium.id("shrine")));
    public static void init(){
        ItemStack result = Items.ACACIA_LEAVES.getDefaultStack();
        result.set(DataComponentTypes.MAX_STACK_SIZE, 63);
        ShapelessMultiblockRecipe r = new ShapelessMultiblockRecipe(result)
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.ofCustom(
                        IncantatiumRegistry.SICHOTIAN_APPLE, 24, null))
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.DIRT,65).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.HEAVY_CORE,300).build());
        registerMultiblockRecipe(r, Incantatium.id("goon4"));
        registerMultiblockRecipe(
                new ShapelessMultiblockRecipe(Items.WAXED_OXIDIZED_COPPER_BULB.getDefaultStack())
                        .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.HEAVY_CORE,5).build()),
                Incantatium.id("goon5"));
    }

    public static Pedestal[] registerEntities(){
        Pedestal[] ret = new Pedestal[8];
        for (int i = 0; i < 8; i++) {
            PedestalEntityGenerator.PedestalEntity p = new PedestalEntityGenerator.PedestalEntity("pedestal_slot_"+i, i);
            PedestalEntityGenerator.PedestalEntityText t = new PedestalEntityGenerator.PedestalEntityText("pedestal_text_slot_"+i, i);
            PedestalEntityGenerator.PedestalEntityInteraction ei = new PedestalEntityGenerator.PedestalEntityInteraction("pedestal_interaction_slot_"+i, i);
            Pedestal x = new Pedestal(p,t,ei);
            ret[i] = x;
        }
        return ret;
    }



    public static void registerMultiblockRecipe(AbstractMultiblockRecipe recipe, Identifier id) {
        IncantatiumRegistry.MULTIBLOCK_RECIPES.put(id, recipe);
    }

    public record Pedestal(PedestalEntityGenerator.PedestalEntity storage, PedestalEntityGenerator.PedestalEntityText text, PedestalEntityGenerator.PedestalEntityInteraction interaction) {
    }
}
