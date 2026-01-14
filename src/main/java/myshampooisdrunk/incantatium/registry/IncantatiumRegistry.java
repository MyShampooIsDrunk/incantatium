package myshampooisdrunk.incantatium.registry;

import com.mojang.serialization.MapCodec;
import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.item.potion.CustomPotion;
import myshampooisdrunk.drunk_server_toolkit.registry.CustomItemRegistry;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.*;
import myshampooisdrunk.incantatium.items.ornaments.*;
import myshampooisdrunk.incantatium.loot.function.SetBundleContentsLootFunction;
import myshampooisdrunk.incantatium.multiblock.IncantatiumMultiblockRegistry;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import net.minecraft.block.Block;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.*;

public class IncantatiumRegistry {

    public static final AbstractCustomItem DIVINE_CROWN = CustomItemRegistry.registerItem(new DivineCrownItem());
    public static final AbstractCustomItem TIME_STOP_SWORD = CustomItemRegistry.registerItem(new TimeStopItem());
    public static final ThunderstormItem THUNDERSTORM_ITEM = CustomItemRegistry.registerWithRecipe(new ThunderstormItem());
    public static final SichotianAppleItem SICHOTIAN_APPLE = CustomItemRegistry.registerWithRecipe(new SichotianAppleItem());

    public static final CoinItem COPPER_COIN = CustomItemRegistry.registerItem(new CoinItem.CopperCoinItem());
    public static final CoinItem SILVER_COIN = CustomItemRegistry.registerItem(new CoinItem.SilverCoinItem());
    public static final CoinItem GOLD_COIN = CustomItemRegistry.registerItem(new CoinItem.GoldCoinItem());
    public static final CoinItem NETHERITE_COIN = CustomItemRegistry.registerItem(new CoinItem.NetheriteCoinItem());

    public static final AbstractCustomItem SHRINE_DISPLAY_ITEM = CustomItemRegistry.registerItem(new ShrineDisplayItem());

    public static final RevelationOrnamentItem REVELATION_ORNAMENT = CustomItemRegistry.registerItem(new RevelationOrnamentItem());
    public static final EnduranceOrnamentItem ENDURANCE_ORNAMENT = CustomItemRegistry.registerItem(new EnduranceOrnamentItem());
    public static final SalvationOrnamentItem SALVATION_ORNAMENT = CustomItemRegistry.registerItem(new SalvationOrnamentItem());
    public static final ConstitutionOrnamentItem CONSTITUTION_ORNAMENT = CustomItemRegistry.registerItem(new ConstitutionOrnamentItem());
    public static final GaleOrnamentItem GALE_ORNAMENT = CustomItemRegistry.registerItem(new GaleOrnamentItem());
    public static final CycloneOrnamentItem CYCLONE_ORNAMENT = CustomItemRegistry.registerItem(new CycloneOrnamentItem());
    public static final HydrousOrnamentItem HYDROUS_ORNAMENT = CustomItemRegistry.registerItem(new HydrousOrnamentItem());

    public static final RegistryKey<Enchantment> RAVAGING = enchantment("ravaging");
    public static final RegistryKey<Enchantment> SOUL_LINKED = enchantment("soul_linked");

    public static final TagKey<Item> HORSE_ARMOR = itemTag("enchantable/horse_armor");
    public static final TagKey<Item> FROGLIGHTS = itemTag("froglights");

    public static final RegistryKey<LootTable> RAID_REWARD_1 = RegistryKey.of(RegistryKeys.LOOT_TABLE, Incantatium.id("raid_reward_1"));// 2 per diff -> 16 max
    public static final RegistryKey<LootTable> RAID_REWARD_2 = RegistryKey.of(RegistryKeys.LOOT_TABLE, Incantatium.id("raid_reward_2"));// 3 per 2 diff (1 per diff and another per other diff) -> 12 max
    public static final RegistryKey<LootTable> RAID_REWARD_3 = RegistryKey.of(RegistryKeys.LOOT_TABLE, Incantatium.id("raid_reward_3")); // every diff -> 8 max
    public static final RegistryKey<LootTable> RAID_REWARD_4 = RegistryKey.of(RegistryKeys.LOOT_TABLE, Incantatium.id("raid_reward_4")); // every 2 diff -> 4 max
    public static final RegistryKey<LootTable> RAID_REWARD_5 = RegistryKey.of(RegistryKeys.LOOT_TABLE, Incantatium.id("raid_reward_5")); // every 3 diff -> 2 max
    //treasure will be a select few items: wind burst 1 books, god apples, beacons, huge amounts of shulker shells, netherite blocks, scute,
    public static final RegistryKey<LootTable> RAID_TREASURE_EASY = RegistryKey.of(RegistryKeys.LOOT_TABLE, Incantatium.id("raid_treasure_easy"));
    public static final RegistryKey<LootTable> RAID_TREASURE_NORMAL = RegistryKey.of(RegistryKeys.LOOT_TABLE, Incantatium.id("raid_treasure_normal"));
    public static final RegistryKey<LootTable> RAID_TREASURE_HARD = RegistryKey.of(RegistryKeys.LOOT_TABLE, Incantatium.id("raid_treasure_hard")); // every 4 diff -> 2 max
    public static final RegistryKey<LootTable> RAID_TREASURE_EXTREME = RegistryKey.of(RegistryKeys.LOOT_TABLE, Incantatium.id("raid_treasure_extreme"));
    // every 4 levels of difficulty (diff = bo level + server difficulty) -> 1 raid treasure (2 or 3 rolls)
    public static final LootFunctionType<SetBundleContentsLootFunction> SET_BUNDLE_CONTENTS = register("set_bundle_contents", SetBundleContentsLootFunction.CODEC);

    //blue ice -> packed ice -> ice

    //to air: beds, candles, shulkers, glass(panes and blocks), sands, concretes, gravels, wools, carpets, dirt and its variants,
    // netherrack, glowstone, leaves (duh), roots, amethyst buds, shroom blocks, wart blocks, shroomlight, azalea, cactus,
    // ladders, all vines, cobwebs, dripleaf, flowers, chorus, coral, sniffer eggs, turtle eggs, sponges, slime, honey,
    // pumpkins, melons, sculk, froglights, sea lanterns, kelp blocks, sea pickles, lily pads, crops, saplings, moss, snow,
    // ice, tnt, all storage blocks (I don't want people to lose their items)
    public static final TagKey<Block> TO_AIR = TagKey.of(RegistryKeys.BLOCK, Incantatium.id( "to_air"));

    public static final TagKey<Block> INDESTRUCTIBLE = TagKey.of(RegistryKeys.BLOCK, Incantatium.id( "indestructible"));

    public static final TagKey<Block> TO_GRAVEL = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "to_gravel"));

    public static final TagKey<Block> TO_MUD = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "to_mud"));//aka AS_PACKED_MUD

    public static final TagKey<Block> TO_SCULK = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "to_sculk"));//things like sculk catalysts and shriekers and whatnot

    public static final TagKey<Block> AS_COPPER = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_copper"));//there are SO MANY KINDS OF COPPER WTF

    public static final TagKey<Block> AS_GLASS = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_glass"));//there are SO MANY KINDS OF COPPER WTF
    public static final TagKey<Block> AS_GLASS_PANE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_glass_pane"));//there are SO MANY KINDS OF COPPER WTF

//	public static final TagKey<Block> AS_STONE_DECO = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_stone_deco"));//blocks like furnaces, droppers, pistons, etc.
//	public static final TagKey<Block> AS_WOOD_DECO = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_wood_deco"));//blocks like bookshelves, composters,
//^ unnecessary for the time being i think

    public static final TagKey<Block> AS_OBSIDIAN = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_obsidian"));

    public static final TagKey<Block> AS_BASALT = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_basalt"));
    public static final TagKey<Block> AS_QUARTZ = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_quartz"));
    public static final TagKey<Block> AS_NETHER_BRICKS = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_nether_bricks"));
    public static final TagKey<Block> AS_BLACKSTONE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_blackstone"));
    public static final TagKey<Block> AS_BLACKSTONE_BRICKS = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_blackstone_bricks"));

    public static final TagKey<Block> AS_END_STONE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_end_stone"));
    public static final TagKey<Block> AS_PURPUR = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_purpur"));

    public static final TagKey<Block> AS_BRICKS = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_bricks"));
    //	public static final TagKey<Block> AS_TERRACOTTA = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_terracotta")); already exists lol
    public static final TagKey<Block> AS_GLAZED_TERRACOTTA = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_glazed_terracotta"));
    public static final TagKey<Block> AS_CONCRETE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_concrete"));

    public static final TagKey<Block> AS_SANDSTONE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_sandstone"));
    public static final TagKey<Block> AS_RED_SANDSTONE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_red_sandstone"));

    public static final TagKey<Block> AS_DIORITE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_diorite"));
    public static final TagKey<Block> AS_GRANITE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_granite"));
    public static final TagKey<Block> AS_ANDESITE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_andesite"));
    public static final TagKey<Block> AS_TUFF_BRICKS = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_tuff_bricks"));
    public static final TagKey<Block> AS_TUFF = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_tuff"));

    public static final TagKey<Block> AS_PRISMARINE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_prismarine"));
    public static final TagKey<Block> AS_PRISMARINE_BRICKS= TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_prismarine_bricks"));

    public static final TagKey<Block> AS_COBBLESTONE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_cobblestone"));
    public static final TagKey<Block> AS_STONE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_stone"));
    public static final TagKey<Block> AS_STONE_BRICKS = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_stone_bricks"));//includes smooth stone
    public static final TagKey<Block> AS_MOSSY_STONE_BRICKS = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_mossy_stone_bricks"));

    public static final TagKey<Block> AS_COBBLED_DEEPSLATE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_cobbled_deepslate"));
    public static final TagKey<Block> AS_DEEPSLATE = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_deepslate"));//includes polished
    public static final TagKey<Block> AS_DEEPSLATE_BRICKS = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_deepslate_bricks"));//includes tiles

    public static final TagKey<Block> AS_DEEPSLATE_ORES = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_deepslate_ores"));
    public static final TagKey<Block> AS_STONE_ORES = TagKey.of(RegistryKeys.BLOCK,  Incantatium.id( "as_stone_ores"));
    
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
//        CustomItemRegistry.registerRecipe(
//                new ShapedRecipe("", CraftingRecipeCategory.MISC, RawShapedRecipe.create(
//                        Map.of('G', Ingredient.ofItems(Items.GOLD_BLOCK),
//                                'S',Ingredient.ofItems(Items.ANCIENT_DEBRIS, Items.NETHERITE_SCRAP),
//                                'A',Ingredient.ofItems(Items.GOLDEN_APPLE),
//                                'D',Ingredient.ofItems(Items.DIAMOND_BLOCK)),
//                        List.of("GDG","ASA","GDG")),
//                        new ItemStack(Items.ENCHANTED_GOLDEN_APPLE,1)
//                ), Incantatium.id("enchanted_golden_apple"));
//        BROKEN_ARMOR.registerBrewingRecipes(Potions.WEAKNESS,
//                Ingredient.ofItems(Items.NETHERITE_SCRAP, Items.ANCIENT_DEBRIS));
    }


    public static int rgbToInt(int[] rgb){
        int ret = 0;
        for(int c :rgb) {
            ret = (ret << 8) + c;
        }
        return ret;
    }

    public static ComponentMap getCustomData(AbstractCustomItem item) {
        return ComponentMap.builder().add(DataComponentTypes.CUSTOM_DATA, item.create().get(DataComponentTypes.CUSTOM_DATA)).build();
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

    private static RegistryKey<Enchantment> enchantment(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Incantatium.id(id));
    }


    private static TagKey<Item> itemTag(String id) {
        return TagKey.of(RegistryKeys.ITEM, Incantatium.id(id));
    }

    private static <T extends LootFunction> LootFunctionType<T> register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, Incantatium.id(id), new LootFunctionType<>(codec));
    }
}
