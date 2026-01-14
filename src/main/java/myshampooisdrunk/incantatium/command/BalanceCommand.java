package myshampooisdrunk.incantatium.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.BankAccount;
import myshampooisdrunk.incantatium.items.CoinItem;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.List;

public class BalanceCommand {
    private static final DynamicCommandExceptionType GENERIC_EXCEPTION = new DynamicCommandExceptionType(message -> (Text) message);
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("balance").requires(CommandManager.requirePermissionLevel(0))
                .executes(context -> executeBalanceCommand(context.getSource()))
                .then(CommandManager.literal("withdraw")
                        .then(CommandManager.argument("quantity", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                .executes(context -> executeWithdrawCommand(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "quantity")))))
                .then(CommandManager.literal("set").requires(CommandManager.requirePermissionLevel(4))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("quantity", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(context -> executeSetBalanceCommand(
                                                context.getSource(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "quantity")
                                        )))))
                .then(CommandManager.literal("get").requires(CommandManager.requirePermissionLevel(4))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> executeGetBalanceCommand(
                                        context.getSource(),
                                        EntityArgumentType.getPlayer(context, "player")
                                )))));
    }

    private static int executeBalanceCommand(ServerCommandSource source) {
        if(!source.isExecutedByPlayer() || source.getPlayer() == null) return 0;

        int balance = source.getPlayer().getComponent(Incantatium.PLAYER_BANK_ACCOUNT_COMPONENT_KEY).getCoins();
        source.getPlayer().sendMessage(Text.literal("Balance").withColor(Colors.GRAY).append(Text.literal("  > $" + balance).withColor(Colors.GREEN)));

        return 1;
    }

    private static int executeWithdrawCommand(ServerCommandSource source, int quantity) throws CommandSyntaxException {
        if(!source.isExecutedByPlayer() || source.getPlayer() == null) return 0;
        BankAccount account = source.getPlayer().getComponent(Incantatium.PLAYER_BANK_ACCOUNT_COMPONENT_KEY);
        int balance = account.getCoins();

        if(quantity > balance) {
            throw GENERIC_EXCEPTION.create(Text.translatable("incantatium.commands.balance.failed.funds"));
        }

        int netherite = 0;
        int gold = 0;
        int silver = 0;
        int copper = 0;
        int quantity2 = quantity;

        if(quantity >= 10000) {
            netherite = quantity / 10000;
            quantity -= netherite * 10000;
        }

        if(quantity >= 100) {
            gold = quantity/100;
            quantity -= gold * 100;
        }

        if(quantity >= 10) {
            silver = quantity/10;
            quantity -= silver * 10;
        }

        if(quantity >= 1) {
            copper = quantity;
        }

        List<ItemStack> stacks = new java.util.ArrayList<>(List.of());
        if(netherite != 0) stacks.add(account.withdraw(netherite, CoinItem.CoinType.NETHERITE));
        if(gold != 0) stacks.add(account.withdraw(gold, CoinItem.CoinType.GOLD));
        if(silver != 0) stacks.add(account.withdraw(silver, CoinItem.CoinType.SILVER));
        if(copper != 0) stacks.add(account.withdraw(copper, CoinItem.CoinType.COPPER));

        if(!stacks.isEmpty()) stacks.forEach(stack -> source.getPlayer().giveOrDropStack(stack));

        source.getPlayer().sendMessage(Text.stringifiedTranslatable("incantatium.bank.withdraw", quantity2).withColor(Colors.GREEN), false);
        return 1;
    }

    private static int executeSetBalanceCommand(ServerCommandSource source, ServerPlayerEntity target, int quantity) {
        target.getComponent(Incantatium.PLAYER_BANK_ACCOUNT_COMPONENT_KEY).setBalance(quantity);
        source.sendFeedback(() -> Text.literal("SET ").append(target.getDisplayName()).append(" BALANCE TO " + quantity).withColor(Colors.GREEN), false);
        return 1;
    }

    private static int executeGetBalanceCommand(ServerCommandSource source, ServerPlayerEntity target) {
        source.sendFeedback(() -> target.getDisplayName().copy().append("'S BALANCE IS $" +
                target.getComponent(Incantatium.PLAYER_BANK_ACCOUNT_COMPONENT_KEY).getCoins()).withColor(Colors.GREEN), false);
        return 1;
    }
}
