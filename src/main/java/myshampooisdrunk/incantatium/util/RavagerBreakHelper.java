package myshampooisdrunk.incantatium.util;

import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static myshampooisdrunk.incantatium.registry.IncantatiumRegistry.*;

public class RavagerBreakHelper {
    public static BlockState getNextBlockstate(BlockState init, BlockPos pos, World world){
//        Block block = init.getBlock();
        BlockState ret = Blocks.AIR.getDefaultState();
        BlockState temp;
        if(init.isIn(BlockTags.AIR) || init.isIn(IncantatiumRegistry.INDESTRUCTIBLE) || init.isLiquid()) return null;
        else if(init.getCollisionShape(world,pos).isEmpty()) ret = Blocks.CAVE_AIR.getDefaultState();
//        else if(init.isIn(TO_AIR)) ret = Blocks.AIR.getDefaultState();
        else if(init.isIn(TO_MUD)) ret = Blocks.MUD.getDefaultState();
        else if(init.isIn(TO_SCULK)) ret = Blocks.SCULK.getDefaultState();
        else if(init.isIn(TO_GRAVEL)) ret = Blocks.GRAVEL.getDefaultState();
        else if(init.isIn(AS_DEEPSLATE_ORES)) ret = Blocks.DEEPSLATE.getDefaultState();
        else if(init.isIn(AS_STONE_ORES)) ret = Blocks.STONE.getDefaultState();
        else if(init.isIn(AS_CONCRETE) || init.isIn(AS_GLAZED_TERRACOTTA)) ret = getFromColor(init).getDefaultState();
        else if(init.isIn(BlockTags.LOGS)) ret = getFromWood(init).getDefaultState();
        else if((temp = getFromStone(init,world.getRandom())) != init) ret = temp;
        else if(init.isIn(AS_RED_SANDSTONE)) ret = Blocks.RED_SAND.getDefaultState();
        else if(init.isIn(AS_SANDSTONE)) ret = Blocks.SAND.getDefaultState();
        else if(init.isIn(AS_BRICKS)) ret = Blocks.TERRACOTTA.getDefaultState();
        else if(init.isIn(AS_COPPER) || init.isOf(Blocks.RAW_COPPER_BLOCK)) ret = Blocks.COPPER_ORE.getDefaultState();
        else if(init.isOf(Blocks.IRON_BLOCK) || init.isOf(Blocks.RAW_IRON_BLOCK)) ret = Blocks.IRON_ORE.getDefaultState();
        else if(init.isOf(Blocks.GOLD_BLOCK) || init.isOf(Blocks.RAW_GOLD_BLOCK)) ret = Blocks.GOLD_ORE.getDefaultState();
        else if(init.isOf(Blocks.EMERALD_BLOCK)) ret = Blocks.EMERALD_ORE.getDefaultState();
        else if(init.isOf(Blocks.DIAMOND_BLOCK)) ret = Blocks.DIAMOND_ORE.getDefaultState();
        else if(init.isOf(Blocks.REDSTONE_BLOCK)) ret = Blocks.REDSTONE_ORE.getDefaultState();
        else if(init.isIn(BlockTags.ANVIL)) ret = Blocks.IRON_BLOCK.getDefaultState();
        else if(init.isOf(Blocks.BONE_BLOCK)) ret = Blocks.CALCITE.getDefaultState();
        else if(init.isOf(Blocks.NETHERITE_BLOCK)) ret = Blocks.ANCIENT_DEBRIS.getDefaultState();
        else if(init.isOf(Blocks.ANCIENT_DEBRIS)) ret = Blocks.OBSIDIAN.getDefaultState();
        else if(init.isIn(AS_OBSIDIAN)) ret = Blocks.BLACKSTONE.getDefaultState();
        else if(init.isIn(AS_BASALT)) ret = Blocks.DEEPSLATE.getDefaultState();
        else if(init.isOf(Blocks.GRAVEL) || init.isOf(Blocks.CALCITE)) ret = Blocks.SAND.getDefaultState();
        else if(init.isIn(BlockTags.TERRACOTTA)) ret = Blocks.RED_SANDSTONE.getDefaultState();
        else if(init.isIn(AS_DIORITE)) ret = Blocks.CALCITE.getDefaultState();
        else if(init.isOf(Blocks.RESPAWN_ANCHOR)) ret = Blocks.CRYING_OBSIDIAN.getDefaultState();
        else if(init.isOf(Blocks.ENCHANTING_TABLE) || init.isOf(Blocks.ENDER_CHEST)) ret = Blocks.OBSIDIAN.getDefaultState();
        return ret;
    }
    public static BlockState getFromStone(BlockState state, Random random){
        String type = "";
        String block;
        BlockState ret;
        if(state.isIn(BlockTags.STAIRS)) type = "_stairs";
        else if(state.isIn(BlockTags.SLABS)) type = "_slab";
        if(state.isIn(AS_DEEPSLATE_BRICKS)) block = new String[]{"polished_deepslate","cobbled_deepslate"}[random.nextBoolean() ? 0:1];
        else if(state.isIn(AS_DEEPSLATE)) block = "cobbled_deepslate";
        else if(state.isIn(AS_COBBLED_DEEPSLATE)) block = "cobblestone";
        else if(state.isIn(AS_MOSSY_STONE_BRICKS)) block = new String[]{"stone", "mossy_cobblestone"}[random.nextBoolean() ? 0:1];
        else if(state.isIn(AS_STONE_BRICKS)) block = new String[]{"stone", "cobblestone"}[random.nextBoolean() ? 0:1];
        else if(state.isIn(AS_PRISMARINE_BRICKS)) block = "prismarine";
        else if(state.isIn(AS_TUFF_BRICKS)) block = "tuff";
        else if(state.isIn(AS_BLACKSTONE)) block = "cobbled_deepslate";
        else if(state.isIn(AS_BLACKSTONE_BRICKS)) block = "blackstone";
        else if(state.isIn(AS_NETHER_BRICKS)) block = "brick" + (type.isEmpty() ? "s":"");
        else if(state.isOf(Blocks.CHISELED_QUARTZ_BLOCK) || state.isOf(Blocks.QUARTZ_BRICKS))
            return Blocks.QUARTZ_BLOCK.getDefaultState();
        else return state;
        Block outputBlock = Registries.BLOCK.get(Identifier.ofVanilla(block + type));
        if(type.isEmpty())return outputBlock.getDefaultState();
        ret = state.getEntries().entrySet().stream().reduce(
                        outputBlock.getDefaultState(),
                        (state1, entry) -> withIfExistsSuppressed(state1, entry.getKey(), entry.getValue()),
                        (state2, state3) -> state2
        );
        return ret;
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState withIfExistsSuppressed(BlockState state, Property<?> property, Comparable<?> value) {
        return state.withIfExists((Property) property, (Comparable) value);
    }

    public static Block getFromColor(BlockState state){
        Block block = state.getBlock();
        if(!(state.isIn(AS_CONCRETE) || state.isIn(AS_GLAZED_TERRACOTTA)))return block;
        if(block.equals(Blocks.RED_CONCRETE)) return Blocks.RED_CONCRETE_POWDER;
        if(block.equals(Blocks.BLUE_CONCRETE))return Blocks.BLUE_CONCRETE_POWDER;
        if(block.equals(Blocks.GRAY_CONCRETE))return Blocks.GRAY_CONCRETE_POWDER;
        if(block.equals(Blocks.ORANGE_CONCRETE))return Blocks.ORANGE_CONCRETE_POWDER;
        if(block.equals(Blocks.YELLOW_CONCRETE))return Blocks.YELLOW_CONCRETE_POWDER;
        if(block.equals(Blocks.GREEN_CONCRETE))return Blocks.GREEN_CONCRETE_POWDER;
        if(block.equals(Blocks.LIGHT_GRAY_CONCRETE))return Blocks.LIGHT_GRAY_CONCRETE_POWDER;
        if(block.equals(Blocks.WHITE_CONCRETE))return Blocks.WHITE_CONCRETE_POWDER;
        if(block.equals(Blocks.BLACK_CONCRETE))return Blocks.BLACK_CONCRETE_POWDER;
        if(block.equals(Blocks.BROWN_CONCRETE))return Blocks.BROWN_CONCRETE_POWDER;
        if(block.equals(Blocks.CYAN_CONCRETE))return Blocks.CYAN_CONCRETE_POWDER;
        if(block.equals(Blocks.LIGHT_BLUE_CONCRETE))return Blocks.LIGHT_BLUE_CONCRETE_POWDER;
        if(block.equals(Blocks.PURPLE_CONCRETE))return Blocks.PURPLE_CONCRETE_POWDER;
        if(block.equals(Blocks.MAGENTA_CONCRETE))return Blocks.MAGENTA_CONCRETE_POWDER;
        if(block.equals(Blocks.PINK_CONCRETE))return Blocks.PINK_CONCRETE_POWDER;
        if(block.equals(Blocks.LIME_CONCRETE))return Blocks.LIME_CONCRETE_POWDER;
        if(block.equals(Blocks.RED_GLAZED_TERRACOTTA))return Blocks.RED_TERRACOTTA;
        if(block.equals(Blocks.BLUE_GLAZED_TERRACOTTA))return Blocks.BLUE_TERRACOTTA;
        if(block.equals(Blocks.GRAY_GLAZED_TERRACOTTA))return Blocks.GRAY_TERRACOTTA;
        if(block.equals(Blocks.ORANGE_GLAZED_TERRACOTTA))return Blocks.ORANGE_TERRACOTTA;
        if(block.equals(Blocks.YELLOW_GLAZED_TERRACOTTA))return Blocks.YELLOW_TERRACOTTA;
        if(block.equals(Blocks.GREEN_GLAZED_TERRACOTTA))return Blocks.GREEN_TERRACOTTA;
        if(block.equals(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA))return Blocks.LIGHT_GRAY_TERRACOTTA;
        if(block.equals(Blocks.WHITE_GLAZED_TERRACOTTA))return Blocks.WHITE_TERRACOTTA;
        if(block.equals(Blocks.BLACK_GLAZED_TERRACOTTA))return Blocks.BLACK_TERRACOTTA;
        if(block.equals(Blocks.BROWN_GLAZED_TERRACOTTA))return Blocks.BROWN_TERRACOTTA;
        if(block.equals(Blocks.CYAN_GLAZED_TERRACOTTA))return Blocks.CYAN_TERRACOTTA;
        if(block.equals(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA))return Blocks.LIGHT_BLUE_TERRACOTTA;
        if(block.equals(Blocks.PURPLE_GLAZED_TERRACOTTA))return Blocks.PURPLE_TERRACOTTA;
        if(block.equals(Blocks.MAGENTA_GLAZED_TERRACOTTA))return Blocks.MAGENTA_TERRACOTTA;
        if(block.equals(Blocks.PINK_GLAZED_TERRACOTTA))return Blocks.PINK_TERRACOTTA;
        if(block.equals(Blocks.LIME_GLAZED_TERRACOTTA))return Blocks.LIME_TERRACOTTA;
        return block;
    }
    public static Block getFromWood(BlockState block){
        //logs -> stripped logs
        if(block.isOf(Blocks.ACACIA_LOG))return Blocks.STRIPPED_ACACIA_LOG;
        if(block.isOf(Blocks.ACACIA_WOOD))return Blocks.STRIPPED_ACACIA_WOOD;
        if(block.isOf(Blocks.BIRCH_LOG))return Blocks.STRIPPED_BIRCH_LOG;
        if(block.isOf(Blocks.BIRCH_WOOD))return Blocks.STRIPPED_BIRCH_WOOD;
        if(block.isOf(Blocks.CHERRY_LOG))return Blocks.STRIPPED_CHERRY_LOG;
        if(block.isOf(Blocks.CHERRY_WOOD))return Blocks.STRIPPED_CHERRY_WOOD;
        if(block.isOf(Blocks.CRIMSON_STEM))return Blocks.STRIPPED_CRIMSON_STEM;
        if(block.isOf(Blocks.CRIMSON_HYPHAE))return Blocks.STRIPPED_CRIMSON_HYPHAE;
        if(block.isOf(Blocks.DARK_OAK_LOG))return Blocks.STRIPPED_DARK_OAK_LOG;
        if(block.isOf(Blocks.DARK_OAK_WOOD))return Blocks.STRIPPED_DARK_OAK_WOOD;//jungle  oak spruce warped
        if(block.isOf(Blocks.JUNGLE_LOG))return Blocks.STRIPPED_JUNGLE_LOG;
        if(block.isOf(Blocks.JUNGLE_WOOD))return Blocks.STRIPPED_JUNGLE_WOOD;
        if(block.isOf(Blocks.MANGROVE_LOG))return Blocks.STRIPPED_MANGROVE_LOG;
        if(block.isOf(Blocks.MANGROVE_LOG))return Blocks.STRIPPED_MANGROVE_WOOD;
        if(block.isOf(Blocks.OAK_LOG))return Blocks.STRIPPED_OAK_LOG;
        if(block.isOf(Blocks.OAK_WOOD))return Blocks.STRIPPED_OAK_WOOD;
        if(block.isOf(Blocks.SPRUCE_LOG))return Blocks.STRIPPED_SPRUCE_LOG;
        if(block.isOf(Blocks.SPRUCE_WOOD))return Blocks.STRIPPED_SPRUCE_WOOD;
        if(block.isOf(Blocks.WARPED_STEM))return Blocks.STRIPPED_WARPED_STEM;
        if(block.isOf(Blocks.WARPED_HYPHAE))return Blocks.STRIPPED_WARPED_HYPHAE;
        //stripped logs -> planks
        if(block.isOf(Blocks.STRIPPED_ACACIA_LOG))return Blocks.ACACIA_PLANKS;
        if(block.isOf(Blocks.STRIPPED_ACACIA_WOOD))return Blocks.ACACIA_PLANKS;
        if(block.isOf(Blocks.STRIPPED_BIRCH_LOG))return Blocks.BIRCH_PLANKS;
        if(block.isOf(Blocks.STRIPPED_BIRCH_WOOD))return Blocks.BIRCH_PLANKS;
        if(block.isOf(Blocks.STRIPPED_CHERRY_LOG))return Blocks.CHERRY_PLANKS;
        if(block.isOf(Blocks.STRIPPED_CHERRY_WOOD))return Blocks.CHERRY_PLANKS;
        if(block.isOf(Blocks.STRIPPED_CRIMSON_STEM))return Blocks.CRIMSON_PLANKS;
        if(block.isOf(Blocks.STRIPPED_CRIMSON_HYPHAE))return Blocks.CRIMSON_PLANKS;
        if(block.isOf(Blocks.STRIPPED_DARK_OAK_LOG))return Blocks.DARK_OAK_PLANKS;
        if(block.isOf(Blocks.STRIPPED_DARK_OAK_WOOD))return Blocks.DARK_OAK_PLANKS;//jungle  oak spruce warped
        if(block.isOf(Blocks.STRIPPED_JUNGLE_LOG))return Blocks.JUNGLE_PLANKS;
        if(block.isOf(Blocks.STRIPPED_JUNGLE_WOOD))return Blocks.JUNGLE_PLANKS;
        if(block.isOf(Blocks.STRIPPED_MANGROVE_LOG))return Blocks.MANGROVE_PLANKS;
        if(block.isOf(Blocks.STRIPPED_MANGROVE_LOG))return Blocks.MANGROVE_PLANKS;
        if(block.isOf(Blocks.STRIPPED_OAK_LOG))return Blocks.OAK_PLANKS;
        if(block.isOf(Blocks.STRIPPED_OAK_WOOD))return Blocks.OAK_PLANKS;
        if(block.isOf(Blocks.STRIPPED_SPRUCE_LOG))return Blocks.SPRUCE_PLANKS;
        if(block.isOf(Blocks.STRIPPED_SPRUCE_WOOD))return Blocks.SPRUCE_PLANKS;
        if(block.isOf(Blocks.STRIPPED_WARPED_STEM))return Blocks.WARPED_PLANKS;
        if(block.isOf(Blocks.STRIPPED_WARPED_HYPHAE))return Blocks.WARPED_PLANKS;
        return Blocks.AIR;
    }


    /*
    wool -> cobweb ->
    just use blast value from block entities

    ignore valuable blocks (except for copper and lapis)

    anything harder than deepslate(explosion-wise) wouldn't be affected

    wood -> stripped wood -> plank -> dead bush... DONE

    grass block, podzol, mycelium -> dirt -> coarse dirt -> gravel... DONE

    deepslate -> cobbled deepslate -> stone -> cobblestone -> gravel -> sand...

    any terracotta -> normal terracotta -> red sandstone -> red sand... DONE
    concrete -> concrete powder DONE
    concrete powder, sand, red sand -> air (breaks with 50% drop rate) DONE

    blocks that arent solid (eg. torch, cobweb, grass, dead bush) -> air
    */
}
