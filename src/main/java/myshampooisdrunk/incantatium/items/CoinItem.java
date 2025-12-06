package myshampooisdrunk.incantatium.items;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class CoinItem extends AbstractCustomItem {
    private final int value;
    public CoinItem(Identifier identifier, String itemName, int value) {
        super(Items.FERMENTED_SPIDER_EYE, identifier, itemName, Incantatium.getModel(identifier));
        this.value = value;

        addComponent(DataComponentTypes.MAX_STACK_SIZE, 99);
        addComponent(DataComponentTypes.CONSUMABLE, new ConsumableComponent(1f, UseAction.BOW, SoundEvents.BLOCK_NOTE_BLOCK_HAT, false, List.of()));
    }

    @Override
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if(user instanceof PlayerEntity player && !world.isClient()) {
            int deposit;
            if(player.isSneaking()) {
                int count = stack.getCount();
                deposit = count * this.value;
                stack.decrement(count);
                player.getComponent(Incantatium.PLAYER_BANK_ACCOUNT_COMPONENT_KEY).addBalance(deposit);
            } else {
                player.getComponent(Incantatium.PLAYER_BANK_ACCOUNT_COMPONENT_KEY).addBalance(deposit = this.value);
            }
            player.sendMessage(Text.stringifiedTranslatable("incantatium.bank.deposit", deposit).withColor(Colors.GREEN), false);
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 0.75f, 1.25f);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity entity) {
        if(!entity.getEntityWorld().isClient()) return 20;
        return super.getMaxUseTime(stack, entity);
    }

    public static class CopperCoinItem extends CoinItem {
        public CopperCoinItem() {
            super(Incantatium.id("copper_coin"), "incantatium.copper_coin.name", 1);
        }
    }

    public static class SilverCoinItem extends CoinItem {
        public SilverCoinItem() {
            super(Incantatium.id("silver_coin"), "incantatium.silver_coin.name", 10);
        }
    }

    public static class GoldCoinItem extends CoinItem {
        public GoldCoinItem() {
            super(Incantatium.id("gold_coin"), "incantatium.gold_coin.name", 100);
        }
    }

    public static class NetheriteCoinItem extends CoinItem {
        public NetheriteCoinItem() {
            super(Incantatium.id("netherite_coin"), "incantatium.netherite_coin.name", 10000);
        }
    }

    public enum CoinType {
        COPPER(1, IncantatiumRegistry.COPPER_COIN),
        SILVER(10, IncantatiumRegistry.SILVER_COIN),
        GOLD(100, IncantatiumRegistry.GOLD_COIN),
        NETHERITE(10000, IncantatiumRegistry.NETHERITE_COIN);

        private final int value;
        private final CoinItem coin;

        CoinType(int value, CoinItem coin) {
            this.value = value;
            this.coin = coin;
        }

        public CoinItem item() {
            return coin;
        }

        public int value() {
            return value;
        }
    }
}
