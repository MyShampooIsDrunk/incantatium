package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.item.CustomItemHelper;
import myshampooisdrunk.incantatium.items.ornaments.SalvationOrnamentItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

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
