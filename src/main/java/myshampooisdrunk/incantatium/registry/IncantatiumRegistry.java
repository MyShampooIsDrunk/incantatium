package myshampooisdrunk.incantatium.registry;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.item.potion.CustomPotion;
import myshampooisdrunk.drunk_server_toolkit.registry.CustomItemRegistry;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.*;
import myshampooisdrunk.incantatium.items.ornaments.*;
import myshampooisdrunk.incantatium.multiblock.IncantatiumMultiblockRegistry;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;

import java.util.*;

public class IncantatiumRegistry {

    public static final AbstractCustomItem DIVINE_CROWN = CustomItemRegistry.registerItem(new DivineCrownItem());
    public static final AbstractCustomItem TIME_STOP_SWORD = CustomItemRegistry.registerItem(new TimeStopItem());
    public static final ThunderstormItem THUNDERSTORM_ITEM = (ThunderstormItem)CustomItemRegistry.registerWithRecipe(new ThunderstormItem());
    public static final SichotianAppleItem SICHOTIAN_APPLE = CustomItemRegistry.registerWithRecipe(new SichotianAppleItem());

    public static final AbstractCustomItem SHRINE_DISPLAY_ITEM = CustomItemRegistry.registerItem(new ShrineDisplayItem());

    public static final AbstractCustomItem REVELATION_ORNAMENT = CustomItemRegistry.registerItem(new RevelationOrnamentItem());
    public static final AbstractCustomItem ENDURANCE_ORNAMENT = CustomItemRegistry.registerItem(new EnduranceOrnamentItem());
    public static final AbstractCustomItem SALVATION_ORNAMENT = CustomItemRegistry.registerItem(new SalvationOrnamentItem());
    public static final AbstractCustomItem CONSTITUTION_ORNAMENT = CustomItemRegistry.registerItem(new ConstitutionOrnamentItem());
    public static final AbstractCustomItem GALE_ORNAMENT = CustomItemRegistry.registerItem(new GaleOrnamentItem());
    public static final AbstractCustomItem CYCLONE_ORNAMENT = CustomItemRegistry.registerItem(new CycloneOrnamentItem());
    public static final AbstractCustomItem HYDROUS_ORNAMENT = CustomItemRegistry.registerItem(new HydrousOrnamentItem());

    public static final CustomPotion BROKEN_ARMOR = CustomPotion.builder()
            .addEffect(
                    (dur, pot, type) -> (900 + (pot == 0 ? 900 * dur : -468))/(type == CustomPotion.PotionType.LINGER ? 4:1),
                    StatusEffects.UNLUCK)
            .color(6050900)
            .maxDurationLevel(1,Ingredient.ofItems(Items.REDSTONE))
            .maxPotency(1,Ingredient.ofItems(Items.GLOWSTONE_DUST))
            .build(Incantatium.id("broken_armor"));

    public static final Map<Identifier, AbstractMultiblockRecipe> MULTIBLOCK_RECIPES = new HashMap<>();

    public static void init(){
        IncantatiumMultiblockRegistry.init();
        CustomItemRegistry.registerRecipe(
                new ShapedRecipe("", CraftingRecipeCategory.MISC, RawShapedRecipe.create(
                        Map.of('G', Ingredient.ofItems(Items.GOLD_BLOCK),
                                'S',Ingredient.ofItems(Items.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP),
                                'A',Ingredient.ofItems(Items.GOLDEN_APPLE),
                                'D',Ingredient.ofItems(Items.DIAMOND_BLOCK)),
                        List.of("GDG","ASA","GDG")),
                        new ItemStack(Items.ENCHANTED_GOLDEN_APPLE,2)
                ), Incantatium.id("enchanted_golden_apple"));

        BROKEN_ARMOR.registerBrewingRecipes(Potions.WEAKNESS,
                Ingredient.ofItems(Items.NETHERITE_SCRAP, Items.ANCIENT_DEBRIS));
    }


    public static int rgbToInt(int[] rgb){
        int ret = 0;
        for(int c :rgb) {
            ret = (ret << 8) + c;
        }
        return ret;
    }

    public static ComponentMap getCustomData(AbstractCustomItem item) {
        return ComponentMap.builder().add(DataComponentTypes.CUSTOM_DATA,item.create().get(DataComponentTypes.CUSTOM_DATA)).build();
    }

    @SuppressWarnings("unchecked")
    public static ComponentMap getCustomData(AbstractCustomItem item, ComponentType<?>... types) {
        ItemStack stack = item.create();
        ComponentMap.Builder builder = ComponentMap.builder();
        for (ComponentType<?> type : types) {
            builder.add((ComponentType<Object>) type, stack.get(type));
        }
        return builder.build();
    }

}
