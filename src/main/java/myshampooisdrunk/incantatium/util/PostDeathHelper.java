package myshampooisdrunk.incantatium.util;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.item.CustomItemHelper;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.OrnamentAbilities;
import myshampooisdrunk.incantatium.items.ornaments.AbstractOrnamentItem;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.util.Optional;

import static net.minecraft.entity.player.PlayerInventory.EQUIPMENT_SLOTS;

public class PostDeathHelper {
    public static void copySoulBoundItems(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (!alive && !(oldPlayer.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator())) {

            for (int i = 0; i < oldPlayer.getInventory().size(); i++) {
                ItemStack oldStack = oldPlayer.getInventory().getStack(i);
                Optional<AbstractCustomItem> custom = CustomItemHelper.getCustomItem(oldStack);

                boolean soulboundOverride = oldStack.getDamage() < oldStack.getMaxDamage() && Boolean.TRUE.equals(oldStack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))
                        && custom.isPresent() && custom.get() instanceof AbstractOrnamentItem;

                //                Incantatium.LOGGER.info("SLOT {} | OLD: {}", i, oldStack);
                ItemStack newStack = newPlayer.getInventory().getStack(i);

                if ((soulboundOverride || isSoulbound(oldStack, true)) && !ItemStack.areEqual(oldStack, newStack)) {
                    if (newStack.isEmpty()) {
                        newPlayer.getInventory().setStack(i, oldStack);
                    } else {
                        newPlayer.getInventory().offerOrDrop(oldStack);
                    }
                }
            }
        }
    }

    public static void damageOrnament(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        for (ItemStack stack : newPlayer.getInventory()) {
            OrnamentAbilities abilities = oldPlayer.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            if(stack != null && !stack.isEmpty() && stack.getDamage() < stack.getMaxDamage())
                CustomItemHelper.getCustomItem(stack).ifPresent(custom -> {
                    if(custom instanceof AbstractOrnamentItem && abilities.isActive(custom.getIdentifier())) stack.setDamage(stack.getDamage() + 1);
                });
        }
    }

    public static void copyBankAccount(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if(!alive)
            newPlayer.getComponent(Incantatium.PLAYER_BANK_ACCOUNT_COMPONENT_KEY).setBalance(oldPlayer.getComponent(Incantatium.PLAYER_BANK_ACCOUNT_COMPONENT_KEY).getCoins());
    }

    public static boolean isSoulbound(ItemStack stack) {
        return isSoulbound(stack, false);
    }

    public static boolean isSoulbound(ItemStack stack, boolean checked) {
        if(stack.contains(DataComponentTypes.CUSTOM_DATA)) {
            NbtCompound nbt = stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt();
            if(nbt.contains("soulbound")) return true;

            if(!checked && Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)) && stack.getDamage() < stack.getMaxDamage()) {
                Optional<AbstractCustomItem> custom = CustomItemHelper.getCustomItem(stack);
                return custom.isPresent() && custom.get() instanceof AbstractOrnamentItem;
            }

        }
        return false;
    }
}
