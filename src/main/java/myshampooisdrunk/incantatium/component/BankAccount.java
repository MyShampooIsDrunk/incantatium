package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.incantatium.items.CoinItem;
import net.minecraft.item.ItemStack;
import org.ladysnake.cca.api.v3.component.Component;

public interface BankAccount extends Component {
    int getCoins();

    void addBalance(int coins);

    void setBalance(int coins);

    ItemStack withdraw(int quantity, CoinItem.CoinType type);
}
