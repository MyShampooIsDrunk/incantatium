package myshampooisdrunk.incantatium.raid;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WaveGenerator {
    //private final Map<Integer, RaidWave> waves;
    private final World world;
    private final int omenLevel;
    private final Random random;
    public WaveGenerator(World world, int omenLevel, Random random){
        this.world = world;
        this.omenLevel = omenLevel;
        this.random = random;
        //this.waves = new HashMap<>();
    }
    public WaveGenerator(World world, int omenLevel){
        this(world,omenLevel, world.getRandom());
    }
    public RaidWave generateWave(int wave){
        Difficulty diff = world.getDifficulty();
        //between 0.625x and 1.375x the difficulty = btwn 5/8x and 11/8x
        int initialDiffPoints, initialRaiderCount, minBonus, maxBonus;

        switch(diff){
            default -> {
                initialDiffPoints = 40;
                initialRaiderCount = 6;
                minBonus = -500;
                maxBonus = 250;
            }
            case NORMAL -> {
                initialDiffPoints = 75;
                initialRaiderCount = 8;
                minBonus = -375;
                maxBonus = 375;
            }
            case HARD -> {
                initialDiffPoints = 120; //hahaha sadism go brrr
                initialRaiderCount = 10;
                minBonus = -250;
                maxBonus = 500;
                //nahhhh this shits actually gonna be mad hard
            }
        }

        float bonus2 = (1+(float)random.nextBetween(minBonus,maxBonus)*0.0005f);
        //normal is 50% harder than easy, while hard is 100% harder than normal
        AtomicInteger diffCache = new AtomicInteger(switch(diff) {
            case HARD -> 30;
            case NORMAL -> 20;
            default -> 10;
        });
        int raiderCount = (int)((double)initialRaiderCount * (1 + (double)wave * Math.log(wave/10d+1d)) * bonus2);//(int)(initialRaiderCount * Math.pow(1.5*Math.pow(wave,-0.8),wave) * bonus2);
        RaidWave raiders = new RaidWave(wave);
        LOGGER.info("wave {} will have {} raiders", wave, raiderCount);
        for(int i = 0; i < raiderCount; i++){
            RaiderEntity raiderEntity = random.nextBoolean() ? new VindicatorEntity(EntityType.VINDICATOR, world) : new PillagerEntity(EntityType.PILLAGER, world);
            RaiderData raider = new RaiderData(raiderEntity, omenLevel + switch(diff){case EASY,PEACEFUL -> 0; case NORMAL -> 1; case HARD -> 2;});
            float bonus = (1+(float)random.nextBetween(minBonus,maxBonus)*0.001f);
            AtomicInteger diffPoints = new AtomicInteger(diffCache.get() + (int)(initialDiffPoints * Math.pow(2.25,omenLevel)/60f * wave * bonus));
            raider.setWaveUpgrades(raiders.getUpgrades());
            raider.setWaveRaiderCount(raiderCount);
            raider.addUpgrades(diffPoints);
            raider.getUpgrades().forEach(raiders::addUpgrade);
            diffCache.set(0);
            if(diffPoints.get() != 0)diffCache.set(diffPoints.get());
            raider.setUpgrades();
            raiders.addRaider(raider);
        }
        int bonusRaiders = 0;
        while(diffCache.get() > 200){
            diffCache.getAndAdd(-200);
            RaiderEntity raiderEntity = random.nextBoolean() ? new VindicatorEntity(EntityType.VINDICATOR, world) : new PillagerEntity(EntityType.PILLAGER, world);
            RaiderData raider = new RaiderData(raiderEntity, omenLevel + switch(diff){case EASY,PEACEFUL -> 0; case NORMAL -> 1; case HARD -> 2;});
            raider.setWaveUpgrades(raiders.getUpgrades());
            raider.addUpgrades(diffCache);
            raider.getUpgrades().forEach(raiders::addUpgrade);
            raider.setUpgrades();
            raiders.addRaider(raider);
            bonusRaiders++;
        }
        //raiders.setRaid(raid);
        //waves.put(wave,raiders);
        LOGGER.info("wave {} has {} extra points and {} bonus raiders", wave, diffCache, bonusRaiders);
        return raiders;
        //bo1 -> 16, 32, 48; bo2 -> 32, 64, 96; bo3 -> 64, 128, 192; bo5 -> 256, 512, 768; bo10 -> 3072 PER WAVE at hard mode
        //vanilla:
        //-- easy:   4-5 ; 5-7 ; 4-6 ; 4-6
        //-- normal: 4-6 ; 5-7 ; 4-7 ; 8-10 ; 11-14 ; 10-14
        //-- hard:   4-8 ; 5-9 ; 4-9 ; 8-12 ; 11-16 ; 7-12  ; 14-19 ; 14-20
        //5 6 8; 7 7 9 ; 6 7 9 ; 6 10 12; 14 16; 14 12; 19; 20
        //how the fuck did they decide these waves? are they stupid?
        //well i guess im gonna have to invent my own algorithm to calculate raiders because they are fucking morons, imbeciles, cretins
        //idea: each wave adds 50% more raiders; we start with 4, 6, and 9 raiders depending on difficulty


    }

    //    public void spawnWave(int wave, BlockPos pos, Raid raid){
//        waves.get(wave).getRaiders().forEach(r -> raid.addRaider(wave, r.getRaider(), pos, false));
//        waves.get(wave).spawnWave(world,pos);
//    }
    public void spawnWave(RaidWave wave, BlockPos pos, Raid raid){
        wave.setRaid(raid, pos);
        wave.spawnWave(world, pos);
        //wave.getRaiders().forEach(r -> raid.addRaider(i, r.getRaider(), pos, true));
    }

    public static WaveGenerator create(Raid raid){
        return new WaveGenerator(raid.getWorld(), raid.getBadOmenLevel());
    }

    public static ItemStack giveRewards(int badOmen, int diff, ServerPlayerEntity player){
        ReloadableRegistries.Lookup lookup = Objects.requireNonNull(player.getServer()).getReloadableRegistries();
        List<ItemStack> contents = new ArrayList<>();
        List<RegistryKey<LootTable>> lootTables = new ArrayList<>();
        int raidDiff = badOmen * (2 * diff);
        for(int i = 1; i < badOmen+diff; i++){
            lootTables.add(RAID_REWARD_1);
            lootTables.add(RAID_REWARD_1);
            lootTables.add(RAID_REWARD_2);
            if(i%2==0)lootTables.add(RAID_REWARD_2);
            if(badOmen+diff >= 3)lootTables.add(RAID_REWARD_3);//bo2 + easy or bo1 + hard
            if(badOmen+diff >= 5 && i%2 == 0)lootTables.add(RAID_REWARD_4);//bo4 + easy or bo2 + hard
            if(badOmen+diff >= 7 && i%3 == 0)lootTables.add(RAID_REWARD_5);//bo5 + normal or bo4 + hard
        }
        int treasure = Math.clamp((int)Math.floor(diff * (Math.log(raidDiff)-1.5)),0,50)+1;//max is 18 so idrc bout the 50
        //int treasure = (int)Math.floor(Math.pow(1.5,raidDiff/8d));
        for (int i = 0; i < treasure; i++) {
            lootTables.add(switch(raidDiff/10){
                default -> RAID_TREASURE_HARD;// raid diff >= 20 --> max is bo5 + normal or bo5 + hard
                case 1 -> RAID_TREASURE_NORMAL;// 20 > raid diff >= 10 --> max is bo5 + easy or bo4 + normal or bo3 + hard
                case 0 -> RAID_TREASURE_EASY;// 10 > raid diff --> max is bo4 + easy or bo2 + normal or bo1 + hard
            });
            if(i%6 == 0 && raidDiff >= 30) lootTables.add(RAID_TREASURE_EXTREME);
        }

        int extra = 0;
        for (int j = 0; j < 5; j++) if(Math.random() < 0.0025 * badOmen * badOmen * badOmen) extra++;

        for(int i = 0; i < extra; i++){
            double f = 0.8 + Math.random() * 0.3; // 0.8 to 1.1
            lootTables.add(switch((int) (f * raidDiff/15)) {
                case 0 -> RAID_TREASURE_NORMAL;
                case 1 -> RAID_TREASURE_HARD;
                default -> RAID_TREASURE_EXTREME;
            });
        }
        for(RegistryKey<LootTable> t : lootTables){
            LootTable table = lookup.getLootTable(t);
            Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger(player, t);
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(player.getServerWorld()).add(LootContextParameters.ORIGIN,player.getPos());
            builder.add(LootContextParameters.THIS_ENTITY, player);
            contents.addAll(table.generateLoot(builder.build(LootContextTypes.CHEST)));
        }
        contents=mergeStacks(contents);
        ItemStack stack = new ItemStack(Items.BUNDLE);
        stack.set(DataComponentTypes.BUNDLE_CONTENTS, new BundleContentsComponent(contents));
        player.getInventory().offerOrDrop(stack);
        player.currentScreenHandler.sendContentUpdates();
        return stack;
    }
    public static List<ItemStack> mergeStacks(List<ItemStack> items){
        Map<Item,Integer> itemCountMap = new HashMap<>();
        List<ItemStack> ret = new ArrayList<>();
        for(ItemStack item : items){
            if (item.isOf(Items.BUNDLE)){
                List<ItemStack> bundleContents = Objects.requireNonNull(item.get(DataComponentTypes.BUNDLE_CONTENTS)).stream().toList();
                for(ItemStack bundleContent : bundleContents){
                    if(bundleContent.getItem().getMaxCount() == 1) {
                        ret.add(bundleContent);
                    }else{
                        if(!itemCountMap.containsKey(bundleContent.getItem()))itemCountMap.put(bundleContent.getItem(),0);
                        itemCountMap.put(bundleContent.getItem(),bundleContent.getCount() + itemCountMap.get(bundleContent.getItem()));
                    }
                }
            }
            else if(item.getItem().getMaxCount() == 1) {
                ret.add(item);
            }else{
                if(!itemCountMap.containsKey(item.getItem()))itemCountMap.put(item.getItem(),0);
                itemCountMap.put(item.getItem(),item.getCount() + itemCountMap.get(item.getItem()));
            }

        }
        itemCountMap.forEach((item,count)->{
            int max = item.getMaxCount();

            int stacks = count/max;
            int extra = count-stacks*max;
            for (int i = 0; i < stacks; i++) {
                ret.add(new ItemStack(item,max));
            }
            if(extra!=0) {
                ret.add(new ItemStack(item, extra));
            }
        });
        return ret;
    }
}



//waves per raid = omen + 2 * (diff-1) + round(2^((omen + diff)/3)) ->
/*
bo1: 3,5,8
bo2: 4,7,9
bo3: 6,8,11
bo4: 7,10,13
bo5: 9,12,15
*/