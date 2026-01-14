package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemCooldownManager.class)
public abstract class ItemCooldownManagerMixin {
    @WrapMethod(method = "isCoolingDown")
    private boolean isSalvationCD(ItemStack stack, Operation<Boolean> original) {
        NbtComponent nbt;
        if(stack != null && (nbt = stack.get(DataComponentTypes.CUSTOM_DATA)) != null) {
            int charges = nbt.copyNbt().getInt("charges").orElse(-1);
            if(charges >= 0 && charges < 2) return false;
        }

        return original.call(stack);
    }
}
