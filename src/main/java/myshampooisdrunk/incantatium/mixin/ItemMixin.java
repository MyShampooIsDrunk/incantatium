package myshampooisdrunk.incantatium.mixin;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.abilities.Abilities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void injectTridentInventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        if(entity instanceof ServerPlayerEntity player && stack.isOf(Items.TRIDENT) && player.isSneaking() &&
                EnchantmentHelper.getTridentSpinAttackStrength(stack, player) > 0) {
            Abilities.updateAbility(player, Abilities.Type.RIPTIDE);
        }
    }
}
