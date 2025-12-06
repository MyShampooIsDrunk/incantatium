package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.items.CoinItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class PlayerBankAccount implements BankAccount{
    private int coins;

    public PlayerBankAccount(PlayerEntity p) {
        coins = 0;
    }

    @Override
    public int getCoins() {
        return this.coins;
    }

    @Override
    public void addBalance(int coins) {
        this.coins += coins;
    }

    @Override
    public ItemStack withdraw(int quantity, CoinItem.CoinType type) {
        if(quantity * type.value() > coins) return ItemStack.EMPTY;
        coins -= quantity * type.value();

        return type.item().create().copyWithCount(quantity);
    }

    @Override
    public void readData(ReadView readView) {
        coins = readView.getInt("coins", 500);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putInt("coins", coins);
    }
}
