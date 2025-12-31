package myshampooisdrunk.incantatium.raid.upgrades;

import myshampooisdrunk.incantatium.raid.RaidWave;

import java.util.HashMap;
import java.util.Map;

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
        private final String id;

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
        public abstract int getCost(RaidWave.RaiderEntry entry);

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


    static {//TODO: WEAPON, ARMOR, TOTEM, EFFECT, ENTITY, PET, MOUNT
        WEAPON = new RaiderUpgrade("WEAPON") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {
                
            }

            @Override
            public int getCost(RaidWave.RaiderEntry entry) {
                return 0;
            }

            @Override
            public int sortWeight() {
                return 6;
            }
        };

        ARMOR = new RaiderUpgrade("ARMOR") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {
                
            }

            @Override
            public int getCost(RaidWave.RaiderEntry entry) {
                return 0;
            }

            @Override
            public int sortWeight() {
                return 5;
            }
        };

        TOTEM = new RaiderUpgrade("TOTEM") {

            @Override
            public void apply(RaidWave.RaiderEntry entry) {

            }

            @Override
            public int getCost(RaidWave.RaiderEntry entry) {
                return 0;
            }

            @Override
            public int sortWeight() {
                return 4;
            }
        };

        EFFECT = new RaiderUpgrade("EFFECT") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {

            }

            @Override
            public int getCost(RaidWave.RaiderEntry entry) {
                return 0;
            }

            @Override
            public int sortWeight() {
                return 0;
            }
        };

        ENTITY = new RaiderUpgrade("ENTITY") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {

            }

            @Override
            public int getCost(RaidWave.RaiderEntry entry) {
                return 0;
            }

            @Override
            public int sortWeight() {
                return 7;
            }
        };

        PET = new RaiderUpgrade("PET") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {

            }

            @Override
            public int getCost(RaidWave.RaiderEntry entry) {
                return 0;
            }

            @Override
            public int sortWeight() {
                return 1;
            }
        };

        MOUNT = new RaiderUpgrade("MOUNT") {
            @Override
            public void apply(RaidWave.RaiderEntry entry) {

            }

            @Override
            public int getCost(RaidWave.RaiderEntry entry) {
                return 0;
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
            public int getCost(RaidWave.RaiderEntry entry) {
                return -1;
            }

            @Override
            public int sortWeight() {
                return -1;
            }
        };
    }
}
