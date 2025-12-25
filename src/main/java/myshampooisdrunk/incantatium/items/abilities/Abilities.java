package myshampooisdrunk.incantatium.items.abilities;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.item.CustomItemHelper;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.ornaments.SalvationOrnamentItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;

public class Abilities {

    public static void updateAbility(ServerPlayerEntity player, Type type) {
        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(type.accept(player)));
    }

    public enum Type {
        RIPTIDE(player -> {
            int charges = player.getComponent(Incantatium.RIPTIDE_COOLDOWN_COMPONENT_KEY).get();
            StringBuilder txt = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                if(i <= charges) {
                    txt.append('\uEa01');
                } else txt.append('\uEa02');
                txt.append(' ');
            }
            return Text.literal(txt.toString());
        }),
        SALVATION(player -> {
            Optional<AbstractCustomItem> item;
            ItemStack stack;
            NbtComponent nbt;
            if((item = CustomItemHelper.getCustomItem(stack = player.getOffHandStack())).isPresent() && item.get() instanceof SalvationOrnamentItem
                && stack.contains(DataComponentTypes.CUSTOM_DATA) && (nbt = stack.get(DataComponentTypes.CUSTOM_DATA)) != null) {
                int charges = nbt.copyNbt().getInt("charges").orElse(-1);
                if(charges > 0) {
                    StringBuilder txt = new StringBuilder();
                    for (int i = 0; i < 3; i++) {
                        if(i <= charges) {
                            txt.append('\uEa11');
                        } else txt.append('\uEa12');
                        txt.append(' ');
                    }
                    return Text.literal(txt.toString());
                }
            }
            return Text.empty();
        });

        private final TextFactory factory;

        Type(TextFactory factory) {
            this.factory = factory;
        }

        public Text accept(ServerPlayerEntity player) {
            return this.factory.accept(player);
        }
    }

    @FunctionalInterface
    interface TextFactory {
        Text accept(ServerPlayerEntity player);
    }
}