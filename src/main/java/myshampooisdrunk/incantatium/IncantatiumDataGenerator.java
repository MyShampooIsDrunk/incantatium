package myshampooisdrunk.incantatium;

import myshampooisdrunk.drunk_server_toolkit.datagen.CustomRecipeProvider;
import myshampooisdrunk.incantatium.datagen.enchantments.CustomEnchantmentProvider;
import myshampooisdrunk.incantatium.loot.function.SetBundleContentsLootFunction;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static myshampooisdrunk.incantatium.registry.IncantatiumRegistry.*;
import static net.minecraft.registry.tag.EntityTypeTags.DEFLECTS_PROJECTILES;

public class IncantatiumDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		IncantatiumRegistry.init();
		pack.addProvider(CustomRecipeProvider::new);
		pack.addProvider(EntityTagGen::new);
		pack.addProvider(BlockTagGen::new);
		pack.addProvider(RaidRewardLootTable::new);
	}

	private static class EntityTagGen extends FabricTagProvider.EntityTypeTagProvider {

		public EntityTagGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			valueLookupBuilder(DEFLECTS_PROJECTILES).add(EntityType.RAVAGER);
		}
	}

	//TODO: all items added between 1.21-1.21.10 :(
	private static class BlockTagGen extends FabricTagProvider.BlockTagProvider {
		public BlockTagGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			valueLookupBuilder(TO_GRAVEL).
					addTag(AS_STONE).addTag(AS_COBBLESTONE).addTag(AS_PRISMARINE).addTag(AS_TUFF).addTag(AS_ANDESITE)
					.addTag(AS_GRANITE);

			valueLookupBuilder(AS_STONE_ORES).add(// -> stone
					Blocks.COAL_ORE,
					Blocks.COPPER_ORE,
					Blocks.DIAMOND_ORE,
					Blocks.EMERALD_ORE,
					Blocks.GOLD_ORE,
					Blocks.IRON_ORE,
					Blocks.LAPIS_ORE,
					Blocks.REDSTONE_ORE
			);

			valueLookupBuilder(AS_DEEPSLATE_ORES).add( // -> deepslate
					Blocks.DEEPSLATE_COAL_ORE,
					Blocks.DEEPSLATE_COPPER_ORE,
					Blocks.DEEPSLATE_DIAMOND_ORE,
					Blocks.DEEPSLATE_EMERALD_ORE,
					Blocks.DEEPSLATE_GOLD_ORE,
					Blocks.DEEPSLATE_IRON_ORE,
					Blocks.DEEPSLATE_LAPIS_ORE,
					Blocks.DEEPSLATE_REDSTONE_ORE
			);

			valueLookupBuilder(AS_DEEPSLATE_BRICKS).add(// -> random(deepslate, cobbled deepslate)
					Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICK_STAIRS,Blocks.CRACKED_DEEPSLATE_BRICKS,
					Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.CRACKED_DEEPSLATE_TILES, Blocks.CHISELED_DEEPSLATE
			);

			valueLookupBuilder(AS_DEEPSLATE).add(Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE_STAIRS);// -> cobbled deepslate

			valueLookupBuilder(AS_COBBLED_DEEPSLATE).add(Blocks.COBBLED_DEEPSLATE,Blocks.COBBLED_DEEPSLATE_SLAB,Blocks.COBBLED_DEEPSLATE_STAIRS);// -> cobblestone

			valueLookupBuilder(AS_MOSSY_STONE_BRICKS).add(Blocks.MOSSY_STONE_BRICKS,Blocks.MOSSY_STONE_BRICK_SLAB, // -> random(stone, mossy cobble)
					Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.CRACKED_STONE_BRICKS);

			valueLookupBuilder(AS_STONE_BRICKS).add(Blocks.STONE_BRICKS,Blocks.STONE_BRICK_SLAB,Blocks.STONE_BRICK_STAIRS, // -> random(stone, cobble)
					Blocks.CHISELED_STONE_BRICKS,Blocks.CRACKED_STONE_BRICKS,Blocks.SMOOTH_STONE,Blocks.SMOOTH_STONE_SLAB);

			valueLookupBuilder(AS_STONE).add(Blocks.STONE,Blocks.STONE_SLAB,Blocks.STONE_STAIRS);// -> gravel

			valueLookupBuilder(AS_COBBLESTONE).add(Blocks.COBBLESTONE,Blocks.COBBLESTONE_SLAB,Blocks.COBBLESTONE_STAIRS,// -> gravel
					Blocks.MOSSY_COBBLESTONE,Blocks.MOSSY_COBBLESTONE_SLAB,Blocks.MOSSY_COBBLESTONE_STAIRS);

			valueLookupBuilder(AS_PRISMARINE_BRICKS).add(// -> prismarine
					Blocks.PRISMARINE_BRICKS,Blocks.PRISMARINE_BRICK_SLAB,Blocks.PRISMARINE_BRICK_STAIRS,
					Blocks.DARK_PRISMARINE, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE_STAIRS
			);

			valueLookupBuilder(AS_PRISMARINE).add(Blocks.PRISMARINE,Blocks.PRISMARINE_SLAB,Blocks.PRISMARINE_STAIRS);// -> gravel

			valueLookupBuilder(AS_TUFF_BRICKS).add(Blocks.TUFF_BRICKS,Blocks.TUFF_BRICK_SLAB,Blocks.TUFF_BRICK_STAIRS, Blocks.CHISELED_TUFF_BRICKS);// -> polished tuff -> gravel

			valueLookupBuilder(AS_TUFF).add(Blocks.TUFF,Blocks.TUFF_SLAB,Blocks.TUFF_STAIRS,Blocks.CHISELED_TUFF,// -> gravel
					Blocks.POLISHED_TUFF,Blocks.POLISHED_TUFF_SLAB,Blocks.POLISHED_TUFF_STAIRS);

			valueLookupBuilder(AS_ANDESITE).add(Blocks.ANDESITE,Blocks.ANDESITE_SLAB,Blocks.ANDESITE_STAIRS,// -> gravel
					Blocks.POLISHED_ANDESITE,Blocks.POLISHED_ANDESITE_SLAB,Blocks.POLISHED_ANDESITE_STAIRS);

			valueLookupBuilder(AS_GRANITE).add(Blocks.GRANITE,Blocks.GRANITE_SLAB,Blocks.GRANITE_STAIRS,// -> gravel
					Blocks.POLISHED_GRANITE,Blocks.POLISHED_GRANITE_SLAB,Blocks.POLISHED_GRANITE_STAIRS);

			valueLookupBuilder(AS_DIORITE).add(Blocks.DIORITE,Blocks.DIORITE_SLAB,Blocks.DIORITE_STAIRS,// -> calcite
					Blocks.POLISHED_DIORITE,Blocks.POLISHED_DIORITE_SLAB,Blocks.POLISHED_DIORITE_STAIRS);

			valueLookupBuilder(AS_RED_SANDSTONE).add(// -> red sand
					Blocks.RED_SANDSTONE,Blocks.RED_SANDSTONE_SLAB,Blocks.RED_SANDSTONE_STAIRS,Blocks.CHISELED_RED_SANDSTONE,
					Blocks.SMOOTH_RED_SANDSTONE,Blocks.SMOOTH_RED_SANDSTONE_SLAB,Blocks.SMOOTH_RED_SANDSTONE_STAIRS
			);

			valueLookupBuilder(AS_SANDSTONE).add(// -> sand
					Blocks.SANDSTONE,Blocks.SANDSTONE_SLAB,Blocks.SANDSTONE_STAIRS,Blocks.CHISELED_SANDSTONE,
					Blocks.SMOOTH_SANDSTONE,Blocks.SMOOTH_SANDSTONE_SLAB,Blocks.SMOOTH_SANDSTONE_STAIRS
			);

			valueLookupBuilder(AS_CONCRETE).add(// -> concrete powder
					Blocks.WHITE_CONCRETE,
					Blocks.ORANGE_CONCRETE,
					Blocks.MAGENTA_CONCRETE,
					Blocks.LIGHT_BLUE_CONCRETE,
					Blocks.YELLOW_CONCRETE,
					Blocks.LIME_CONCRETE,
					Blocks.PINK_CONCRETE,
					Blocks.GRAY_CONCRETE,
					Blocks.LIGHT_GRAY_CONCRETE,
					Blocks.CYAN_CONCRETE,
					Blocks.PURPLE_CONCRETE,
					Blocks.BLUE_CONCRETE,
					Blocks.BROWN_CONCRETE,
					Blocks.GREEN_CONCRETE,
					Blocks.RED_CONCRETE,
					Blocks.BLACK_CONCRETE
			);

			valueLookupBuilder(AS_GLAZED_TERRACOTTA).add( // -> terracotta
					Blocks.WHITE_GLAZED_TERRACOTTA,
					Blocks.ORANGE_GLAZED_TERRACOTTA,
					Blocks.MAGENTA_GLAZED_TERRACOTTA,
					Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
					Blocks.YELLOW_GLAZED_TERRACOTTA,
					Blocks.LIME_GLAZED_TERRACOTTA,
					Blocks.PINK_GLAZED_TERRACOTTA,
					Blocks.GRAY_GLAZED_TERRACOTTA,
					Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
					Blocks.CYAN_GLAZED_TERRACOTTA,
					Blocks.PURPLE_GLAZED_TERRACOTTA,
					Blocks.BLUE_GLAZED_TERRACOTTA,
					Blocks.BROWN_GLAZED_TERRACOTTA,
					Blocks.GREEN_GLAZED_TERRACOTTA,
					Blocks.RED_GLAZED_TERRACOTTA,
					Blocks.BLACK_GLAZED_TERRACOTTA
			);

			valueLookupBuilder(AS_BRICKS).add(Blocks.BRICKS, Blocks.BRICK_STAIRS,Blocks.BRICK_SLAB);// -> terracotta

			//valueLookupBuilder(AS_PURPUR).add(Blocks.PURPUR_BLOCK,Blocks.PURPUR_SLAB,Blocks.PURPUR_PILLAR,Blocks.PURPUR_STAIRS);// -> air

			valueLookupBuilder(AS_END_STONE).add(Blocks.END_STONE,Blocks.END_STONE_BRICK_SLAB,Blocks.END_STONE_BRICK_STAIRS,Blocks.END_STONE_BRICKS);// -> sandstone i think

			valueLookupBuilder(AS_BLACKSTONE_BRICKS).add(// -> blackstone -> cobbled deepslate -> ...
					Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE,
					Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB,
					Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS,
					Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS
			);
			valueLookupBuilder(AS_BLACKSTONE).add(Blocks.GILDED_BLACKSTONE,Blocks.BLACKSTONE,Blocks.BLACKSTONE_STAIRS,Blocks.BLACKSTONE_SLAB); // -> cobbled deepslate

			valueLookupBuilder(AS_NETHER_BRICKS).add(// -> bricks ->...
					Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS,
					Blocks.NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICK_SLAB,
					Blocks.NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICK_STAIRS
			);

			//Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BRICKS -> Quartz block -> air
//			valueLookupBuilder(AS_QUARTZ).add(Blocks.QUARTZ_BLOCK, Blocks.SMOOTH_QUARTZ, Blocks.QUARTZ_SLAB,
//					Blocks.SMOOTH_QUARTZ_SLAB, Blocks.QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ_STAIRS);

			valueLookupBuilder(AS_BASALT).add(Blocks.BASALT,Blocks.POLISHED_BASALT,Blocks.SMOOTH_BASALT);// -> deepslate

			valueLookupBuilder(AS_OBSIDIAN).add(Blocks.CRYING_OBSIDIAN, Blocks.OBSIDIAN);//netherite block -> ancient debris -> obsidian -> blackstone

			//FUCK YOU MOJANK
			valueLookupBuilder(AS_COPPER).add(// -> copper ore...
					Blocks.CUT_COPPER,Blocks.WAXED_CUT_COPPER,
					Blocks.WEATHERED_CUT_COPPER,Blocks.WAXED_WEATHERED_CUT_COPPER,
					Blocks.EXPOSED_CUT_COPPER,Blocks.WAXED_EXPOSED_CUT_COPPER,
					Blocks.OXIDIZED_CUT_COPPER,Blocks.WAXED_OXIDIZED_CUT_COPPER,
					Blocks.CUT_COPPER_SLAB,Blocks.WAXED_CUT_COPPER_SLAB,
					Blocks.WEATHERED_CUT_COPPER_SLAB,Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
					Blocks.EXPOSED_CUT_COPPER_SLAB,Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
					Blocks.OXIDIZED_CUT_COPPER_SLAB,Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB,
					Blocks.CUT_COPPER_STAIRS,Blocks.WAXED_CUT_COPPER_STAIRS,
					Blocks.WEATHERED_CUT_COPPER_STAIRS,Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,
					Blocks.EXPOSED_CUT_COPPER_STAIRS,Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
					Blocks.OXIDIZED_CUT_COPPER_STAIRS,Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS,

					Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB,
					Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB,
					Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB,
					Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB,

					Blocks.COPPER_GRATE, Blocks.WAXED_COPPER_GRATE,
					Blocks.WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE,
					Blocks.EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE,
					Blocks.OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE,

					Blocks.CHISELED_COPPER, Blocks.WAXED_CHISELED_COPPER,
					Blocks.WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER,
					Blocks.EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER,
					Blocks.OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER,

					Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK,
					Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER,
					Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER,
					Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER
			);
			valueLookupBuilder(TO_SCULK).add(Blocks.SCULK_CATALYST,Blocks.SCULK_SENSOR,Blocks.SCULK_SHRIEKER);// -> sculk obv

			valueLookupBuilder(TO_MUD).add(// -> mud obv
					Blocks.PACKED_MUD,
					Blocks.MUD_BRICK_SLAB,
					Blocks.MUD_BRICK_STAIRS,
					Blocks.MUD_BRICKS,
					Blocks.MUDDY_MANGROVE_ROOTS
			);

//			valueLookupBuilder(AS_GLASS_PANE).add(
//					Blocks.GLASS_PANE,
//					Blocks.GRAY_STAINED_GLASS_PANE,
//					Blocks.BLACK_STAINED_GLASS_PANE,
//					Blocks.BLUE_STAINED_GLASS_PANE,
//					Blocks.CYAN_STAINED_GLASS_PANE,
//					Blocks.BROWN_STAINED_GLASS_PANE,
//					Blocks.GREEN_STAINED_GLASS_PANE,
//					Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
//					Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
//					Blocks.LIME_STAINED_GLASS_PANE,
//					Blocks.MAGENTA_STAINED_GLASS_PANE,
//					Blocks.ORANGE_STAINED_GLASS_PANE,
//					Blocks.PINK_STAINED_GLASS_PANE,
//					Blocks.YELLOW_STAINED_GLASS_PANE,
//					Blocks.WHITE_STAINED_GLASS_PANE,
//					Blocks.RED_STAINED_GLASS_PANE,
//					Blocks.PURPLE_STAINED_GLASS_PANE
//			);

//			valueLookupBuilder(AS_GLASS).add(
//					Blocks.GLASS,
//					Blocks.GRAY_STAINED_GLASS,
//					Blocks.BLACK_STAINED_GLASS,
//					Blocks.BLUE_STAINED_GLASS,
//					Blocks.CYAN_STAINED_GLASS,
//					Blocks.BROWN_STAINED_GLASS,
//					Blocks.GREEN_STAINED_GLASS,
//					Blocks.LIGHT_BLUE_STAINED_GLASS,
//					Blocks.LIGHT_GRAY_STAINED_GLASS,
//					Blocks.LIME_STAINED_GLASS,
//					Blocks.MAGENTA_STAINED_GLASS,
//					Blocks.ORANGE_STAINED_GLASS,
//					Blocks.PINK_STAINED_GLASS,
//					Blocks.YELLOW_STAINED_GLASS,
//					Blocks.WHITE_STAINED_GLASS,
//					Blocks.RED_STAINED_GLASS,
//					Blocks.PURPLE_STAINED_GLASS,
//					Blocks.TINTED_GLASS
//			);

//			valueLookupBuilder(TO_AIR)
//					.addTag(AS_GLASS)
//					.addTag(AS_GLASS_PANE)
//					.addTag(AS_QUARTZ)
//					.addTag(AS_PURPUR)
//					.addOptionalTag(BlockTags.CHAINS)
//					.addOptionalTag(BlockTags.BEDS)
//					.addOptionalTag(BlockTags.DIRT)
//					.addOptionalTag(BlockTags.CONCRETE_POWDER)
//					.addOptionalTag(BlockTags.CANDLES)
//					.addOptionalTag(BlockTags.WOOL_CARPETS)
//					.addOptionalTag(BlockTags.WOOL)
//					.addOptionalTag(BlockTags.SHULKER_BOXES)
//					.addOptionalTag(BlockTags.LEAVES)
//					.addOptionalTag(BlockTags.CORALS)
//					.addOptionalTag(BlockTags.SAPLINGS)
//					.addOptionalTag(BlockTags.FLOWERS)
//					.addOptionalTag(BlockTags.CAVE_VINES)
//					.addOptionalTag(BlockTags.BUTTONS)
//					.addOptionalTag(BlockTags.TRAPDOORS)
//					.addOptionalTag(BlockTags.DOORS)
//					.addOptionalTag(BlockTags.PRESSURE_PLATES)
//					.addOptionalTag(BlockTags.CAULDRONS)
//					.addOptionalTag(BlockTags.FENCE_GATES)
//					.addOptionalTag(BlockTags.FENCES)
//					.addOptionalTag(BlockTags.WALLS)
//					.addOptionalTag(BlockTags.ALL_SIGNS)
//					.addOptionalTag(BlockTags.CAMPFIRES)
//					.addOptionalTag(BlockTags.CROPS)
//					.addOptionalTag(BlockTags.BANNERS)
//					.addOptionalTag(BlockTags.WART_BLOCKS)
//					.addOptionalTag(BlockTags.CANDLE_CAKES)
//					.addOptionalTag(BlockTags.PLANKS)
//					.addOptionalTag(BlockTags.WOODEN_STAIRS)
//					.addOptionalTag(BlockTags.WOODEN_SLABS)
//					.addOptionalTag(BlockTags.FLOWER_POTS)
//
//					.add(
//							Blocks.SHROOMLIGHT,
//							Blocks.FROSTED_ICE,
//							Blocks.ICE,
//							Blocks.CHEST,
//							Blocks.BARREL,
//							Blocks.SAND,
//							Blocks.RED_SAND,
//							Blocks.BROWN_MUSHROOM,
//							Blocks.RED_MUSHROOM,
//							Blocks.TNT,
//							Blocks.CACTUS,
//							Blocks.CLAY,
//							Blocks.PUMPKIN,
//							Blocks.CARVED_PUMPKIN,
//							Blocks.MELON,
//							Blocks.CRIMSON_FUNGUS,
//							Blocks.CRIMSON_NYLIUM,
//							Blocks.CRIMSON_ROOTS,
//							Blocks.WARPED_FUNGUS,
//							Blocks.WARPED_NYLIUM,
//							Blocks.WARPED_ROOTS,
//							Blocks.NETHERRACK,
//							Blocks.SCULK,
//							Blocks.SPONGE,
//							Blocks.WET_SPONGE,
//							Blocks.SNIFFER_EGG,
//							Blocks.TURTLE_EGG,
//							Blocks.SLIME_BLOCK,
//							Blocks.HONEY_BLOCK,
//							Blocks.HONEYCOMB_BLOCK,
//							Blocks.SEA_PICKLE,
//							Blocks.SEA_LANTERN,
//							Blocks.GLOWSTONE,
//							Blocks.OCHRE_FROGLIGHT,
//							Blocks.PEARLESCENT_FROGLIGHT,
//							Blocks.VERDANT_FROGLIGHT,
//							Blocks.TWISTING_VINES,
//							Blocks.WEEPING_VINES,
//							Blocks.TWISTING_VINES_PLANT,
//							Blocks.WEEPING_VINES_PLANT,
//							Blocks.VINE,
//							Blocks.LADDER,
//							Blocks.AMETHYST_CLUSTER,
//							Blocks.LARGE_AMETHYST_BUD,
//							Blocks.MEDIUM_AMETHYST_BUD,
//							Blocks.SMALL_AMETHYST_BUD,
//							Blocks.AMETHYST_BLOCK,
//							Blocks.BAMBOO,
//							Blocks.BAMBOO_SAPLING,
//							Blocks.IRON_BARS,
//							Blocks.COBWEB,
//							Blocks.POINTED_DRIPSTONE,
//							Blocks.BEE_NEST,
//							Blocks.BEEHIVE,
//							Blocks.SNOW,
//							Blocks.SNOW_BLOCK,
//							Blocks.POWDER_SNOW,
//							Blocks.MANGROVE_ROOTS,
//							Blocks.LILY_PAD,
//							Blocks.DRIED_KELP_BLOCK,
//							Blocks.CAKE,
//							Blocks.FURNACE,
//							Blocks.DROPPER,
//							Blocks.DISPENSER,
//							Blocks.COMPOSTER,
//							Blocks.SMOKER,
//							Blocks.BLAST_FURNACE,
//							Blocks.JUKEBOX,
//							Blocks.BREWING_STAND,
//							Blocks.DECORATED_POT,
//							Blocks.CHISELED_BOOKSHELF,
//							Blocks.BOOKSHELF,
//							Blocks.LECTERN,
//							Blocks.TRAPPED_CHEST,
//							Blocks.PISTON,
//							Blocks.PISTON_HEAD,
//							Blocks.STICKY_PISTON,
//							Blocks.INFESTED_COBBLESTONE,
//							Blocks.INFESTED_CHISELED_STONE_BRICKS,
//							Blocks.INFESTED_DEEPSLATE,
//							Blocks.INFESTED_STONE,
//							Blocks.INFESTED_CRACKED_STONE_BRICKS,
//							Blocks.INFESTED_STONE_BRICKS,
//							Blocks.INFESTED_MOSSY_STONE_BRICKS,
//							Blocks.SUSPICIOUS_GRAVEL,
//							Blocks.SUSPICIOUS_SAND,
//							Blocks.DIRT_PATH,
//							Blocks.FARMLAND,
//							Blocks.SHORT_GRASS,
//							Blocks.TALL_GRASS,
//							Blocks.SEAGRASS,
//							Blocks.TALL_SEAGRASS,
//							Blocks.KELP,
//							Blocks.SUGAR_CANE,
//							Blocks.TORCH,
//							Blocks.LOOM,
//							Blocks.STONECUTTER,
//							Blocks.CARTOGRAPHY_TABLE,
//							Blocks.SMITHING_TABLE,
//							Blocks.GRINDSTONE,
//							Blocks.FLETCHING_TABLE,
//							Blocks.CAULDRON,
//							Blocks.BREWING_STAND,
//							Blocks.HOPPER,
//							Blocks.LIGHTNING_ROD
//					);

			valueLookupBuilder(INDESTRUCTIBLE)
					.add(Blocks.BEDROCK, Blocks.BARRIER, Blocks.BEACON, Blocks.STRUCTURE_BLOCK, Blocks.STRUCTURE_VOID,
							Blocks.TEST_BLOCK, Blocks.TEST_INSTANCE_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK,
							Blocks.REPEATING_COMMAND_BLOCK, Blocks.LIGHT, Blocks.JIGSAW, Blocks.REINFORCED_DEEPSLATE);
		}
	}

	//TODO: make the main raid reward loot table
	//TODO: rewrite all of ts vro
	private static class RaidRewardLootTable extends SimpleFabricLootTableProvider {

		private RegistryWrapper.Impl<Enchantment> impl = null;
		public RaidRewardLootTable(FabricDataOutput dataGenerator, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataGenerator, registryLookup, LootContextTypes.CHEST);
			try {
				impl = registryLookup.get().getOrThrow(RegistryKeys.ENCHANTMENT);
			} catch (Exception e){
				e.printStackTrace();
			}

		}

		@Override
		public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
			LootTable.builder().pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1f,5f)).with(ItemEntry.builder(Items.EXPERIENCE_BOTTLE).apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(64,0.75f))))).build();
			lootTableBiConsumer.accept(RAID_REWARD_1,LootTable.builder()
					.pool(LootPool.builder()
							.rolls(UniformLootNumberProvider.create(1f,3f))
							.with(ItemEntry.builder(Items.DIAMOND_BLOCK).weight(2)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.REDSTONE_BLOCK).weight(3)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2))))
							.with(ItemEntry.builder(Items.EMERALD_BLOCK).weight(5)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(10,0.5f))))
							.with(ItemEntry.builder(Items.BUNDLE).weight(1)
									.apply(new SetBundleContentsLootFunction.Builder().item(Items.EXPERIENCE_BOTTLE.getDefaultStack().getRegistryEntry(),BinomialLootNumberProvider.create(24,0.5f))).build())
							//.apply(  CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY)))
							.build()));
			lootTableBiConsumer.accept(RAID_REWARD_2,LootTable.builder()
					.pool(LootPool.builder()
							.rolls(UniformLootNumberProvider.create(2f,5f))
							.with(ItemEntry.builder(Items.DIAMOND_BLOCK).weight(6)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.REDSTONE_BLOCK).weight(8)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
							.with(ItemEntry.builder(Items.GLOWSTONE).weight(2)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(10,0.3f))))
							.with(ItemEntry.builder(Items.EMERALD_BLOCK).weight(15)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(10,0.6f))))
							.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(1)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(1)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.BUNDLE).weight(2)
									.apply(new SetBundleContentsLootFunction.Builder().item(Items.EXPERIENCE_BOTTLE.getDefaultStack().getRegistryEntry(),BinomialLootNumberProvider.create(48,0.65f))).build())
							.build()));
			lootTableBiConsumer.accept(RAID_REWARD_3,LootTable.builder()
					.pool(LootPool.builder()
							.rolls(UniformLootNumberProvider.create(3f,8f))
							.with(ItemEntry.builder(Items.DIAMOND_BLOCK).weight(6)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.REDSTONE_BLOCK).weight(8)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
							.with(ItemEntry.builder(Items.GLOWSTONE).weight(2)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(10,0.4f))))
							.with(ItemEntry.builder(Items.EMERALD_BLOCK).weight(15)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(15,0.65f))))
							.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(2)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(2)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.NETHERITE_INGOT).weight(1)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.DRAGON_BREATH).weight(3)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(8,0.5f))))
							.with(ItemEntry.builder(Items.CHORUS_FRUIT).weight(5)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(8,0.5f))))
							.with(ItemEntry.builder(Items.SHULKER_SHELL).weight(3)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(16,0.4f))))
							.with(ItemEntry.builder(Items.BUNDLE).weight(2)
									.apply(new SetBundleContentsLootFunction.Builder().item(Items.EXPERIENCE_BOTTLE.getDefaultStack().getRegistryEntry(),BinomialLootNumberProvider.create(96,0.7f))).build())
							.build()));

			lootTableBiConsumer.accept(RAID_REWARD_4,LootTable.builder()
					.pool(LootPool.builder()
							.rolls(UniformLootNumberProvider.create(5f,12f))
							.with(ItemEntry.builder(Items.DIAMOND_BLOCK).weight(6)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2))))
							.with(ItemEntry.builder(Items.REDSTONE_BLOCK).weight(8)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2,5))))
							.with(ItemEntry.builder(Items.GLOWSTONE).weight(2)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(40,0.4f))))
							.with(ItemEntry.builder(Items.EMERALD_BLOCK).weight(18)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(20,0.7f))))
							.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(2)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2))))
							.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(2)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2))))
							.with(ItemEntry.builder(Items.NETHERITE_INGOT).weight(1)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.DRAGON_BREATH).weight(3)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(16,0.5f))))
							.with(ItemEntry.builder(Items.CHORUS_FRUIT).weight(5)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(16,0.5f))))
							.with(ItemEntry.builder(Items.SHULKER_SHELL).weight(5)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(24,0.5f))))
							.with(ItemEntry.builder(Items.BUNDLE).weight(2)
									.apply(new SetBundleContentsLootFunction.Builder().item(Items.EXPERIENCE_BOTTLE.getDefaultStack().getRegistryEntry(),BinomialLootNumberProvider.create(96,0.7f))).build())
							.build()));

			lootTableBiConsumer.accept(RAID_REWARD_5,LootTable.builder()
					.pool(LootPool.builder()
							.rolls(UniformLootNumberProvider.create(7f,18f))
							.with(ItemEntry.builder(Items.DIAMOND_BLOCK).weight(6)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
							.with(ItemEntry.builder(Items.REDSTONE_BLOCK).weight(8)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2,5))))
							.with(ItemEntry.builder(Items.GLOWSTONE).weight(1)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(25,0.5f))))
							.with(ItemEntry.builder(Items.EMERALD_BLOCK).weight(16)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(30,0.7f))))
							.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(2)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
							.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(2)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
							.with(ItemEntry.builder(Items.NETHERITE_INGOT).weight(1)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2))))
							.with(ItemEntry.builder(Items.DRAGON_BREATH).weight(2)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(16,0.75f))))
							.with(ItemEntry.builder(Items.CHORUS_FRUIT).weight(2)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(16,0.75f))))
							.with(ItemEntry.builder(Items.SHULKER_SHELL).weight(4)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(24,0.75f))))
							.build()));

			lootTableBiConsumer.accept(RAID_TREASURE_EASY, LootTable.builder()
					.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
							.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(8).quality(0)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2))))
							.with(ItemEntry.builder(Items.BEACON).weight(1).quality(1)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(16).quality(0)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(8,0.5f))))
							.with(ItemEntry.builder(Items.BUNDLE).weight(16).quality(0)
									.apply(new SetBundleContentsLootFunction.Builder()
											.item(Items.SHULKER_SHELL.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(20,0.5f))
											.item(Items.DRAGON_BREATH.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(10,0.4f))
											.item(Items.CHORUS_FRUIT.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(10,0.4f))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(2).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.SHARPNESS),ConstantLootNumberProvider.create(4))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(2).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.PROTECTION),ConstantLootNumberProvider.create(3))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(1).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.DENSITY),ConstantLootNumberProvider.create(5))))
							.with(ItemEntry.builder(Items.BUNDLE).weight(6).quality(0)
									.apply(new SetBundleContentsLootFunction.Builder()
											.item(Items.BLAZE_ROD.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(10, 0.5f))
											.item(Items.GHAST_TEAR.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(5, 0.8f))
											.item(Items.WITHER_SKELETON_SKULL.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(2, 0.15f))))
							.build()));

			lootTableBiConsumer.accept(RAID_TREASURE_NORMAL, LootTable.builder()
					.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
							.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(8).quality(0)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.BEACON).weight(4).quality(0)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.NETHERITE_INGOT).weight(10).quality(0)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(4,0.5f))))
							.with(ItemEntry.builder(Items.BUNDLE).weight(5).quality(0)
									.apply(new SetBundleContentsLootFunction.Builder()
											.item(Items.SHULKER_SHELL.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(48,0.4f))
											.item(Items.DRAGON_BREATH.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(20,0.35f))
											.item(Items.CHORUS_FRUIT.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(20,0.35f))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(1).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.SHARPNESS),ConstantLootNumberProvider.create(5))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(1).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.PROTECTION),ConstantLootNumberProvider.create(3))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(1).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.WIND_BURST),ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(1).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.DENSITY),ConstantLootNumberProvider.create(5))))
							.with(ItemEntry.builder(Items.NETHER_STAR).weight(5).quality(0)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(1,0.4f))))
							.build()));

			lootTableBiConsumer.accept(RAID_TREASURE_HARD, LootTable.builder()
					.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).bonusRolls(BinomialLootNumberProvider.create(1,0.1f))
							.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(8).quality(0)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,5))))
							.with(ItemEntry.builder(Items.BEACON).weight(3).quality(0)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2))))
							.with(ItemEntry.builder(Items.NETHERITE_INGOT).weight(6).quality(0)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(12,0.5f))))
							.with(ItemEntry.builder(Items.NETHERITE_BLOCK).weight(2).quality(0)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(2).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.SHARPNESS),ConstantLootNumberProvider.create(6))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(2).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.PROTECTION),ConstantLootNumberProvider.create(4))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(4).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.WIND_BURST),ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(2).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.BREACH),ConstantLootNumberProvider.create(3))))
							.build()));

			lootTableBiConsumer.accept(RAID_TREASURE_EXTREME, LootTable.builder()
					.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
							.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(10).quality(0)
									.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2))))
							.with(ItemEntry.builder(Items.BEACON).weight(3).quality(0)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.NETHERITE_INGOT).weight(6).quality(0)
									.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(6,0.5f))))
							.with(ItemEntry.builder(Items.NETHERITE_BLOCK).weight(2).quality(0)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.BUNDLE).weight(3).quality(0)
									.apply(new SetBundleContentsLootFunction.Builder()
											.item(Items.SHULKER_SHELL.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(64,0.5f))
											.item(Items.DRAGON_BREATH.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(30,0.35f))
											.item(Items.CHORUS_FRUIT.getDefaultStack().getRegistryEntry(), BinomialLootNumberProvider.create(30,0.35f))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(1).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.SHARPNESS),ConstantLootNumberProvider.create(6))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(1).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.PROTECTION),ConstantLootNumberProvider.create(4))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(3).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.WIND_BURST),ConstantLootNumberProvider.create(1))))
							.with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(1).quality(0)
									.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.BREACH),ConstantLootNumberProvider.create(3))))
							.with(ItemEntry.builder(Items.NETHER_STAR).weight(3).quality(0)
									.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
							.build()));
		}
	}
}
