package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Items.class)
public class ItemsMixin {
    @ModifyArg(method = "<clinit>", at=@At(value="INVOKE",target = "Lnet/minecraft/item/Item$Settings;food(Lnet/minecraft/component/type/FoodComponent;)Lnet/minecraft/item/Item$Settings;",ordinal = 6))
    private static FoodComponent getRealGodApple(FoodComponent foodComponent){
        return Incantatium.MOD_GOD_APPLE;
    }
}
