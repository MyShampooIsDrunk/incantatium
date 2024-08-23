package myshampooisdrunk.incantatium.registry;

import me.emafire003.dev.custombrewrecipes.CustomBrewRecipeRegister;
import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.register.CustomItemRegistry;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.BrokenArmorPotionItem;
import myshampooisdrunk.incantatium.items.SichotianApple;
import myshampooisdrunk.incantatium.items.ThunderstormItem;
import myshampooisdrunk.incantatium.items.TimeStopItem;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IncantatiumRegistry {

    public static final AbstractCustomItem TIME_STOP_SWORD = CustomItemRegistry.registerItem(new TimeStopItem());
    public static final AbstractCustomItem THUNDERSTORM_ITEM = CustomItemRegistry.registerWithRecipe(new ThunderstormItem());
    public static final AbstractCustomItem SICHOTIAN_APPLE = CustomItemRegistry.registerWithRecipe(new SichotianApple());

//    public static final RegistryEntry<Potion> BROKEN_ARMOR = registerPotion("broken_armor", new Potion("broken_armor", new StatusEffectInstance(StatusEffects.UNLUCK, 900)));
//    public static final RegistryEntry<Potion> BROKEN_ARMOR_LONG = registerPotion("broken_armor_long", new Potion("broken_armor", new StatusEffectInstance(StatusEffects.UNLUCK, 1800)));
//    public static final RegistryEntry<Potion> BROKEN_ARMOR_STRONG = registerPotion("broken_armor_strong", new Potion("broken_armor", new StatusEffectInstance(StatusEffects.UNLUCK, 432)));

    public static void init(){
        CustomItemRegistry.registerRecipe(
                new ShapedRecipe("", CraftingRecipeCategory.MISC, RawShapedRecipe.create(
                        Map.of('G', Ingredient.ofItems(Items.GOLD_BLOCK),
                                'S',Ingredient.ofItems(Items.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP),
                                'A',Ingredient.ofItems(Items.GOLDEN_APPLE),
                                'D',Ingredient.ofItems(Items.DIAMOND_BLOCK)),
                        List.of("DGS","GAG","SGD")),
                        new ItemStack(Items.ENCHANTED_GOLDEN_APPLE,1)
                ), Incantatium.id("enchanted_golden_apple"));
        //potion color: #9E9170 or 158, 145, 112
        List<BrokenArmorPotionItem> brokenArmorItems = new ArrayList<>();
        for (BrokenArmorPotionItem.PotionStrength value : BrokenArmorPotionItem.PotionStrength.values()) {
            for (BrokenArmorPotionItem.PotionType potionType : BrokenArmorPotionItem.PotionType.values()) {
                brokenArmorItems.add(new BrokenArmorPotionItem(potionType, value));
            }
        }
        List<Item> ingredients = List.of(Items.NETHERITE_SCRAP, Items.ANCIENT_DEBRIS);
        for (BrokenArmorPotionItem item : brokenArmorItems) {
//            CustomItemRegistry.registerItem(item); //might as well; if it clutters it too much i'll remove
            ItemStack weakPotion = new ItemStack(item.getType().item);
            switch(item.getStrength()){
                case NONE -> weakPotion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.WEAKNESS));
                case LONG -> weakPotion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.LONG_WEAKNESS));
            }
            if(item.getStrength() != BrokenArmorPotionItem.PotionStrength.STRONG){
                //all weakness -> broken armor recipes
                for (Item i : ingredients) {
                    CustomBrewRecipeRegister.registerCustomRecipeWithComponents(weakPotion, new ItemStack(i), item.create());
                }
            }
            if(item.getStrength() == BrokenArmorPotionItem.PotionStrength.NONE){
                //all broken armor + redstone -> long
                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(item.create(), Items.REDSTONE.getDefaultStack(),
                        new BrokenArmorPotionItem(item.getType(), BrokenArmorPotionItem.PotionStrength.LONG).create());

                //all broken armor + glowstone -> strong
                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(item.create(), Items.GLOWSTONE.getDefaultStack(),
                        new BrokenArmorPotionItem(item.getType(), BrokenArmorPotionItem.PotionStrength.STRONG).create());
            }
            switch(item.getType()){
                //drink + glowstone -> splash
                case DRINK -> CustomBrewRecipeRegister.registerCustomRecipeWithComponents(item.create(), Items.GUNPOWDER.getDefaultStack(),
                        new BrokenArmorPotionItem(BrokenArmorPotionItem.PotionType.SPLASH, item.getStrength()).create());
                //splash + dbreath -> linger
                case SPLASH -> CustomBrewRecipeRegister.registerCustomRecipeWithComponents(item.create(), Items.DRAGON_BREATH.getDefaultStack(),
                        new BrokenArmorPotionItem(BrokenArmorPotionItem.PotionType.LINGER, item.getStrength()).create());
            }
        }

//        int color = rgbToInt(new int[]{158,145,112});
//        ItemStack brokenArmorPotion = new ItemStack(Items.POTION);
//        ItemStack brokenArmorPotionLong = new ItemStack(Items.POTION);
//        ItemStack brokenArmorPotionStrong = new ItemStack(Items.POTION);
//        ItemStack brokenArmorSplashPotion = new ItemStack(Items.SPLASH_POTION);
//        ItemStack brokenArmorSplashPotionLong = new ItemStack(Items.SPLASH_POTION);
//        ItemStack brokenArmorSplashPotionStrong = new ItemStack(Items.SPLASH_POTION);
//        ItemStack brokenArmorLingerPotion = new ItemStack(Items.LINGERING_POTION);
//        ItemStack brokenArmorLingerPotionLong = new ItemStack(Items.LINGERING_POTION);
//        ItemStack brokenArmorLingerPotionStrong = new ItemStack(Items.LINGERING_POTION);
//
//        PotionContentsComponent brokenArmorComp = new PotionContentsComponent(Optional.empty(), Optional.of(color), List.of(
//                new StatusEffectInstance(StatusEffects.UNLUCK, 900)
//        ));
//        PotionContentsComponent brokenArmorLongComp = new PotionContentsComponent(Optional.empty(), Optional.of(color), List.of(
//                new StatusEffectInstance(StatusEffects.UNLUCK, 1800)
//        ));
//        PotionContentsComponent brokenArmorStrongComp = new PotionContentsComponent(Optional.empty(), Optional.of(color), List.of(
//                new StatusEffectInstance(StatusEffects.UNLUCK, 432, 1)
//        ));
//        PotionContentsComponent brokenArmorCompLinger = new PotionContentsComponent(Optional.empty(), Optional.of(color), List.of(
//                new StatusEffectInstance(StatusEffects.UNLUCK, 225)
//        ));
//        PotionContentsComponent brokenArmorLongCompLinger = new PotionContentsComponent(Optional.empty(), Optional.of(color), List.of(
//                new StatusEffectInstance(StatusEffects.UNLUCK, 450)
//        ));
//        PotionContentsComponent brokenArmorStrongCompLinger = new PotionContentsComponent(Optional.empty(), Optional.of(color), List.of(
//                new StatusEffectInstance(StatusEffects.UNLUCK, 108, 1)
//        ));
//
//
//        brokenArmorPotion.set(DataComponentTypes.POTION_CONTENTS, brokenArmorComp);
//        brokenArmorSplashPotion.set(DataComponentTypes.POTION_CONTENTS, brokenArmorComp);
//        brokenArmorLingerPotion.set(DataComponentTypes.POTION_CONTENTS, brokenArmorComp);
//        brokenArmorPotionLong.set(DataComponentTypes.POTION_CONTENTS, brokenArmorLongComp);
//        brokenArmorSplashPotionLong.set(DataComponentTypes.POTION_CONTENTS, brokenArmorLongComp);
//        brokenArmorLingerPotionLong.set(DataComponentTypes.POTION_CONTENTS, brokenArmorLongComp);
//        brokenArmorPotionStrong.set(DataComponentTypes.POTION_CONTENTS, brokenArmorStrongComp);
//        brokenArmorSplashPotionStrong.set(DataComponentTypes.POTION_CONTENTS, brokenArmorStrongComp);
//        brokenArmorLingerPotionStrong.set(DataComponentTypes.POTION_CONTENTS, brokenArmorStrongComp);
//        PotionContentsComponent WEAKNESS = new PotionContentsComponent(Potions.WEAKNESS);
//        PotionContentsComponent WEAKNESS_LONG = new PotionContentsComponent(Potions.LONG_WEAKNESS);
//        List<Item> ingredients = List.of(Items.NETHERITE_BOOTS, Items.NETHERITE_LEGGINGS, Items.NETHERITE_CHESTPLATE,
//                Items.NETHERITE_HELMET);
//        List<Item> potionTypes = List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
//
//        ComponentType<PotionContentsComponent> type = DataComponentTypes.POTION_CONTENTS;
//
//        //this library sucks ass but its better than every alternative
//        ComponentMap EMPTY = empty();
//        ComponentMap WEAK = mapFromComponent(type, WEAKNESS);
//        ComponentMap WEAK_LONG = mapFromComponent(type, WEAKNESS_LONG);
//        ComponentMap BROKEN = mapFromComponentWithName(type, brokenArmorComp, Text.literal("Potion of Broken Armor"));
//        ComponentMap BROKEN_LONG = mapFromComponentWithName(type, brokenArmorLongComp, Text.literal("Potion of Broken Armor"));
//        ComponentMap BROKEN_STRONG = mapFromComponentWithName(type, brokenArmorStrongComp, Text.literal("Potion of Broken Armor"));
//        ComponentMap BROKEN_SPLASH = mapFromComponentWithName(type, brokenArmorComp, Text.literal("Splash Potion of Broken Armor"));
//        ComponentMap BROKEN_LONG_SPLASH = mapFromComponentWithName(type, brokenArmorLongComp, Text.literal("Splash Potion of Broken Armor"));
//        ComponentMap BROKEN_STRONG_SPLASH = mapFromComponentWithName(type, brokenArmorStrongComp, Text.literal("Splash Potion of Broken Armor"));
//        ComponentMap BROKEN_LINGER = mapFromComponentWithName(type, brokenArmorComp, Text.literal("Lingering Potion of Broken Armor"));
//        ComponentMap BROKEN_LONG_LINGER = mapFromComponentWithName(type, brokenArmorLongComp, Text.literal("Lingering Potion of Broken Armor"));
//        ComponentMap BROKEN_STRONG_LINGER = mapFromComponentWithName(type, brokenArmorStrongComp, Text.literal("Lingering Potion of Broken Armor"));
//        for (Item potion : potionTypes) {
//            for (Item ingredient : ingredients) {
//                if(potion == Items.LINGERING_POTION){
//                    CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, ingredient, potion, WEAK, EMPTY, BROKEN_LINGER);
//                    CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, ingredient, potion, WEAK_LONG, EMPTY, BROKEN_LONG_LINGER);
//                }else if(potion == Items.SPLASH_POTION){
//                    CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, ingredient, potion, WEAK, EMPTY, BROKEN_SPLASH);
//                    CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, ingredient, potion, WEAK_LONG, EMPTY, BROKEN_SPLASH);
//                } else {
//                    CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, ingredient, potion, WEAK, EMPTY, BROKEN);
//                    CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, ingredient, potion, WEAK_LONG, EMPTY, BROKEN_LONG);
//                }
//            }
//            if(potion == Items.SPLASH_POTION){
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.DRAGON_BREATH, Items.LINGERING_POTION,
//                        BROKEN_SPLASH, EMPTY, BROKEN_LINGER);
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.DRAGON_BREATH, Items.LINGERING_POTION,
//                        BROKEN_LONG_SPLASH, EMPTY, BROKEN_LONG_LINGER);
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.DRAGON_BREATH, Items.LINGERING_POTION,
//                        BROKEN_STRONG_SPLASH, EMPTY, BROKEN_STRONG_LINGER);
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.GLOWSTONE, potion, BROKEN_SPLASH, EMPTY, BROKEN_STRONG_SPLASH);
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.REDSTONE, potion, BROKEN_SPLASH, EMPTY, BROKEN_LONG_SPLASH);
//            } else if (potion == Items.LINGERING_POTION){
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.GLOWSTONE, potion, BROKEN_LINGER, EMPTY, BROKEN_STRONG_LINGER);
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.REDSTONE, potion, BROKEN_LINGER, EMPTY, BROKEN_LONG_LINGER);
//            } else{
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.GUNPOWDER, Items.SPLASH_POTION,
//                        BROKEN, EMPTY, BROKEN_SPLASH);
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.GUNPOWDER, Items.SPLASH_POTION,
//                        BROKEN_LONG, EMPTY, BROKEN_LONG_SPLASH);
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.GUNPOWDER, Items.SPLASH_POTION,
//                        BROKEN_STRONG, EMPTY, BROKEN_STRONG_SPLASH);
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.GLOWSTONE, potion, BROKEN, EMPTY, BROKEN_STRONG);
//                CustomBrewRecipeRegister.registerCustomRecipeWithComponents(potion, Items.REDSTONE, potion, BROKEN, EMPTY, BROKEN_LONG);
//            }
//        }
//        CustomBrewRecipeRegister.registerCustomRecipeWithComponents();
    }

    private static <T> ComponentMap mapFromComponent(ComponentType<T> type, T val){
        ComponentMap.Builder builder = ComponentMap.builder();
        builder.add(type,val);
        return builder.build();
    }
    private static <T> ComponentMap mapFromComponentWithName(ComponentType<T> type, T val, Text name){
        ComponentMap.Builder builder = ComponentMap.builder();
        builder.add(DataComponentTypes.ITEM_NAME, name);
        builder.add(type,val);
        return builder.build();
    }

    private static ComponentMap empty(){
        return ComponentMap.builder().build();
    }

    public static int rgbToInt(int[] rgb){
        int ret = 0;
        for(int c :rgb) {
            ret = (ret << 8) + c;
        }
        return ret;
    }
}
