package myshampooisdrunk.incantatium.registry;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.item.potion.CustomPotion;
import myshampooisdrunk.drunk_server_toolkit.register.CustomItemRegistry;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.SichotianApple;
import myshampooisdrunk.incantatium.items.ThunderstormItem;
import myshampooisdrunk.incantatium.items.TimeStopItem;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import java.util.List;
import java.util.Map;

public class IncantatiumRegistry {

    public static final AbstractCustomItem TIME_STOP_SWORD = CustomItemRegistry.registerItem(new TimeStopItem());
    public static final AbstractCustomItem THUNDERSTORM_ITEM = CustomItemRegistry.registerWithRecipe(new ThunderstormItem());
    public static final AbstractCustomItem SICHOTIAN_APPLE = CustomItemRegistry.registerWithRecipe(new SichotianApple());

    public static final CustomPotion BROKEN_ARMOR = CustomPotion.builder()
            .addEffect(
                    (dur, pot, type) -> (900 + (pot == 0 ? 900 * dur : -468))/(type == CustomPotion.PotionType.LINGER ? 4:1),
                    StatusEffects.UNLUCK)
            .color(6050900)
            .maxDurationLevel(1)
            .maxPotency(1)
            .build(Incantatium.id("broken_armor"));

    public static void init(){
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
}
