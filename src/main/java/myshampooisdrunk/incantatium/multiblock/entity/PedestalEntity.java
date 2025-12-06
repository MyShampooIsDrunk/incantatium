package myshampooisdrunk.incantatium.multiblock.entity;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockEntityType;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.drunk_server_toolkit.world.MultiblockCacheI;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.InventorySlotStorage;
import myshampooisdrunk.incantatium.component.InventoryStorage;
import myshampooisdrunk.incantatium.component.PedestalInteraction;
import myshampooisdrunk.incantatium.component.PedestalInventoryStorage;
import myshampooisdrunk.incantatium.multiblock.ShrineMultiblock;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

public abstract class PedestalEntity<E extends Entity, T extends PedestalEntity<E,T>> extends MultiblockEntity<E,T> {
    protected final int slot;

    public PedestalEntity(MultiblockEntityType<E, T> type, World world, E entity) {
        super(type, world, entity);
        this.slot = initSlot(entity);
    }

    public PedestalEntity(MultiblockEntityType<E, T> type, World world, int slot) {
        super(type, world);
        this.slot = slot;
    }

    abstract int initSlot(E entity);

    public final int getSlot() {
        return this.slot;
    }

    public final Optional<ShrineMultiblock.PedestalEntry> getEntry() {
        if(slot >= 0 && slot < 8 && ((MultiblockCacheI) world).drunk_server_toolkit$getStructure(coreUuid) instanceof ShrineMultiblock shrine) {
            return shrine.getEntry(slot);
        } else return Optional.empty();
    }

    @Override
    public E create(MultiblockStructure structure, BlockPos center, Vec3d relative, SpawnReason reason) {
        return super.create(structure, center, relative.add(0.5,-2,0.5), reason);
    }

    public static Vec3d[] offsets(double radius) {
        Vec3d[] offsets = new Vec3d[8];
        for (double i = 0; i < 2; i+=0.25) {
            offsets[(int)(i*4)] = new Vec3d(Math.cos(Math.PI * i),0,Math.sin(Math.PI*i)).multiply(radius);
        }
        return offsets;
    }

    public static MultiblockEntityType.Factory<DisplayEntity.ItemDisplayEntity, PedestalItemEntity> getFactoryPItem() {
        return PedestalItemEntity::new;
    }

    public static MultiblockEntityType.Factory<DisplayEntity.TextDisplayEntity, PedestalTextEntity> getFactoryPTxt() {
        return PedestalTextEntity::new;
    }

    public static MultiblockEntityType.Factory<InteractionEntity, PedestalInteractionEntity> getFactoryPInt() {
        return PedestalInteractionEntity::new;
    }

    public static class PedestalItemEntity extends PedestalEntity<DisplayEntity.ItemDisplayEntity, PedestalItemEntity> {

        private boolean dirty;

        public PedestalItemEntity(MultiblockEntityType<DisplayEntity.ItemDisplayEntity, PedestalItemEntity> type, World world, DisplayEntity.ItemDisplayEntity entity) {
            super(type, world, entity);
            this.dirty = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).isDirty();
        }

        public PedestalItemEntity(MultiblockEntityType<DisplayEntity.ItemDisplayEntity, PedestalItemEntity> type, World world, int slot) {
            super(type, world, slot);
            this.dirty = false;
        }

        @Override
        int initSlot(DisplayEntity.ItemDisplayEntity entity) {
            return entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).getSlot();
        }

        @Override
        public void initializeFromData(DisplayEntity.ItemDisplayEntity entity) {
            super.initializeFromData(entity);
//            if(((MultiblockCacheI) world).drunk_server_toolkit$getStructure(coreUuid) instanceof ShrineMultiblock shrine) {
//                ShrineMultiblock.PedestalEntry entry = shrine.getEntry(this.slot).orElse(new ShrineMultiblock.PedestalEntry(null, null, null));
//                if(entry.itemEntity() == null) {
//                    entry = entry.withItem(this);
//                    shrine.setEntry(this.slot, entry);
//                }
//            }
        }

        public void markDirty(boolean dirty) {
            this.dirty = dirty;
            this.entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).markDirty(dirty);
        }

        public boolean isDirty() {
            return dirty;
        }

        public void update() {
            if(dirty && slot != -1) {
                InventorySlotStorage storage = this.entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
                entity.setItemStack(storage.getEntry().stack());
                MultiblockInventory.Singleton e = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).getEntry();
                MutableText ret = Text.empty();
                if(!e.isEmpty()) {
                    ret.append(e.stack().getItemName()).append(Text.literal("\n" + e.count()));
                }

                getEntry().flatMap(ShrineMultiblock.PedestalEntry::text).ifPresent(t -> t.getEntity().setText(ret));
                if(world.getEntity(coreUuid) instanceof MarkerEntity marker) {
                    InventoryStorage coreStorage = marker.getComponent(Incantatium.INVENTORY_STORAGE_COMPONENT_KEY);
                    coreStorage.setSlot(e, this.slot);
                    coreStorage.update();
                }
            }
        }

        @Override
        public DisplayEntity.ItemDisplayEntity create(MultiblockStructure structure, BlockPos center, Vec3d relative, SpawnReason reason) {
            Vec3d spawnPos = relative.add(0.5,0,0.5);
            DisplayEntity.ItemDisplayEntity ret = super.create(structure, center, spawnPos, reason);
            ret.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).initialize(this);
            ret.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
            ret.setItemDisplayContext(ItemDisplayContext.GUI);
            ret.setTransformation(new AffineTransformation(new Vector3f(), new Quaternionf(0,1,0,0), new Vector3f(0.5f,0.5f,0.5f), new Quaternionf()));
            ret.refreshPositionAndAngles(spawnPos.add(new Vec3d(center)),0 ,-90);
            return ret;
            //TODO: inventory stuff
        }

        @Override
        public void onInteract(PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
            super.onInteract(user, hand, cir);
            ItemStack handStack = null;
            if(user.getActiveHand() == hand && !(handStack = user.getStackInHand(hand)).isEmpty()) {
//                System.out.println("player interacted with item " + handStack);

                PedestalInventoryStorage p = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
                MultiblockInventory temp = new MultiblockInventory(1);
                temp.set(p.getEntry(), 0);
                int count = user.isSneaking() ? Math.min(handStack.getCount(),handStack.getMaxCount()) : 1;
                if(temp.addStack(0, handStack.copyWithCount(count))) {
//                    System.out.println("b");
                    handStack.decrementUnlessCreative(count, user);
                    cir.setReturnValue(ActionResult.SUCCESS);
                    p.setEntry(temp.get(0));
                    markDirty(true);
                    this.update();
//                    Objects.requireNonNull(getEntry()).textEntity().getEntity().getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).withItemDisplay(entity).update();
                } else {
                    cir.setReturnValue(ActionResult.FAIL);
                }
            } else if(user.getActiveHand() == hand && handStack != null && handStack.isEmpty()) {
                cir.setReturnValue(ActionResult.PASS);
            }
        }

        @Override
        public void handleAttack(Entity attacker, CallbackInfoReturnable<Boolean> cir) {
            super.handleAttack(attacker, cir);

            if(attacker instanceof PlayerEntity player) {
//                System.out.println("player interacted with item " + handStack);

                PedestalInventoryStorage p = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
                MultiblockInventory.Singleton e = p.getEntry();
                if(e.isEmpty()) return;
                MultiblockInventory temp = new MultiblockInventory(1);
                temp.set(e, 0);
                int count = player.isSneaking() ? e.stack().getMaxCount() : 1;
                ItemStack ret;
                if((ret = temp.removeStack(0, count)) != null) {
                    cir.setReturnValue(true);
                    player.giveItemStack(ret);
                    p.setEntry(temp.get(0));
                    p.markDirty(true);
                    this.update();
                } else {
                    cir.setReturnValue(false);
                }
            }
        }

        @Override
        public void onRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
            super.onRemoved(reason, ci);
            PedestalInventoryStorage p = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
            MultiblockInventory.Singleton e = p.getEntry();
            if(e.isEmpty()) return;
            MultiblockInventory temp = new MultiblockInventory(1);
            temp.set(e, 0);
            if(entity.getEntityWorld() instanceof ServerWorld s) {
                for (ItemStack stack : temp.toStacks()) {
                    ItemEntity item = new ItemEntity(s, entity.getX(), entity.getY(), entity.getZ(), stack);
                    item.setPickupDelay(40);
                    float f = s.random.nextFloat() * 0.5F;
                    float g = s.random.nextFloat() * (float) (Math.PI * 2);
                    item.setVelocity(-MathHelper.sin(g) * f, 0.2F, MathHelper.cos(g) * f);
                    s.spawnEntity(item);
                }
            }
        }
    }

    public static class PedestalTextEntity extends PedestalEntity<DisplayEntity.TextDisplayEntity, PedestalTextEntity> {

        public PedestalTextEntity(MultiblockEntityType<DisplayEntity.TextDisplayEntity, PedestalTextEntity> type, World world, DisplayEntity.TextDisplayEntity entity) {
            super(type, world, entity);
        }

        public PedestalTextEntity(MultiblockEntityType<DisplayEntity.TextDisplayEntity, PedestalTextEntity> type, World world, int slot) {
            super(type, world, slot);
        }

        @Override
        int initSlot(DisplayEntity.TextDisplayEntity entity) {
            return entity.getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).getSlot();
        }

        @Override
        public void initializeFromData(DisplayEntity.TextDisplayEntity entity) {
            super.initializeFromData(entity);
//            if(((MultiblockCacheI) world).drunk_server_toolkit$getStructure(coreUuid) instanceof ShrineMultiblock shrine) {
//                ShrineMultiblock.PedestalEntry entry = shrine.getEntry(this.slot).orElse(new ShrineMultiblock.PedestalEntry(null, null, null));
//                if(entry.textEntity() == null) {
//                    entry = entry.withText(this);
//                    shrine.setEntry(this.slot, entry);
//                }
//            }
        }

        @Override
        public DisplayEntity.TextDisplayEntity create(MultiblockStructure structure, BlockPos center, Vec3d relative, SpawnReason reason) {
            DisplayEntity.TextDisplayEntity ret = super.create(structure, center, relative.add(0,2.25,0), reason);
            ret.getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).initialize(this);
            ret.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
            ret.setDisplayWidth(100);
            ret.setBackground(0);
            ret.setTransformation(new AffineTransformation(new Vector3f(), new Quaternionf(), new Vector3f(0.5f,0.5f,0.5f), new Quaternionf()));
            return ret;
            //TODO: inventory stuff
        }
    }

    public static class PedestalInteractionEntity extends PedestalEntity<InteractionEntity, PedestalInteractionEntity> {

        public PedestalInteractionEntity(MultiblockEntityType<InteractionEntity, PedestalInteractionEntity> type, World world, InteractionEntity entity) {
            super(type, world, entity);
        }

        public PedestalInteractionEntity(MultiblockEntityType<InteractionEntity, PedestalInteractionEntity> type, World world, int slot) {
            super(type, world, slot);
        }

        @Override
        int initSlot(InteractionEntity entity) {
            return entity.getComponent(Incantatium.PEDESTAL_INTERACTION_COMPONENT_KEY).getSlot();
        }

        @Override
        public void onInteract(PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
            Optional<ShrineMultiblock.PedestalEntry> entry = getEntry();
            entry.flatMap(ShrineMultiblock.PedestalEntry::item).ifPresentOrElse(e -> e.onInteract(user, hand, cir), () -> Incantatium.LOGGER.info("couldnt find item"));
        }

        @Override
        public void handleAttack(Entity attacker, CallbackInfoReturnable<Boolean> cir) {
            Optional<ShrineMultiblock.PedestalEntry> entry = getEntry();
            entry.flatMap(ShrineMultiblock.PedestalEntry::item).ifPresent(e -> e.handleAttack(attacker, cir));
        }

        @Override
        public void initializeFromData(InteractionEntity entity) {
            super.initializeFromData(entity);
//            if(((MultiblockCacheI) world).drunk_server_toolkit$getStructure(coreUuid) instanceof ShrineMultiblock shrine) {
//                ShrineMultiblock.PedestalEntry entry = shrine.getEntry(this.slot).orElse(new ShrineMultiblock.PedestalEntry(null, null, null));
//                if(entry.interactionEntity() == null) {
//                    entry = entry.withInteraction(this);
//                    shrine.setEntry(this.slot, entry);
//                }
//            }
        }

        @Override
        public InteractionEntity create(MultiblockStructure structure, BlockPos center, Vec3d relative, SpawnReason reason) {
            InteractionEntity ret = super.create(structure, center, relative, reason);
            ret.getComponent(Incantatium.PEDESTAL_INTERACTION_COMPONENT_KEY).initialize(this);
            ret.setInteractionHeight(1.5f);
            ret.setInteractionWidth(1);
            ret.setResponse(true);
            return ret;
        }
    }
}

//TODO: fix the bug where loading multiblock + break or smth not work idk anymore | align item entity pos | fix the fuck outta text entity pos lol