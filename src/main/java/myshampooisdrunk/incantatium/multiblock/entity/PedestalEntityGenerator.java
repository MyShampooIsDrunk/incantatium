package myshampooisdrunk.incantatium.multiblock.entity;

import myshampooisdrunk.drunk_server_toolkit.DST;
import myshampooisdrunk.drunk_server_toolkit.component.DSTComponentRegistry;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.PedestalInventoryStorage;
import myshampooisdrunk.incantatium.multiblock.IncantatiumMultiblockRegistry;
import myshampooisdrunk.incantatium.multiblock.ShrineMultiblock;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

public class PedestalEntityGenerator {
    public static void attach(double radius, ShrineMultiblock multiblock) {
        Vec3d[] offsets = new Vec3d[8];
        IncantatiumMultiblockRegistry.Pedestal[] ps = IncantatiumMultiblockRegistry.registerEntities();
        for (double i = 0; i < 2; i+=0.25) {
            Vec3d offset = new Vec3d(Math.cos(Math.PI * i),-0.5,Math.sin(Math.PI*i)).multiply(radius);
            int slot = (int)(4 * i);
//            IncantatiumMultiblockRegistry.Pedestal p = IncantatiumMultiblockRegistry.PEDESTAL_ENTITIES[slot];
            IncantatiumMultiblockRegistry.Pedestal p = ps[slot];
            multiblock.attachPedestalEntities(offset,p.storage(),p.text(),p.interaction(),slot);
//            offsets[(int)(i*4)] = new Vec3d(Math.cos(Math.PI * i),0,Math.sin(Math.PI*i)).multiply(radius);
        }

//        multiblock.attachPedestalEntity();
    }

    public static class PedestalEntity extends AbstractMultiblockStructureEntity<DisplayEntity.ItemDisplayEntity> {
        private final int slot;

        public PedestalEntity(String id, int slot) {
            super(EntityType.ITEM_DISPLAY, id);
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        @Override
        public DisplayEntity.ItemDisplayEntity create(ServerWorld world, MultiblockStructure structure, BlockPos center, Vec3d relative) {
            Vec3d spawnPos = relative.add(center.toCenterPos());
            DisplayEntity.ItemDisplayEntity ret = super.create(world, structure, center, relative);
            ret.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).setStorageSlot(slot);
            ret.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
            ret.setTransformationMode(ModelTransformationMode.GUI);
            ret.setTransformation(new AffineTransformation(new Vector3f(), new Quaternionf(0,1,0,0), new Vector3f(0.5f,0.5f,0.5f), new Quaternionf()));
            ret.refreshPositionAndAngles(spawnPos,0 ,-90);
            return ret;
            //TODO: inventory stuff
        }

        @Override
        public void onInteract(PlayerEntity user, Entity me, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
            super.onInteract(user, me, hand, cir);
            ItemStack handStack = null;
            if(user.getActiveHand() == hand && me instanceof DisplayEntity.ItemDisplayEntity disp  &&
                    !(handStack = user.getStackInHand(hand)).isEmpty()) {
//                System.out.println("player interacted with item " + handStack);

                PedestalInventoryStorage p = disp.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
                MultiblockInventory temp = new MultiblockInventory(1);
                temp.set(p.getEntry(), 0);
                int count = user.isSneaking() ? Math.min(handStack.getCount(),handStack.getMaxCount()) : 1;
                if(temp.addStack(0, handStack.copyWithCount(count))) {
//                    System.out.println("b");
                    handStack.decrementUnlessCreative(count, user);
                    cir.setReturnValue(ActionResult.SUCCESS);
                    p.setEntry(temp.get(0));
                    p.markDirty(true);
                    p.update();
                    findMatchingText(disp).getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).withItemDisplay(disp).update();
                } else {
                    cir.setReturnValue(ActionResult.FAIL);
                }
            } else if(user.getActiveHand() == hand && handStack != null && handStack.isEmpty()) {
                cir.setReturnValue(ActionResult.PASS);
            }
        }

        @Override
        public void handleAttack(Entity attacker, Entity me, CallbackInfoReturnable<Boolean> cir) {
            super.handleAttack(attacker, me, cir);
            ItemStack handStack;

            if(attacker instanceof PlayerEntity player && me instanceof DisplayEntity.ItemDisplayEntity disp) {
//                System.out.println("player interacted with item " + handStack);

                PedestalInventoryStorage p = disp.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
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
                    p.update();
                    findMatchingText(disp).getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).withItemDisplay(disp).update();
                } else {
                    cir.setReturnValue(false);
                }
            }
        }

        public DisplayEntity.TextDisplayEntity findMatchingText(DisplayEntity.ItemDisplayEntity me) {
            List<DisplayEntity.TextDisplayEntity> entities = me.getWorld().getEntitiesByType(EntityType.TEXT_DISPLAY, me.getBoundingBox().expand(1).expand(0,2,0), e -> {
                if(e.getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).getSlot() == slot) {
                    return true;
                }
                return false;
            });
            entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(me)));
            if(entities.isEmpty()) return null;
            return entities.getFirst();
        }


        @Override
        public void onDiscard(Entity me) {
            if(me instanceof DisplayEntity.ItemDisplayEntity d) {
                PedestalInventoryStorage p = d.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
                MultiblockInventory.Singleton e = p.getEntry();
                if(e.isEmpty()) return;
                MultiblockInventory temp = new MultiblockInventory(1);
                temp.set(e, 0);
                if(me.getWorld() instanceof ServerWorld s) {
                    for (ItemStack stack : temp.toStacks()) {
                        ItemEntity item = new ItemEntity(s, d.getX(), d.getY(), d.getZ(), stack);
                        item.setPickupDelay(40);
                        float f = s.random.nextFloat() * 0.5F;
                        float g = s.random.nextFloat() * (float) (Math.PI * 2);
                        item.setVelocity(-MathHelper.sin(g) * f, 0.2F, MathHelper.cos(g) * f);
                        s.spawnEntity(item);
                    }
                }
            }
        }
    }

    public static class PedestalEntityText extends AbstractMultiblockStructureEntity<DisplayEntity.TextDisplayEntity> {
        private final int slot;
        public PedestalEntityText(String id, int slot) {
            super(EntityType.TEXT_DISPLAY, id);
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        @Override
        public DisplayEntity.TextDisplayEntity create(ServerWorld world, MultiblockStructure structure, BlockPos center, Vec3d relative) {
            DisplayEntity.TextDisplayEntity ret = super.create(world, structure, center, relative);
            ret.getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).setStorageSlot(slot);
            ret.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
            ret.setDisplayWidth(100);
            ret.setBackground(0);
            ret.setTransformation(new AffineTransformation(new Vector3f(), new Quaternionf(), new Vector3f(0.5f,0.5f,0.5f), new Quaternionf()));
            return ret;
            //TODO: inventory stuff
        }
    }

    public static class PedestalEntityInteraction extends AbstractMultiblockStructureEntity<InteractionEntity> {
        private final int slot;
        public PedestalEntityInteraction(String id, int slot) {
            super(EntityType.INTERACTION, id);
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        public DisplayEntity.ItemDisplayEntity findMatchingDisplay(InteractionEntity me) {
            List<DisplayEntity.ItemDisplayEntity> entities = me.getWorld().getEntitiesByType(EntityType.ITEM_DISPLAY, me.getBoundingBox().expand(1), e -> {
                if(e.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).getSlot() == slot) {
                    return true;
                }
                return false;
            });
            entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(me)));
            if(entities.isEmpty()) return null;
            return entities.getFirst();
        }

        @Override
        public void onInteract(PlayerEntity user, Entity me, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
            DisplayEntity.ItemDisplayEntity d;
            if(me instanceof InteractionEntity e && (d = findMatchingDisplay(e)) != null) {
                user.interact(d, hand);
                cir.setReturnValue(ActionResult.PASS);
            }
        }

        @Override
        public void handleAttack(Entity attacker, Entity me, CallbackInfoReturnable<Boolean> cir) {
            DisplayEntity.ItemDisplayEntity d;
            if(me instanceof InteractionEntity e && (d = findMatchingDisplay(e)) != null) {
                cir.setReturnValue(d.handleAttack(attacker));
            }
        }

        @Override
        public InteractionEntity create(ServerWorld world, MultiblockStructure structure, BlockPos center, Vec3d relative) {
            InteractionEntity ret = super.create(world, structure, center, relative);
            ret.setInteractionHeight(1.5f);
            ret.setInteractionWidth(1);
            ret.setResponse(true);
            return ret;
        }
    }
}
