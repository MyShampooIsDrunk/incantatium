package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.component.type.BundleContentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleContentsComponent.class)
public abstract class BundleContentsComponentMixin {

    @ModifyExpressionValue(method = "getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I"))
    private static int wrapModifiedOccupancy(int original) {
        if(original == 1) return 8;
        if(original == 16) return 32;
        return original;
    }
}
