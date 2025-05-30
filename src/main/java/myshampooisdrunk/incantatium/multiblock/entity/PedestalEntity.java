package myshampooisdrunk.incantatium.multiblock.entity;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class PedestalEntity extends AbstractMultiblockStructureEntity<ArmorStandEntity> {
    public PedestalEntity(String id) {
        super(EntityType.ARMOR_STAND, id);
    }

    @Override
    public void onInteract(PlayerEntity user, Entity me, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.onInteract(user, me, hand, cir);
        if(hand == user.getActiveHand() && !user.getWorld().isClient()) {
            ItemStack stack = user.getStackInHand(hand);

        }
    }

    public static class PedestalEntityText extends AbstractMultiblockStructureEntity<DisplayEntity.TextDisplayEntity> {
        public PedestalEntityText(String id) {
            super(EntityType.TEXT_DISPLAY, id);
        }
    }
}
