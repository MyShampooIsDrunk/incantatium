package myshampooisdrunk.incantatium.multiblock;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockDisplayEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockEntityType;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructureType;
import myshampooisdrunk.drunk_server_toolkit.util.BlockUtil;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.ornaments.AbstractOrnamentItem;
import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntity;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.multiblock.recipe.ShapelessMultiblockRecipe;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class IncantatiumMultiblockRegistry {
    public static final MultiblockEntityType<DisplayEntity.ItemDisplayEntity, PedestalEntity.PedestalItemEntity> PEDESTAL_ITEM
            = MultiblockEntityType.register(
            "pedestal_item", MultiblockEntityType.Builder.create(PedestalEntity.getFactoryPItem(), EntityType.ITEM_DISPLAY));

    public static final MultiblockEntityType<DisplayEntity.TextDisplayEntity, PedestalEntity.PedestalTextEntity> PEDESTAL_TEXT
            = MultiblockEntityType.register(
            "pedestal_text", MultiblockEntityType.Builder.create(PedestalEntity.getFactoryPTxt(), EntityType.TEXT_DISPLAY));

    public static final MultiblockEntityType<InteractionEntity, PedestalEntity.PedestalInteractionEntity> PEDESTAL_INTERACTION
            = MultiblockEntityType.register(
            "pedestal_interaction", MultiblockEntityType.Builder.create(PedestalEntity.getFactoryPInt(), EntityType.INTERACTION));

    public static final MultiblockEntityType<DisplayEntity.ItemDisplayEntity, MultiblockDisplayEntity> ITEM_DISPLAY = MultiblockEntityType
            .register("item_display", MultiblockEntityType.Builder.create(MultiblockDisplayEntity::new, EntityType.ITEM_DISPLAY));

    public static final MultiblockStructureType<ShrineMultiblock> SHRINE = MultiblockStructureType.create(
            "shrine", ShrineMultiblock::new, ShrineMultiblock.getTemplate(), BlockUtil.simpleLookup(Blocks.ENCHANTING_TABLE));

    public static void init(){
        if(Incantatium.DEV_MODE) {
            ItemStack result = Items.ACACIA_LEAVES.getDefaultStack();
            result.set(DataComponentTypes.MAX_STACK_SIZE, 63);
            ShapelessMultiblockRecipe r = new ShapelessMultiblockRecipe(result)
                    .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.ofCustom(
                            IncantatiumRegistry.SICHOTIAN_APPLE, 24, null))
                    .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.DIRT,65).build())
                    .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.HEAVY_CORE,300).build());
            registerMultiblockRecipe(r, Incantatium.id("dest4"));
            registerMultiblockRecipe(
                    new ShapelessMultiblockRecipe(Items.WAXED_OXIDIZED_COPPER_BULB.getDefaultStack())
                            .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.HEAVY_CORE,5).build()),
                    Incantatium.id("dest5"));
        }

        registerOrnamentRecipe(IncantatiumRegistry.REVELATION_ORNAMENT);
        registerOrnamentRecipe(IncantatiumRegistry.ENDURANCE_ORNAMENT);
        registerOrnamentRecipe(IncantatiumRegistry.SALVATION_ORNAMENT);
        registerOrnamentRecipe(IncantatiumRegistry.CONSTITUTION_ORNAMENT);
        registerOrnamentRecipe(IncantatiumRegistry.GALE_ORNAMENT);
        registerOrnamentRecipe(IncantatiumRegistry.CYCLONE_ORNAMENT);
        registerOrnamentRecipe(IncantatiumRegistry.HYDROUS_ORNAMENT);

    }

    private static void registerOrnamentRecipe(AbstractOrnamentItem item) {
        registerMultiblockRecipe(item.recipe(), item.getIdentifier());
    }

    public static void registerMultiblockRecipe(AbstractMultiblockRecipe recipe, Identifier id) {
        IncantatiumRegistry.MULTIBLOCK_RECIPES.put(id, recipe);
    }
}
