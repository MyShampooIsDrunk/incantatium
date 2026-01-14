package myshampooisdrunk.incantatium.mixin;

import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.BiFunction;

import static myshampooisdrunk.incantatium.util.PostDeathHelper.isSoulbound;

@Mixin(EntityEquipment.class)
public class EntityEquipmentMixin {
    @ModifyArg(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"))
    private ItemStack dontDropSoulbound(ItemStack stack) {
        if(isSoulbound(stack)) return ItemStack.EMPTY;
        return stack;
    }

    @ModifyArg(method = "clear", at = @At(value = "INVOKE", target = "Ljava/util/EnumMap;replaceAll(Ljava/util/function/BiFunction;)V"))
    private BiFunction<EquipmentSlot, ItemStack, ItemStack> dontClearSoulboundItems(BiFunction<EquipmentSlot, ItemStack, ItemStack> par1) {
        return (slot, stack) -> isSoulbound(stack) ? stack : ItemStack.EMPTY;
    }
}
