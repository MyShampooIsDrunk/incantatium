package myshampooisdrunk.incantatium.raid;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import myshampooisdrunk.incantatium.raid.upgrades.Upgrades;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class RaidWave {
    private final Set<RaiderEntry> raiders;
    private final Object2IntLinkedOpenHashMap<Upgrades.RaiderUpgrade> totalUpgrades;
    private final int wave;

    public RaidWave(int wave){
        this.wave=wave;
        raiders = new HashSet<>();
        totalUpgrades = new Object2IntLinkedOpenHashMap<>();
    }

    public void addRaider(RaiderEntry raider){
        raiders.add(raider);
    }

    public void spawnWave(World world, BlockPos pos){
        raiders.forEach(raider -> raider.getRaider().heal(1000));//just in case they have health boost so they spawn w full health
        raiders.forEach(raider -> raider.getRaider().addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 50, 5)));//so they dont entity cram lol
        raiders.forEach(raider -> raider.spawn(world,pos));
    }
    public int getWave(){return wave;}
    public Set<RaiderEntry> getRaiders() {return raiders;}
    public void setRaid(Raid raid, BlockPos pos){
        raiders.forEach(raider -> {
            raider.joinRaid(raid, wave, pos, false);
        });
    }
    public void addUpgrade(Upgrades.RaiderUpgrade u){
        totalUpgrades.put(u,totalUpgrades.containsKey(u) ? totalUpgrades.getInt(u) + 1 : 1);
    }
    public Object2IntLinkedOpenHashMap<Upgrades.RaiderUpgrade> getUpgrades(){return totalUpgrades;}

    public static class RaiderEntry {
        private final List<String> upgrades;
        private RaiderEntity baseRaider;
        private MobEntity pet = null;
        private MobEntity mount = null;
        private final int difficulty; //omen + world difficulty
        private final Object2IntLinkedOpenHashMap<Upgrades.RaiderUpgrade> waveUpgrades;
        private final Set<StatusEffectInstance> effects;
        private int waveRaiderCount;

        public RaiderEntry(RaiderEntity baseRaider, int difficulty){
            this.difficulty = difficulty;
            this.baseRaider = baseRaider;
            this.upgrades = new ArrayList<>();
            this.waveUpgrades = new Object2IntLinkedOpenHashMap<>();
            this.waveRaiderCount = 0;
            this.effects = new HashSet<>();
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
        public int getWaveRaiderCount(){
            return waveRaiderCount;
        }

        public void spawn(ServerWorld world, BlockPos pos){
            baseRaider.refreshPositionAndAngles(pos.toCenterPos(), 0, 0);
            baseRaider.setPersistent();
            baseRaider.setVelocity(randomVel(0.2,0.01,0.2, baseRaider.getRandom()));

            for (StatusEffectInstance instance : effects) {
                baseRaider.addStatusEffect(instance);
                if(pet != null)
                    pet.addStatusEffect(instance);

                if(mount != null)
                    mount.addStatusEffect(instance);
            }

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
        public void setRaider(RaiderEntity raider){
            baseRaider = raider;
        }

        public void setUpgrades() {
            List<Upgrades.RaiderUpgrade> raiderUpgrades = getUpgrades();

            for (Upgrades.RaiderUpgrade upgrade : raiderUpgrades) {
                upgrade.apply(this);
            }

            if(!upgrades.contains(Upgrades.WEAPON.id())){
                switch(baseRaider){
                    case VindicatorEntity vind -> vind.setStackInHand(Hand.MAIN_HAND, Items.IRON_AXE.getDefaultStack());
                    case PillagerEntity pilly -> pilly.setStackInHand(Hand.MAIN_HAND, Items.CROSSBOW.getDefaultStack());
                    default -> {}
                }
            }
        }

        public List<Upgrades.RaiderUpgrade> getUpgrades(){
            return upgrades.stream().map(Upgrades.RaiderUpgrade::getUpgrade)
                    .sorted(Comparator.comparingInt(Upgrades.RaiderUpgrade::sortWeight)).toList();
        }

        public int getDifficulty() {
            return difficulty;
        }
        public int addUpgrades(int points) {
            Random rand = baseRaider.getRandom();
            List<Upgrades.RaiderUpgrade> poss = new ArrayList<>();
//            Map<Upgrades.RaiderUpgrade, Integer> weights = new HashMap<>();
            int totalWeight = 0;
            for (Upgrades.RaiderUpgrade upgrade : Upgrades.RaiderUpgrade.UPGRADES.values()) {
                int cost = upgrade.getCost(this);
                if(cost != -1 && cost <= points) {
                    totalWeight += cost;
                    poss.add(upgrade);
//                    weights.put(upgrade, totalWeight);
                }
            }

//            int finalTotalWeight = totalWeight;
//            weights.replaceAll((u, i) -> finalTotalWeight - i);
            poss = poss.reversed();

            int cost, newCost, totalCost = 0, index = 0;
            Upgrades.RaiderUpgrade upgrade;

            while(!poss.isEmpty()) {
                if(rand.nextBetween(0, cost = (upgrade = poss.get(index)).getCost(this)) > totalWeight) {
                    this.upgrades.add(upgrade.id());
                    newCost = upgrade.getCost(this);
                    totalCost += cost;
                    if(newCost >= 0) {
                        totalWeight += (newCost - cost); //newCost should be >= cost, 0, or -1 (-1 means cant apply anymore)
                        index++;
                    } else {
                        index = 0;
                        totalCost -= cost;
                    }
                }
            } //essentially using BFS to create a maze

            return totalCost;

//            if(poss.isEmpty()) return 0;
//            Collections.shuffle(poss);
//            RaiderUpgrade chosen = poss.size() == 1 ? poss.getFirst().getUpgrade() : poss.get(rand.nextInt(poss.size()-1)).getUpgrade();
//            upgrades.add(chosen);
//            if(chosen.applyCost(this,points)) points.set(points.get() - chosen.getCost().apply(this));
//            if(points.get() > 0 && !poss.isEmpty()) addUpgrades(points);
        }

        public Object2IntLinkedOpenHashMap<Upgrades.RaiderUpgrade> getWaveUpgrades() {
            return waveUpgrades;
        }

        public void setWaveUpgrades(Map<Upgrades.RaiderUpgrade, Integer> upgrades){
            waveUpgrades.clear();
            waveUpgrades.putAll(upgrades);
        }

        public static Vec3d randomVel(double xMax, double yMax, double zMax, Random rand){
            double dx = (double) rand.nextBetween((int) (xMax*-1000),(int) (xMax*1000))/1000d;
            double dy = (double) rand.nextBetween(0, (int) (yMax*1000))/1000d;
            double dz = (double) rand.nextBetween((int) (zMax*-1000),(int) (zMax*1000))/1000d;
            return new Vec3d(dx,dy,dz);
        }
    }
}