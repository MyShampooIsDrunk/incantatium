package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContentsComponent.class)
public abstract class BundleContentsComponentMixin {

    @ModifyExpressionValue(method = "getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I"))
    private static int wrapModifiedOccupancy(int original) {
        if(original == 1) return 8;
        if(original == 16) return 32;
        return original;
    }

//    @Inject(method = "getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;", at = @At("RETURN"), cancellable = true)
//    private static void injectModifiedOccupancy(ItemStack stack, CallbackInfoReturnable<Fraction> cir) {
//        if(stack.getMaxCount() == 1) {
//            cir.setReturnValue(Fraction.getFraction(1,8));
//        } else if(stack.getMaxCount() == 16) {
//            cir.setReturnValue(Fraction.getFraction(1,32));
//        }
//    }
}
