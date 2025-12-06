package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.Settings.class)
public class Item$SettingsMixin {
    @WrapMethod(method = "horseArmor")
    private Item.Settings wrapHorseArmor(ArmorMaterial material, Operation<Item.Settings> original) {
        return original.call(material).enchantable(material.enchantmentValue());
    }
}
