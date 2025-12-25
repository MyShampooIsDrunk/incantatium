package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MaceItem.class)
public class MaceItemMixin {
    @WrapMethod(method = "shouldDealAdditionalDamage")
    private static boolean dontDamageIfCooldown(LivingEntity attacker, Operation<Boolean> original) {
        if(attacker instanceof PlayerEntity p && p.getItemCooldownManager().isCoolingDown(Items.MACE.getDefaultStack())) return false;
        return original.call(attacker);
    }
}
