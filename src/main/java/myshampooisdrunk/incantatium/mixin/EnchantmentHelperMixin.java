//package myshampooisdrunk.incantatium.mixin;
//
//import com.llamalad7.mixinextras.injector.ModifyReturnValue;
//import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
//import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//import myshampooisdrunk.incantatium.Incantatium;
//import net.minecraft.enchantment.EnchantmentHelper;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.damage.DamageSource;
//import net.minecraft.item.ItemStack;
//import net.minecraft.server.world.ServerWorld;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//
//@Mixin(EnchantmentHelper.class)
//public abstract class EnchantmentHelperMixin {
//    @ModifyReturnValue(method = "getArmorEffectiveness", at = @At("RETURN"))
//    private static float printArmorEffectiveness(float original) {
//        Incantatium.LOGGER.info("armor effectiveness: {}", original);
//        return original;
//    }
//
//    @ModifyReturnValue(method = "getSmashDamagePerFallenBlock", at = @At("RETURN"))
//    private static float printDensityEffectiveness(float original) {
//        Incantatium.LOGGER.info("density damage per block: {}", original);
//        return original;
//    }
//
//}
