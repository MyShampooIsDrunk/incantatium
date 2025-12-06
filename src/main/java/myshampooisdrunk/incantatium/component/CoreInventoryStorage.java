package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.multiblock.registry.MultiblockRegistry;
import myshampooisdrunk.drunk_server_toolkit.world.MultiblockCacheI;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.ShrineMultiblock;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static myshampooisdrunk.incantatium.multiblock.ShrineMultiblock.RT2;

public class CoreInventoryStorage implements InventoryStorage {
    private final static int CRAFTING_TIME = 100;
    private final static int TURNS = 2;
    private final static double K = 2 * Math.PI * (double) TURNS / (double) CRAFTING_TIME;
    private final MarkerEntity core;
    private final MultiblockInventory inventory;
    private final ShrineMultiblock.PedestalEntry[] pedestals;
    private int ticks;

    public CoreInventoryStorage(MarkerEntity core) {
        this.core = core;
        this.inventory = new MultiblockInventory(8);
        this.ticks = -1;
        this.pedestals = new ShrineMultiblock.PedestalEntry[8];
        Arrays.fill(pedestals, null);
    }

    @Override
    public MultiblockInventory getInventory() {
        return inventory;
    }

    @Override
    public ShrineMultiblock.PedestalEntry[] getPedestals() {
        return pedestals;
    }

    @Override
    public void copyPedestals(ShrineMultiblock.PedestalEntry[] pedestals) {
        System.arraycopy(pedestals, 0, this.pedestals, 0, this.pedestals.length);
    }

    @Override
    public void startTimer() {
        ticks = CRAFTING_TIME;
    }

    @Override
    public boolean isTicking() {
        return ticks >= 0;
    }

    @Override
    public void update() {
        if(isTicking()) {
            cancel();
        } else {
            for (Identifier id : IncantatiumRegistry.MULTIBLOCK_RECIPES.keySet()) {
//                System.out.println("checking id " + id);
                if(IncantatiumRegistry.MULTIBLOCK_RECIPES.get(id).matches(inventory, core.getEntityWorld())) {
//                    System.out.println("matches");
                    startTimer();
                    break;
                }
            }
        }
    }

    @Override
    public void cancel() {
        ticks = -2;
    }

    @Override
    public void readData(ReadView readView) {
        this.inventory.readData(readView);
        ticks = readView.getInt("crafting_ticks", -1);
//        Incantatium.LOGGER.info("READ DATA!");
        for (ReadView slot : readView.getListReadView("slots")) {
            int s = slot.getInt("slot", -1);
            if(s != -1) {
//                Incantatium.LOGGER.info("READ SLOT!!! {}", s);
                Optional<UUID> item = slot.read("item", Uuids.CODEC);
                Optional<UUID> text = slot.read("text", Uuids.CODEC);
                Optional<UUID> interaction = slot.read("interaction", Uuids.CODEC);
                if(item.isPresent() && text.isPresent() && interaction.isPresent()) {
                    pedestals[s] = new ShrineMultiblock.PedestalEntry(item.get(), text.get(), interaction.get(), core.getEntityWorld());
                } //else Incantatium.LOGGER.info("COULDNT LOAD SLOT {}", s);
            }
        }
    }

    @Override
    public void writeData(WriteView writeView) {
        if(!inventory.isEmpty())
            this.inventory.writeData(writeView);
        writeView.putInt("crafting_ticks",ticks);


        boolean bl = true;

        for (ShrineMultiblock.PedestalEntry pedestal : pedestals) {
            if(pedestal == null) {
                bl = false;
                break;
            }
        }

//        Incantatium.LOGGER.info("WROTE DATA!");
        if(bl) {
            WriteView.ListView list = writeView.getList("slots");

            for (int i = 0; i < pedestals.length; i++) {
//                Incantatium.LOGGER.info("WROTE SLOT!!! {}", i);
                ShrineMultiblock.PedestalEntry entry = pedestals[i];
                WriteView slot = list.add();
                slot.putInt("slot", i);
                slot.put("item", Uuids.CODEC, entry.itemUuid());
                slot.put("text", Uuids.CODEC, entry.textUuid());
                slot.put("interaction", Uuids.CODEC, entry.interactionUuid());
            }
        }
    }

    @Override
    public void serverTick() {
        if(ticks >= 0) {
            tickCrafting();
            ticks--;
            if(ticks == -1) {
                ItemStack result;
                for (AbstractMultiblockRecipe recipe : IncantatiumRegistry.MULTIBLOCK_RECIPES.values()) {
                    if(recipe.matches(inventory, core.getEntityWorld()) && core.getEntityWorld() instanceof ServerWorld sw) {
                        result = recipe.craft(inventory, core.getRegistryManager());
                        ItemEntity item = new ItemEntity(core.getEntityWorld(), core.getX(), core.getY() + 4.5, core.getZ(), result);
                        item.setPickupDelay(20);
                        item.setNeverDespawn();
                        item.setNoGravity(true);
                        item.setVelocity(new Vec3d(0,-0.075,0));

                        for (int i = 0; i < 50; i++) {
                            double d = sw.random.nextGaussian() * 2;
                            double e = sw.random.nextGaussian() * 2;
                            double f = sw.random.nextGaussian() * 2;
                            sw.spawnParticles(ParticleTypes.END_ROD, core.getX(), core.getY() + 3.5, core.getZ(), 1, d, e, f, 0);
                        }
                        sw.playSound(null, core.getX(), core.getY() + 0.5, core.getZ(), SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 2f, 2);
                        sw.spawnEntity(item);
                        inventory.clear();
                        if (((MultiblockCacheI) core.getEntityWorld()).drunk_server_toolkit$getStructure(core.getUuid()) instanceof ShrineMultiblock shrine) {
                            for (int i = 0; i < 8; i++) {
                                shrine.getEntry(i).flatMap(ShrineMultiblock.PedestalEntry::item).ifPresent(e -> {
                                    e.getEntity().getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).setEntry(MultiblockInventory.EMPTY);
                                    e.markDirty(true);
                                    e.update();
                                });
                            }
                        }
                        break;
                    }
                }
            }
        }
        else if(ticks == -2) {
            if(core.getEntityWorld() instanceof ServerWorld sw) {
                for (int i = 0; i < 50; i++) {
                    double d = sw.random.nextGaussian() * 1.5;
                    double e = sw.random.nextGaussian() * 1.5;
                    double f = sw.random.nextGaussian() * 1.5;
                    sw.spawnParticles(ParticleTypes.ANGRY_VILLAGER, core.getX(), core.getY() + 1.5, core.getZ(), 1, d, e, f, 0);
                }
                sw.playSound(null, core.getX(), core.getY() + 0.5, core.getZ(), SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 2f, 0.75f);
            }
            ticks = -1;
        }
    }

    public void tickCrafting() {
        int t = CRAFTING_TIME - ticks;
        if(core.getEntityWorld() instanceof ServerWorld sw) {
            assert 0 <= t && t <= CRAFTING_TIME;
            double c = CRAFTING_TIME / 4d;
            double x = (CRAFTING_TIME - t) * Math.cos(K * t);
//            double dx = K * (t - CRAFTING_TIME) * Math.sin(K * t) - Math.cos(K * t);
            double y = 2 * t - t * t / (double) CRAFTING_TIME;
//            double dy = 2 - 2 * t / (double) CRAFTING_TIME;
            double z = (CRAFTING_TIME - t) * Math.sin(K * t);
//            double dz = K * (CRAFTING_TIME - t) * Math.cos(K * t) - Math.sin(K * t);
//            double N = K * K;
            x /= c;
            y /= c / 2d;
            z /= c;
//            dx *= N;
//            dy *= N;
//            dz *= N;
            double oldX = x, oldZ = z;
//            double oldDx = dx, oldDz = dz;
            for (int i = 0; i < 8; i++) {
                double rotX;
                double rotZ;
                switch (i) {
                    case 0:
                        rotX = 1;
                        rotZ = 0;
                        break;
                    case 1:
                        rotX = (rotZ = RT2 / 2d);
                        break;
                    case 2:
                        rotX = 0;
                        rotZ = 1;
                        break;
                    case 3:
                        rotX = -(rotZ = RT2 / 2d);
                        break;
                    case 4:
                        rotX = -1;
                        rotZ = 0;
                        break;
                    case 5:
                        rotX = (rotZ = -RT2 / 2d);
                        break;
                    case 6:
                        rotX = 0;
                        rotZ = -1;
                        break;
                    case 7:
                        rotX = -(rotZ = -RT2 / 2d);
                        break;
                    default:
                        rotX = 1;
                        rotZ = 1;
                        break;
                }
                x = oldX * rotX + oldZ * rotZ;
                z = oldZ * rotX - oldX * rotZ;
//                dx = oldDx * rotX + oldDz * rotZ;
//                dz = oldDz * rotX - oldDx * rotZ;
                sw.spawnParticles(ParticleTypes.WITCH, core.getX() + x, core.getY() + y - 3, core.getZ() + z, 1, 0, 0, 0, 0);
            }
            if(t % 5 == 0) {
                sw.playSound(null, core.getX(), core.getY() + 0.5, core.getZ(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.25f, 0.5f + t/50f);
            }
        }
    }
}
