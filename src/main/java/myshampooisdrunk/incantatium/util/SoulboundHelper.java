package myshampooisdrunk.incantatium.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

public class SoulboundHelper {
    public static void copySoulBoundItems(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (!alive && !(oldPlayer.getServerWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator())) {
            for (int i = 0; i < oldPlayer.getInventory().size(); i++) {
                ItemStack oldStack = oldPlayer.getInventory().getStack(i);
                ItemStack newStack = newPlayer.getInventory().getStack(i);
                if (isSoulbound(oldStack) && !ItemStack.areEqual(oldStack, newStack)) {
                    if (newStack.isEmpty()) {
                        newPlayer.getInventory().setStack(i, oldStack);
                    } else {
                        newPlayer.getInventory().offerOrDrop(oldStack);
                    }
                }
            }
        }
    }

    public static boolean isSoulbound(ItemStack stack) {
        if(stack.contains(DataComponentTypes.CUSTOM_DATA)){
            NbtCompound nbt = stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt();
            return nbt.contains("Soulbound");
        } else {
            return false;
        }
    }
}
