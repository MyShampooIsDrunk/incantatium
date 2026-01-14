package myshampooisdrunk.incantatium.raid.upgrades;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.ornaments.SalvationOrnamentItem;
import myshampooisdrunk.incantatium.raid.RaidWave;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

public class Upgrades {
    /**
     <p>vindicators get enchantments, better axes, etc. in 1.21.11 they'll potentially get spears too</p>
     <p>pillagers get better/more enchantments(including normally incompatible ones like power) + different projectiles (potion arrows, fireworks) </p>
     <p>evokers get more spells and base stats </p>
     */
    public static final RaiderUpgrade WEAPON;

    /**
     <p>... gives them armor lol</p>
     <p>higher levels -> better armor + enchantments</p>
     */
    public static final RaiderUpgrade ARMOR;

    /**
     <p>gives raiders offhand totems... </p>
     <p>ill try to not make them offhand bc i want them to be able to hold offhand items but who knows </p>
     */
    public static final RaiderUpgrade TOTEM;

    /**
     <p>gives raiders beneficial potion effects </p>
     <p>level 1 gives fire res + water breathing 100% of the time </p>
     <p>vindicators -> strength, speed, resistance, regeneration </p>
     <p>evokers, pillagers -> speed, resistance, regeneration </p>
     */
    public static final RaiderUpgrade EFFECT;

    /**
     <p>upgrades base raiders from vindicators/pillagers -> evokers</p>
     */
    public static final RaiderUpgrade ENTITY;

    /**
     <p> gives raiders pets</p>
     <p> chooses between (40%) witch, (35%) ravager, (10%) guardian, (10%) bee, (5%) breeze </p>
     <p> if the pet has shoots projectiles (witch, ravager, guardian, breeze) and the raider has a 2-seated mount it'll summon it on the mount instead </p>
     */
    public static final RaiderUpgrade PET;
    /*
    TODO:
     - implement
     - make them not attack each other/consider the raiders and each other teammates
     - do the mount stuff
     */

    /**
     <p> gives raiders mounts</p>
     <p> chooses between (40%) ravager, (20%) camel (husk camel in 1.21.11), (20%) zombie horse, (7.5%) cave spider, (5%) blaze, (5%) breeze, and (2.5%) bat </p>
     */
    public static final RaiderUpgrade MOUNT;
    /*
    TODO:
     - implement
     - allow the rider to control them
     - do the pet stuff
     */

    public static final RaiderUpgrade NULL;

    public abstract static class RaiderUpgrade {
        public static final Map<String, RaiderUpgrade> UPGRADES = new HashMap<>();
        protected final String id;

        protected RaiderUpgrade(String id) {
            UPGRADES.put(id, this);
            this.id = id;
        }

        public abstract void apply(RaidWave.RaiderEntry entry);

        /**
         <p><b> -1 </b> -> can't be bought</p>
         <p><b> 0 </b> -> free</p>
         <p><b> >0 </b> -> that's how much it costs</p>
        */
        public abstract int getCost(int upgradeLevel, RaidWave.RaiderEntry entry);

        /**
         used to sort the upgrades in order
         */
        public abstract int sortWeight();

        public final String id() {
            return id;
        }

        public static RaiderUpgrade getUpgrade(String id) {
            return UPGRADES.getOrDefault(id, NULL);
        }
    }


    static {
        WEAPON = new RaiderUpgrade("WEAPON") { //max 7
            @Override
            public void apply(RaidWave.RaiderEntry entry) {
                int upgradeLevel = entry.getUpgradeCount(this.id);
                if(upgradeLevel <= 0) return;
                RaiderEntity raider = entry.getRaider();
                Random rand = raider.getRandom();

                RegistryEntry<Enchantment> vanishing = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.VANISHING_CURSE).orElseThrow();

                RegistryEntry<Enchantment> power = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.POWER).orElseThrow(); //max 3
                RegistryEntry<Enchantment> pierce = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.PIERCING).orElseThrow(); //max 6
                RegistryEntry<Enchantment> flame = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.FLAME).orElseThrow(); // max 1
                RegistryEntry<Enchantment> punch = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.PUNCH).orElseThrow(); //max 2
                RegistryEntry<Enchantment> multishot = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.MULTISHOT).orElseThrow(); //max 4
                RegistryEntry<Enchantment> quick = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.QUICK_CHARGE).orElseThrow(); //max 4
                //20

                RegistryEntry<Enchantment> sharpness = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.SHARPNESS).orElseThrow();// max 5
                RegistryEntry<Enchantment> fire = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.FIRE_ASPECT).orElseThrow();// 2 or 3
                RegistryEntry<Enchantment> knockback = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.KNOCKBACK).orElseThrow();// max 2
                //9

                Object2IntMap<RegistryEntry<Enchantment>> possibleEnchants = new Object2IntLinkedOpenHashMap<>();
                Object2IntMap<RegistryEntry<Enchantment>> finalEnchants = new Object2IntLinkedOpenHashMap<>();
                int points = 0;

                ItemStack stack = null;
                ItemEnchantmentsComponent.Builder enchants = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
                enchants.set(vanishing, 0);
                ItemEnchantmentsComponent.Builder vanishingComponent = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
                vanishingComponent.set(vanishing, 0);

                switch (raider) {
                    case VindicatorEntity vindicator -> {
                        points = (int) (1.5d * upgradeLevel);
                        if(upgradeLevel > 1) stack = Items.NETHERITE_AXE.getDefaultStack();
                        else stack = Items.DIAMOND_AXE.getDefaultStack();
                        possibleEnchants.put(sharpness, 4);
                        possibleEnchants.put(fire, 1);//add 1 after
                        possibleEnchants.put(knockback, 1);
                    }
                    case PillagerEntity pillager -> {
                        stack = Items.CROSSBOW.getDefaultStack();
                        points = (int) (3d * Math.min(upgradeLevel, 6));
                        possibleEnchants.put(power, 2);
                        possibleEnchants.put(punch, 1);
                        possibleEnchants.put(pierce, 5);
                        possibleEnchants.put(flame, 0);
                        possibleEnchants.put(multishot, 3);
                        possibleEnchants.put(quick, 3);
                        if(upgradeLevel == 7) {
                            int choice = rand.nextBetween(0,3);
                            ItemStack projectile = switch (choice) {
                                case 0 -> {
                                    ItemStack ret = Items.TIPPED_ARROW.getDefaultStack();
                                    ret.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(),
                                            Optional.of(4673362),
                                            List.of(new StatusEffectInstance(StatusEffects.BLINDNESS, 20)),
                                            Optional.empty()));
                                    yield ret;
                                }

                                case 1 -> {
                                    ItemStack ret = Items.TIPPED_ARROW.getDefaultStack();
                                    ret.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(),
                                            Optional.of(4673362),
                                            List.of(new StatusEffectInstance(StatusEffects.DARKNESS, 100)),
                                            Optional.empty()));
                                    yield ret;
                                }

                                case 2 -> {
                                    ItemStack ret = Items.TIPPED_ARROW.getDefaultStack();
                                    ret.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(),
                                            Optional.of(4673362),
                                            List.of(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 50)),
                                            Optional.empty()));
                                    yield ret;
                                }

                                default -> {
                                    ItemStack ret = Items.TIPPED_ARROW.getDefaultStack();
                                    ret.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(),
                                            Optional.of(4673362),
                                            List.of(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1)),
                                            Optional.empty()));
                                    yield ret;
                                }
                            };

                            projectile.set(DataComponentTypes.MAX_STACK_SIZE, 99);
                            projectile.setCount(99);
                            projectile.set(DataComponentTypes.ENCHANTMENTS, vanishingComponent.build());
                            pillager.setStackInHand(Hand.OFF_HAND, projectile.copy());
                        }
                    }
                    case EvokerEntity evoker -> evoker.getComponent(Incantatium.STRONGER_EVOKERS_COMPONENT_KEY).setBuffLevel(upgradeLevel);
                    default -> {}
                }

                List<RegistryEntry<Enchantment>> availableEnchants = new ArrayList<>(possibleEnchants.keySet());

                if(points > 0) {
                    while (points > 0) {
                        if(availableEnchants.isEmpty()) break;
                        RegistryEntry<Enchantment> key;

                        int index;
                        key = availableEnchants.get(index = rand.nextBetween(0, availableEnchants.size() - 1));
                        finalEnchants.put(key, possibleEnchants.getOrDefault(key, 0) + 1);
                        points--;
                        if(finalEnchants.getInt(key) >= possibleEnchants.getInt(key)) availableEnchants.remove(index);
                    }
                    finalEnchants.computeInt(fire, (e,l) -> l + 1);
                    finalEnchants.forEach(enchants::set);
                }
                if(!(raider instanceof EvokerEntity) && stack != null) {
                    stack.set(DataComponentTypes.ENCHANTMENTS, enchants.build());
                    raider.setStackInHand(Hand.MAIN_HAND, stack.copy());
                }

            }

            @Override
            public int getCost(int upgradeLevel, RaidWave.RaiderEntry entry) {
                return upgradeLevel > 7 ? -1 : upgradeLevel * upgradeLevel * 12;
            }

            @Override
            public int sortWeight() {
                return 6;
            }
        };

        //TODO: instead of using different armor pieces just make 1 custom armor pieces with like a metric fuck ton of stats that way they can have up to like 30 armor in theory
        //max level: 8
        ARMOR = new RaiderUpgrade("ARMOR") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {
                int upgradeLevel = entry.getUpgradeCount(this.id);
                if(upgradeLevel <= 0) return;

                RaiderEntity raider = entry.getRaider();
                Random rand = raider.getRandom();
                RegistryEntry<Enchantment> vanishing = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.VANISHING_CURSE).orElseThrow();
                RegistryEntry<Enchantment> feather = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.FEATHER_FALLING).orElseThrow();
                RegistryEntry<Enchantment> prot = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.VANISHING_CURSE).orElseThrow();
                RegistryEntry<Enchantment> blastProt = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.BLAST_PROTECTION).orElseThrow();

                int armorLevel = Math.clamp(upgradeLevel, 1, 4);

                //levels 1 - 4 -> armor
                /* -0.5x^2 + 8.5x - 1
                * 1 -> 7 armor
                * 2 -> 14 armor
                * 3 -> 20 armor
                * 4 -> 25 armor
                */
                //levels 5 - 8 -> enchants


                ItemEnchantmentsComponent.Builder enchants = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
                enchants.set(vanishing, 0);
                if(upgradeLevel >= 5) {
                    int enchantLevel = upgradeLevel - 4;
                    enchants.set(feather, 4);
                    enchants.set(blastProt, enchantLevel * 4);
                    enchants.set(prot, enchantLevel * 4);
                }

                ItemStack armorItem = Items.CLOCK.getDefaultStack();
                armorItem.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder()
                        .add(EntityAttributes.SAFE_FALL_DISTANCE, new EntityAttributeModifier(Incantatium.id("safe_fall"), 10d * upgradeLevel, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.FEET)
                        .add(EntityAttributes.ARMOR, new EntityAttributeModifier(Incantatium.id("armor"), (armorLevel * armorLevel * -0.5d + 8.5d * armorLevel - 1d), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.FEET)
                        .build());
                armorItem.set(DataComponentTypes.ENCHANTMENTS, enchants.build());

                raider.equipStack(EquipmentSlot.FEET, armorItem);
            }

            @Override
            public int getCost(int upgradeLevel, RaidWave.RaiderEntry entry) {
                return upgradeLevel > 8 ? -1 : 16 * ((upgradeLevel - 1) * (upgradeLevel - 1) + 5);
                //10x^3 - 50x^2 +100x - 50
            }

            @Override
            public int sortWeight() {
                return 5;
            }
        };

        TOTEM = new RaiderUpgrade("TOTEM") {

            @Override
            public void apply(RaidWave.RaiderEntry entry) {
                int upgradeLevel = entry.getUpgradeCount(this.id);
                if(upgradeLevel <= 0) return;
                RaiderEntity raider = entry.getRaider();
                RegistryEntry<Enchantment> vanishing = raider.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.VANISHING_CURSE).orElseThrow();
                ItemEnchantmentsComponent.Builder vanishingComponent = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
                vanishingComponent.set(vanishing, 0);
                ItemStack totem;
                DeathProtectionComponent totemComponent = SalvationOrnamentItem.DEATH_PROTECTION;
                int count = 1;
                if((totem = raider.getEquippedStack(EquipmentSlot.SADDLE)) != null && totem.contains(DataComponentTypes.DEATH_PROTECTION)) {
                    count = totem.getCount() + 1;
                }
                totem = Items.CLOCK.getDefaultStack();
                totem.set(DataComponentTypes.DEATH_PROTECTION, totemComponent);
                totem.setCount(count);
                totem.set(DataComponentTypes.ENCHANTMENTS, vanishingComponent.build());
                raider.equipStack(EquipmentSlot.SADDLE, totem);
            }

            @Override
            public int getCost(int upgradeLevel, RaidWave.RaiderEntry entry) {
                return upgradeLevel == 26 ? -1 : upgradeLevel * upgradeLevel - 4 * upgradeLevel * upgradeLevel + 14;
            }

            @Override
            public int sortWeight() {
                return 4;
            }
        };

        //max level 7
        EFFECT = new RaiderUpgrade("EFFECT") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {
                int upgradeLevel = entry.getUpgradeCount(this.id);
                if(upgradeLevel <= 0) return;

                int points;

                RaiderEntity raider = entry.getRaider();

                if(upgradeLevel == 1) {
                    raider.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, -1));
                    raider.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, -1));
                    return;
                }

                Random random = raider.getRandom();

                Object2IntMap<RegistryEntry<StatusEffect>> goodPotions = new Object2IntLinkedOpenHashMap<>();
                Object2IntMap<RegistryEntry<StatusEffect>> effects = new Object2IntLinkedOpenHashMap<>();

                goodPotions.put(StatusEffects.RESISTANCE, 1);
                goodPotions.put(StatusEffects.REGENERATION, 1);
                goodPotions.put(StatusEffects.SPEED, 2);
                goodPotions.put(StatusEffects.HEALTH_BOOST, 3);// * 3 at the end
                if(raider instanceof VindicatorEntity) {
                    goodPotions.put(StatusEffects.STRENGTH, 2);
                    points = 2 * (upgradeLevel - 1);
                } else points = (int) (1.5d * (upgradeLevel - 1));

                List<RegistryEntry<StatusEffect>> available = new ArrayList<>(goodPotions.keySet());

                if(points > 0) {
                    while (points > 0) {
                        if(available.isEmpty()) break;
                        RegistryEntry<StatusEffect> key;

                        int index;
                        key = available.get(index = random.nextBetween(0, available.size() - 1));
                        effects.put(key, effects.getOrDefault(key, -1) + 1);
                        points--;
                        if(effects.getInt(key) >= goodPotions.getInt(key)) available.remove(index);
                    }

                    effects.forEach((e, l) -> raider.addStatusEffect(
                            new StatusEffectInstance(e, -1, (e == StatusEffects.HEALTH_BOOST ? 3 : 1) * l)));
                }
            }

            @Override
            public int getCost(int upgradeLevel, RaidWave.RaiderEntry entry) {
                return upgradeLevel > 7 ? -1 : upgradeLevel * upgradeLevel * 15;
            }

            @Override
            public int sortWeight() {
                return 0;
            }
        };

        ENTITY = new RaiderUpgrade("ENTITY") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {
                RaiderEntity raider = entry.getRaider();
                if(raider instanceof VindicatorEntity || raider instanceof PillagerEntity){
                    entry.setRaider(new EvokerEntity(EntityType.EVOKER, raider.getEntityWorld()));
                }
            }

            @Override
            public int getCost(int upgradeLevel, RaidWave.RaiderEntry entry) {
                return upgradeLevel > 0 ? -1 : (entry.getWaveUpgradeCount(id) > 40 ? -1 : 125);
            }

            @Override
            public int sortWeight() {
                return 7;
            }
        };

        PET = new RaiderUpgrade("PET") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {
                MobEntity pet;
                RaiderEntity raider = entry.getRaider();
                World world = raider.getEntityWorld();
                Random random = raider.getRandom();
                int n = random.nextBetween(1, 100);
                if (n <= 40) {
                    pet = EntityType.WITCH.create(world, SpawnReason.EVENT);
                } else if (n <= 75) {
                    pet = EntityType.RAVAGER.create(world, SpawnReason.EVENT);
                } else if (n <= 85) {
                    pet = EntityType.GUARDIAN.create(world, SpawnReason.EVENT);
                } else if (n <= 95) {
                    pet = EntityType.BEE.create(world, SpawnReason.EVENT);
                } else {
                    pet = EntityType.BREEZE.create(world, SpawnReason.EVENT);
                }
                entry.setPet(pet);
            }

            @Override
            public int getCost(int upgradeLevel, RaidWave.RaiderEntry entry) {
                return upgradeLevel > 0 ? -1 : 100;
            }

            @Override
            public int sortWeight() {
                return 1;
            }
        };

        MOUNT = new RaiderUpgrade("MOUNT") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {
                MobEntity mount;
                RaiderEntity raider = entry.getRaider();
                World world = raider.getEntityWorld();
                Random random = raider.getRandom();
                int n = random.nextBetween(1, 100);

                if (n <= 40) {
                    mount = EntityType.RAVAGER.create(world, SpawnReason.EVENT);
                } else if (n <= 60) {
                    mount = EntityType.CAMEL.create(world, SpawnReason.EVENT);
                } else if (n <= 80) {
                    mount = EntityType.ZOMBIE_HORSE.create(world, SpawnReason.EVENT);
                } else if (n <= 88) {
                    mount = EntityType.CAVE_SPIDER.create(world, SpawnReason.EVENT);
                } else if (n <= 93) {
                    mount = EntityType.BLAZE.create(world, SpawnReason.EVENT);
                } else if (n <= 98) {
                    mount = EntityType.BREEZE.create(world, SpawnReason.EVENT);
                } else {
                    mount = EntityType.BAT.create(world, SpawnReason.EVENT);
                }

                entry.setMount(mount);
            }

            @Override
            public int getCost(int upgradeLevel, RaidWave.RaiderEntry entry) {
                return upgradeLevel > 0 ? -1 : 75;
            }

            @Override
            public int sortWeight() {
                return 2;
            }
        };

        NULL = new RaiderUpgrade("NULL") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {
            }

            @Override
            public int getCost(int upgradeLevel, RaidWave.RaiderEntry entry) {
                return -1;
            }

            @Override
            public int sortWeight() {
                return -1;
            }
        };
    }
}
