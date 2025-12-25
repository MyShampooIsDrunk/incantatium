package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import myshampooisdrunk.drunk_server_toolkit.util.Util;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static myshampooisdrunk.incantatium.util.PostDeathHelper.isSoulbound;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

//    @Redirect(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
//    public <E> E setIfSoulbound(DefaultedList<E> instance, int i, E e){
//        ItemStack stack = (ItemStack) instance.get(i);
////        System.out.println("stack: " + stack);
//        if(isSoulbound(stack)) {
//            return Util.forceCast(stack);
//        }
//        instance.set(i,e);
//        return e;
//    }

    @WrapOperation(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean wrapDropItem(ItemStack instance, Operation<Boolean> original) {
        if(isSoulbound(instance)) return true;
        return original.call(instance);
    }

//    @Redirect(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"))
//    public ItemEntity dontDropIfSoulbound(PlayerEntity instance, ItemStack stack, boolean throwRandomly, boolean retainOwnership){
//        if(!isSoulbound(stack)) {
//            return instance.dropItem(stack, throwRandomly, retainOwnership);
//        }
//        return null;
//    }
}
