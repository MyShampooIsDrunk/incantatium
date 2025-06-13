package myshampooisdrunk.incantatium.multiblock.entity;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.PedestalInventoryStorage;
import myshampooisdrunk.incantatium.multiblock.RitualMultiblock;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

public class PedestalEntityGenerator {
    public static void attach(double radius, RitualMultiblock multiblock) {
        Vec3d[] offsets = new Vec3d[8];
        for (double i = 0; i < 2; i+=0.25) {
//            offsets[(int)(i*4)] = Math.cos(Math.PI * i);
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
            ret.setBoundingBox(Box.of(spawnPos,1,2.5,1).offset(0,-0.75,0));
            return ret;
            //TODO: inventory stuff
        }

        @Override
        public void onInteract(PlayerEntity user, Entity me, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
            super.onInteract(user, me, hand, cir);
            ItemStack handStack = null;
            if(user.getActiveHand() == hand && me instanceof DisplayEntity.ItemDisplayEntity disp  &&
            !(handStack = user.getStackInHand(hand)).isEmpty()) {

                PedestalInventoryStorage p = disp.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
                MultiblockInventory temp = new MultiblockInventory(1);
                temp.set(p.getEntry(), 0);
                int count = user.isSneaking() ? 1 : handStack.getMaxCount();
                if(temp.addStack(slot, handStack.copyWithCount(count))) {
                    handStack.decrementUnlessCreative(count, user);
                    cir.setReturnValue(ActionResult.SUCCESS);
                    p.setEntry(temp.get(0));
                    p.update();
                    findMatchingText(disp).getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).update();
                } else {
                    cir.setReturnValue(ActionResult.FAIL);
                }
            } else if(user.getActiveHand() == hand && handStack != null) {
                cir.setReturnValue(ActionResult.PASS);
            }
        }

        public DisplayEntity.TextDisplayEntity findMatchingText(DisplayEntity.ItemDisplayEntity me) {
            List<DisplayEntity.TextDisplayEntity> entities = me.getWorld().getEntitiesByType(EntityType.TEXT_DISPLAY, me.getBoundingBox().expand(0.5), e -> {
                if(e.getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).getStorageSlot() == slot) {
                    return true;
                }
                return false;
            });
            entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(me)));
            if(entities.isEmpty()) return null;
            return entities.getFirst();
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
            return ret;
            //TODO: inventory stuff
        }
    }
}
