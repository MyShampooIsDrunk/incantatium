package myshampooisdrunk.incantatium.raid;

import it.unimi.dsi.fastutil.doubles.Double2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.ints.IntHeapPriorityQueue;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import myshampooisdrunk.incantatium.raid.upgrades.Upgrades;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

import java.util.*;

import static myshampooisdrunk.incantatium.raid.ModifiedRaid.RAVAGER_SPAWN_LOCATION;

public class RaidWave {
    private static final int MAX_SQUAD_SIZE = 25;
//    private final Set<RaiderEntry> raiders;
    private final Object2IntMap<Upgrades.RaiderUpgrade> totalUpgrades;
    private final int wave;
    private final int count;
    private final Set<BlockPos> spawningLocations;

    public RaidWave(int wave, int count){
        this.wave = wave;
        this.count = count;
//        this.raiders = new HashSet<>();
        this.totalUpgrades = new Object2IntLinkedOpenHashMap<>();
        this.spawningLocations = new HashSet<>();
    }

//    public void addRaider(RaiderEntry raider) {
//        raiders.add(raider);
//    }

//    public void spawnNextGroup(ServerWorld world) {
//        raiders.forEach(raider -> raider.getRaider().heal(1000));//just in case they have health boost so they spawn w full health
//        raiders.forEach(raider -> raider.getRaider().addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 50, 5)));//so they dont entity cram lol
//        raiders.forEach(raider -> raider.spawn(world, pos));
//    }

    public List<BlockPos> generateRaiderSpawns() {
        int squads = (int) Math.ceil((double) this.count / MAX_SQUAD_SIZE);
    }

    private BlockPos findRandomRaidersSpawnLocation(ServerWorld world, int proximity, int preRaidTicks, BlockPos center) {
        int i = preRaidTicks / 20;
        float f = 0.22F * i - 0.24F;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        float g = world.random.nextFloat() * (float) (Math.PI * 2);

        for (int j = 0; j < proximity; j++) {
            float h = g + (float) Math.PI * j / 8.0F;
            int k = center.getX() + MathHelper.floor(MathHelper.cos(h) * 32.0F * f) + world.random.nextInt(3) * MathHelper.floor(f);
            int l = center.getZ() + MathHelper.floor(MathHelper.sin(h) * 32.0F * f) + world.random.nextInt(3) * MathHelper.floor(f);
            int m = world.getTopY(Heightmap.Type.WORLD_SURFACE, k, l);
            if (MathHelper.abs(m - center.getY()) <= 96) {
                mutable.set(k, m, l);
                if (!world.isNearOccupiedPointOfInterest(mutable) || i <= 7) {
                    int n = 10;
                    if (world.isRegionLoaded(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)
                            && world.shouldTickEntityAt(mutable)
                            && (
                            RAVAGER_SPAWN_LOCATION.isSpawnPositionOk(world, mutable, EntityType.RAVAGER)
                                    || world.getBlockState(mutable.down()).isOf(Blocks.SNOW) && world.getBlockState(mutable).isAir()
                    )) {
                        return mutable;
                    }
                }
            }
        }

        return null;
    }

    public int getWave() {
        return wave;
    }

//    public Set<RaiderEntry> getRaiders() {
//        return raiders;
//    }

    public void addUpgrades(Upgrades.RaiderUpgrade u, int count){
        totalUpgrades.put(u, totalUpgrades.getOrDefault(u, 0) + count);
    }

    public void tickSpawning(ServerWorld world, BlockPos center) {

    }

    public Object2IntMap<Upgrades.RaiderUpgrade> getUpgrades() {
        return totalUpgrades;
    }

    public static class RaiderEntry {
        private final Object2IntMap<String> upgrades;
        private RaiderEntity baseRaider;
        private MobEntity pet = null;
        private MobEntity mount = null;
        private final int difficulty; //omen + world difficulty
        private final Object2IntMap<String> waveUpgrades;
        private int waveRaiderCount;

        public RaiderEntry(RaiderEntity baseRaider, int difficulty){
            this.difficulty = difficulty;
            this.baseRaider = baseRaider;
            this.upgrades = new Object2IntLinkedOpenHashMap<>();
            this.waveUpgrades = new Object2IntLinkedOpenHashMap<>();
            this.waveRaiderCount = 0;
        }

        public int getUpgradeCount(String upgrade) {
            return upgrades.getOrDefault(upgrade, 0);
        }

        public void setPet(MobEntity pet) {
            this.pet = pet;
        }

        public void setMount(MobEntity mount) {
            this.mount = mount;
        }

        public void setWaveRaiderCount(int count) {
            this.waveRaiderCount = count;
        }

        public int getWaveRaiderCount() {
            return waveRaiderCount;
        }

        public void spawn(ServerWorld world, BlockPos pos){
            baseRaider.refreshPositionAndAngles(pos.toCenterPos(), 0, 0);
            baseRaider.setPersistent();
            baseRaider.setVelocity(randomVel(0.2,0.01,0.2, baseRaider.getRandom()));

//            for (StatusEffectInstance instance : effects) {
//                baseRaider.addStatusEffect(instance);
//                if(pet != null)
//                    pet.addStatusEffect(instance);
//
//                if(mount != null)
//                    mount.addStatusEffect(instance);
//            }

            if(pet != null) {
                pet.refreshPositionAndAngles(pos.toCenterPos(), 0, 0);
                pet.setPersistent();
                pet.setVelocity(randomVel(0.2,0.01,0.2, pet.getRandom()));
            }

            if(mount != null) {
                mount.refreshPositionAndAngles(pos.toCenterPos(), 0, 0);
                mount.setPersistent();
                mount.setVelocity(randomVel(0.2,0.01,0.2, mount.getRandom()));
            }

            if(mount != null) {
                world.spawnNewEntityAndPassengers(mount);
            }
        }
        public RaiderEntity getRaider(){
            return baseRaider;
        }

//        public void addEffect(StatusEffectInstance effect) {
//            this.effects.add(effect);
//        }

        public void setRaider(RaiderEntity raider){
            baseRaider = raider;
        }

        public MobEntity getPet() {
            return pet;
        }

        public void setUpgrades() {
            List<Upgrades.RaiderUpgrade> raiderUpgrades = getUpgrades();

            for (Upgrades.RaiderUpgrade upgrade : raiderUpgrades) {
                upgrade.apply(this);
            }

            if(!upgrades.containsKey(Upgrades.WEAPON.id())){
                switch(baseRaider){
                    case VindicatorEntity vind -> vind.setStackInHand(Hand.MAIN_HAND, Items.IRON_AXE.getDefaultStack());
                    case PillagerEntity pilly -> pilly.setStackInHand(Hand.MAIN_HAND, Items.CROSSBOW.getDefaultStack());
                    default -> {}
                }
            }
        }

        public List<Upgrades.RaiderUpgrade> getUpgrades(){
            return upgrades.keySet().stream().map(Upgrades.RaiderUpgrade::getUpgrade)
                    .sorted(Comparator.comparingInt(Upgrades.RaiderUpgrade::sortWeight).reversed()).toList();
        }

        public int getDifficulty() {
            return difficulty;
        }

        public int addUpgrades(int points) {
            Double2ObjectRBTreeMap<Upgrades.RaiderUpgrade> weightMap = new Double2ObjectRBTreeMap<>();
//            NavigableMap<Double, Upgrades.RaiderUpgrade> weightMap = new TreeMap<>();
            double totalWeight = 0;
            int minCost = Integer.MAX_VALUE;

            for (Upgrades.RaiderUpgrade upgrade : Upgrades.RaiderUpgrade.UPGRADES.values()) {
                int cost = upgrade.getCost(this.getUpgradeCount(upgrade.id()), this);

                if (cost != -1) {
                    double weight = 1.0 / (cost + 1.0);

                    totalWeight += weight;
                    weightMap.put(totalWeight, upgrade);

                    if (cost < minCost) minCost = cost;
                }
            }

            Random rand = baseRaider.getRandom();
            int currentPoints = points;
            int failedAttempts = 0;
            int maxFailures = 10;

            while (currentPoints >= minCost && failedAttempts < maxFailures) {

                double target = rand.nextDouble() * totalWeight;


                Map.Entry<Double, Upgrades.RaiderUpgrade> entry = weightMap.tailMap(target).firstEntry();

                if (entry == null) {
                    continue;
                }

                Upgrades.RaiderUpgrade candidate = entry.getValue();
                int currentLevel = this.getUpgradeCount(candidate.id());
                int cost = candidate.getCost(currentLevel, this);

                if (cost != -1 && cost <= currentPoints) {
                    this.upgrades.put(candidate.id(), currentLevel + 1);
                    currentPoints -= cost;

                    failedAttempts = 0;
                } else {
                    failedAttempts++;
                }
            }

            return points - currentPoints;
        }

        public int getWaveUpgradeCount(String id) {
            return waveUpgrades.getOrDefault(id, 0);
        }

        public void setWaveUpgrades(Map<Upgrades.RaiderUpgrade, Integer> upgrades){
            waveUpgrades.clear();
            upgrades.forEach((u, i) -> waveUpgrades.put(u.id(), i.intValue()));
        }

        public static Vec3d randomVel(double xMax, double yMax, double zMax, Random rand){
            double dx = (double) rand.nextBetween((int) (xMax*-1000),(int) (xMax*1000))/1000d;
            double dy = (double) rand.nextBetween(0, (int) (yMax*1000))/1000d;
            double dz = (double) rand.nextBetween((int) (zMax*-1000),(int) (zMax*1000))/1000d;
            return new Vec3d(dx,dy,dz);
        }
    }
}