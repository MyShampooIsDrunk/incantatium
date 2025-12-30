package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(UseCooldownComponent.class)
public abstract class UseCooldownComponentMixin {
    @WrapOperation(method = "set", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/ItemStack;I)V"))
    private void setIfNonZero(ItemCooldownManager instance, ItemStack stack, int duration, Operation<Void> original) {
        if(duration > 0) original.call(instance, stack, duration);
    }
}
