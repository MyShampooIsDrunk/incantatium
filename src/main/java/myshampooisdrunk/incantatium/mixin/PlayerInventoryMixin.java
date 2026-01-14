package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static myshampooisdrunk.incantatium.util.PostDeathHelper.isSoulbound;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @WrapOperation(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean wrapDropItem(ItemStack instance, Operation<Boolean> original) {
        if(isSoulbound(instance)) return true;
        return original.call(instance);
    }
}
