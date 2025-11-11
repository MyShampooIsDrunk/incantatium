//package myshampooisdrunk.incantatium.multiblock.entity;
//
//import myshampooisdrunk.drunk_server_toolkit.DST;
//import myshampooisdrunk.drunk_server_toolkit.component.DSTComponentRegistry;
//import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockEntity;
//import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockEntityType;
//import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
//import myshampooisdrunk.incantatium.Incantatium;
//import myshampooisdrunk.incantatium.component.PedestalInventoryStorage;
//import myshampooisdrunk.incantatium.multiblock.IncantatiumMultiblockRegistry;
//import myshampooisdrunk.incantatium.multiblock.ShrineMultiblock;
//import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.ItemEntity;
//import net.minecraft.entity.SpawnReason;
//import net.minecraft.entity.decoration.DisplayEntity;
//import net.minecraft.entity.decoration.InteractionEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemDisplayContext;
//import net.minecraft.item.ItemStack;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.Hand;
//import net.minecraft.util.math.AffineTransformation;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.World;
//import org.joml.Quaternionf;
//import org.joml.Vector3f;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.Comparator;
//import java.util.List;
//
//public class PedestalEntityGenerator {
////    public static Map<Vec3d, > attach(double radius, ShrineMultiblock multiblock) {
////        Vec3d[] offsets = new Vec3d[8];
////        IncantatiumMultiblockRegistry.Pedestal[] ps = IncantatiumMultiblockRegistry.registerEntities();
////        for (double i = 0; i < 2; i+=0.25) {
////            Vec3d offset = new Vec3d(Math.cos(Math.PI * i),-0.5,Math.sin(Math.PI*i)).multiply(radius);
////            int slot = (int)(4 * i);
//////            IncantatiumMultiblockRegistry.Pedestal p = IncantatiumMultiblockRegistry.PEDESTAL_ENTITIES[slot];
////            IncantatiumMultiblockRegistry.Pedestal p = ps[slot];
////            multiblock.attachPedestalEntities(offset,p.storage(),p.text(),p.interaction(),slot);
//////            offsets[(int)(i*4)] = new Vec3d(Math.cos(Math.PI * i),0,Math.sin(Math.PI*i)).multiply(radius);
////        }
////
//////        multiblock.attachPedestalEntity();
////    }
//
//    public static class PedestalEntity extends MultiblockEntity<DisplayEntity.ItemDisplayEntity, PedestalEntity> {
//        private final int slot;
//
//        public PedestalEntity(MultiblockEntityType<DisplayEntity.ItemDisplayEntity, PedestalEntity> type, World world, DisplayEntity.ItemDisplayEntity entity) {
//            super(type, world, entity);
//            this.slot = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).getSlot();
//        }
//
//        public PedestalEntity(MultiblockEntityType<DisplayEntity.ItemDisplayEntity, PedestalEntity> type, World world, int slot) {
//            super(type, world);
//            this.slot = slot;
//        }
//
//        public int getSlot() {
//            return slot;
//        }
//
//        @Override
//        public DisplayEntity.ItemDisplayEntity create(MultiblockStructure structure, BlockPos center, Vec3d relative, SpawnReason reason) {
//            DisplayEntity.ItemDisplayEntity ret = super.create(structure, center, relative, reason);
//            Vec3d spawnPos = relative.add(new Vec3d(center));
//            ret.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).setStorageSlot(slot);
//            ret.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
//            ret.setItemDisplayContext(ItemDisplayContext.GUI);
//            ret.setTransformation(new AffineTransformation(new Vector3f(), new Quaternionf(0,1,0,0), new Vector3f(0.5f,0.5f,0.5f), new Quaternionf()));
//            ret.refreshPositionAndAngles(spawnPos,0 ,-90);
//            return ret;
//            //TODO: inventory stuff
//        }
//
//        @Override
//        public void onInteract(PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
//            super.onInteract(user, hand, cir);
//            ItemStack handStack = null;
//            if(user.getActiveHand() == hand && !(handStack = user.getStackInHand(hand)).isEmpty()) {
////                System.out.println("player interacted with item " + handStack);
//
//                PedestalInventoryStorage p = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
//                MultiblockInventory temp = new MultiblockInventory(1);
//                temp.set(p.getEntry(), 0);
//                int count = user.isSneaking() ? Math.min(handStack.getCount(),handStack.getMaxCount()) : 1;
//                if(temp.addStack(0, handStack.copyWithCount(count))) {
////                    System.out.println("b");
//                    handStack.decrementUnlessCreative(count, user);
//                    cir.setReturnValue(ActionResult.SUCCESS);
//                    p.setEntry(temp.get(0));
//                    p.markDirty(true);
//                    p.update();
//                    findMatchingText().getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).withItemDisplay(entity).update();
//                } else {
//                    cir.setReturnValue(ActionResult.FAIL);
//                }
//            } else if(user.getActiveHand() == hand && handStack != null && handStack.isEmpty()) {
//                cir.setReturnValue(ActionResult.PASS);
//            }
//        }
//
//        @Override
//        public void handleAttack(Entity attacker, CallbackInfoReturnable<Boolean> cir) {
//            super.handleAttack(attacker, cir);
//            ItemStack handStack;
//
//            if(attacker instanceof PlayerEntity player) {
////                System.out.println("player interacted with item " + handStack);
//
//                PedestalInventoryStorage p = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
//                MultiblockInventory.Singleton e = p.getEntry();
//                if(e.isEmpty()) return;
//                MultiblockInventory temp = new MultiblockInventory(1);
//                temp.set(e, 0);
//                int count = player.isSneaking() ? e.stack().getMaxCount() : 1;
//                ItemStack ret;
//                if((ret = temp.removeStack(0, count)) != null) {
//                    cir.setReturnValue(true);
//                    player.giveItemStack(ret);
//                    p.setEntry(temp.get(0));
//                    p.markDirty(true);
//                    p.update();
//                    findMatchingText().getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).withItemDisplay(entity).update();
//                } else {
//                    cir.setReturnValue(false);
//                }
//            }
//        }
//
//        public DisplayEntity.TextDisplayEntity findMatchingText() {
//            List<DisplayEntity.TextDisplayEntity> entities = entity.getEntityWorld().getEntitiesByType(EntityType.TEXT_DISPLAY, entity.getBoundingBox().expand(1).expand(0,2,0), e -> {
//                if(e.getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).getSlot() == slot) {
//                    return true;
//                }
//                return false;
//            });
//            entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(entity)));
//            if(entities.isEmpty()) return null;
//            return entities.getFirst();
//        }
//
//
//        @Override
//        public void onRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
//            super.onRemoved(reason, ci);
//            if(entity instanceof DisplayEntity.ItemDisplayEntity d) {
//                PedestalInventoryStorage p = d.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY);
//                MultiblockInventory.Singleton e = p.getEntry();
//                if(e.isEmpty()) return;
//                MultiblockInventory temp = new MultiblockInventory(1);
//                temp.set(e, 0);
//                if(entity.getEntityWorld() instanceof ServerWorld s) {
//                    for (ItemStack stack : temp.toStacks()) {
//                        ItemEntity item = new ItemEntity(s, d.getX(), d.getY(), d.getZ(), stack);
//                        item.setPickupDelay(40);
//                        float f = s.random.nextFloat() * 0.5F;
//                        float g = s.random.nextFloat() * (float) (Math.PI * 2);
//                        item.setVelocity(-MathHelper.sin(g) * f, 0.2F, MathHelper.cos(g) * f);
//                        s.spawnEntity(item);
//                    }
//                }
//            }
//        }
//    }
//
//    public static class PedestalEntityText extends MultiblockEntity<DisplayEntity.TextDisplayEntity, PedestalEntityText> {
//        private final int slot;
//
//        public PedestalEntityText(MultiblockEntityType<DisplayEntity.TextDisplayEntity, PedestalEntityText> type, World world, DisplayEntity.TextDisplayEntity entity) {
//            super(type, world, entity);
//            this.slot = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).getSlot();
//        }
//
//        public PedestalEntityText(MultiblockEntityType<DisplayEntity.TextDisplayEntity, PedestalEntityText> type, World world, int slot) {
//            super(type, world);
//            this.slot = slot;
//        }
//
//        public int getSlot() {
//            return slot;
//        }
//
//        @Override
//        public DisplayEntity.TextDisplayEntity create(MultiblockStructure structure, BlockPos center, Vec3d relative, SpawnReason reason) {
//            DisplayEntity.TextDisplayEntity ret = super.create(structure, center, relative, reason);
//            ret.getComponent(Incantatium.PEDESTAL_TEXT_COMPONENT_KEY).setStorageSlot(slot);
//            ret.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
//            ret.setDisplayWidth(100);
//            ret.setBackground(0);
//            ret.setTransformation(new AffineTransformation(new Vector3f(), new Quaternionf(), new Vector3f(0.5f,0.5f,0.5f), new Quaternionf()));
//            return ret;
//            //TODO: inventory stuff
//        }
//    }
//
//    public static class PedestalEntityInteraction extends MultiblockEntity<InteractionEntity, PedestalEntityInteraction> {
//        private final int slot;
//        public PedestalEntityInteraction(MultiblockEntityType<InteractionEntity, PedestalEntityInteraction> type, World world, InteractionEntity entity) {
//            super(type, world, entity);
//            this.slot = entity.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).getSlot();
//        }
//
//        public PedestalEntityInteraction(MultiblockEntityType<InteractionEntity, PedestalEntityInteraction> type, World world, int slot) {
//            super(type, world);
//            this.slot = slot;
//        }
//
//        public int getSlot() {
//            return slot;
//        }
//
//        public DisplayEntity.ItemDisplayEntity findMatchingDisplay() {
//            List<DisplayEntity.ItemDisplayEntity> entities = entity.getEntityWorld().getEntitiesByType(EntityType.ITEM_DISPLAY, entity.getBoundingBox().expand(1), e -> {
//                if(e.getComponent(Incantatium.PEDESTAL_STORAGE_COMPONENT_KEY).getSlot() == slot) {
//                    return true;
//                }
//                return false;
//            });
//            entities.sort(Comparator.comparingDouble(t -> t.squaredDistanceTo(entity)));
//            if(entities.isEmpty()) return null;
//            return entities.getFirst();
//        }
//
//        @Override
//        public void onInteract(PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
//            DisplayEntity.ItemDisplayEntity d;
//            if((d = findMatchingDisplay()) != null) {
//                user.interact(d, hand);
//                cir.setReturnValue(ActionResult.PASS);
//            }
//        }
//
//        @Override
//        public void handleAttack(Entity attacker, CallbackInfoReturnable<Boolean> cir) {
//            DisplayEntity.ItemDisplayEntity d;
//            if((d = findMatchingDisplay()) != null) {
//                cir.setReturnValue(d.handleAttack(attacker));
//            }
//        }
//
//        @Override
//        public InteractionEntity create(MultiblockStructure structure, BlockPos center, Vec3d relative, SpawnReason reason) {
//            InteractionEntity ret = super.create(structure, center, relative, reason);
//            ret.setInteractionHeight(1.5f);
//            ret.setInteractionWidth(1);
//            ret.setResponse(true);
//            return ret;
//        }
//    }
//}
