package myshampooisdrunk.incantatium.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

import static myshampooisdrunk.incantatium.util.SoulboundHelper.isSoulbound;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Redirect(method = "dropAll", at = @At(value = "INVOKE", target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    public <E> E setIfSoulbound(List<E> instance, int i, E e){
        ItemStack stack = (ItemStack) instance.get(i);
//        System.out.println("stack: " + stack);
        if(isSoulbound(stack)) {
            return (E)stack;
        }
        instance.set(i,e);
        return e;
    }
    @Redirect(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"))
    public ItemEntity dontDropIfSoulbound(PlayerEntity instance, ItemStack stack, boolean throwRandomly, boolean retainOwnership){
        if(!isSoulbound(stack)) {
            return instance.dropItem(stack, throwRandomly, retainOwnership);
        }
        return null;
    }
}
