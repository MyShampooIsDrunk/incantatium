package myshampooisdrunk.incantatium.items;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.item.AbstractRecipeItem;
import myshampooisdrunk.drunk_server_toolkit.item.CustomRecipe;
import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

import static myshampooisdrunk.incantatium.items.TimeStopItem.rgbToInt;

public class SichotianAppleItem extends AbstractRecipeItem<CraftingRecipeInput> {

    public static final FoodComponent SICHOTIAN_APPLE_HUNGER = new FoodComponent.Builder().nutrition(10).saturationModifier(2f).alwaysEdible().build();
    public static final ConsumableComponent SICHOTIAN_APPLE_EFFECTS = ConsumableComponent.builder().consumeEffect(
            new ApplyEffectsConsumeEffect(
                    List.of(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 18000, 0),
                            new StatusEffectInstance(StatusEffects.ABSORPTION, 9000, 5),
                            new StatusEffectInstance(StatusEffects.REGENERATION, 2400, 1),
                            new StatusEffectInstance(StatusEffects.HASTE, 1800, 2),
                            new StatusEffectInstance(StatusEffects.REGENERATION, 100, 4),
                            new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 4)
                    )
            )).consumeSeconds(0.8f).build();
    public SichotianAppleItem() {
        super(Items.GOLDEN_APPLE, Identifier.of(Incantatium.LOGGER.getName(),"sichotian_apple"), "incantatium.sichotian_apple.name", Incantatium.getModel(Incantatium.id("sichotian_apple")));

        addComponent(DataComponentTypes.FOOD, SICHOTIAN_APPLE_HUNGER);
        addComponent(DataComponentTypes.CONSUMABLE, SICHOTIAN_APPLE_EFFECTS);
        addComponent(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    @Override
    public ItemStack create() {
        ItemStack ret = super.create();
        String s = "Sichotian Apple";
        int spaces = 0;
        int[][] nums = new int[][] {
                {180,95,114},{172,85,112},{164,76,109},{155,67,108},{146,58,106},{136,49,105},{126,41,103},
                {115,34,102},{103,27,101},{91,21,100},{78,16,99},{63,13,97},{47,10,95},{26,9,93}
        };
        MutableText name = Text.literal("");
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == ' '){
                name.append(" ");
                spaces++;
            } else name.append(Text.literal(String.valueOf(s.charAt(i))).withColor(rgbToInt(nums[i-spaces])));
        }
        ret.set(DataComponentTypes.ITEM_NAME, name.setStyle(Style.EMPTY.withItalic(false)));
        return ret;
    }

    public CraftingRecipe recipe(){
        return new ShapedRecipe("", CraftingRecipeCategory.MISC, RawShapedRecipe.create(
                Map.of('N', Ingredient.ofItems(Items.NETHERITE_BLOCK),
                        'S',Ingredient.ofItems(Items.NETHER_STAR),
                        'T',Ingredient.ofItems(Items.TOTEM_OF_UNDYING),
                        'G',Ingredient.ofItems(Items.ENCHANTED_GOLDEN_APPLE)),
                List.of("SGS","TNT","SGS")),
                create()
        );
    }
    //insanely difficult code 😱😱😱
}
