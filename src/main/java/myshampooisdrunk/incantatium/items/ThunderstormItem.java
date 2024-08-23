package myshampooisdrunk.incantatium.items;

import myshampooisdrunk.drunk_server_toolkit.cooldown.CustomItemCooldownManager;
import myshampooisdrunk.drunk_server_toolkit.cooldown.CustomItemCooldownManagerI;
import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.item.CustomRecipeItem;
import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

public class ThunderstormItem extends CustomRecipeItem<CraftingRecipeInput> {
    public ThunderstormItem() {
        super(Items.CLOCK, Incantatium.id("thunderstorm_item"), null ,true);
        addComponent(DataComponentTypes.ITEM_NAME, Text.literal("Aspect of Thunder"));
        addComponent(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        addComponent(DataComponentTypes.MAX_STACK_SIZE, 16);
        addComponent(DataComponentTypes.RARITY, Rarity.RARE);
    }

    @Override
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable cir) {
        if(!world.isClient()) {
            CustomItemCooldownManager cooldown = ((CustomItemCooldownManagerI)user).getCustomItemCooldownManager();
            if(!cooldown.isCoolingDown("thunderstorm_item")){
                world.getServer().getOverworld().setWeather(0,
                        ServerWorld.THUNDER_WEATHER_DURATION_PROVIDER.get(world.getRandom()), true, true);
                ItemStack itemStack = user.getStackInHand(hand);
                cir.setReturnValue(TypedActionResult.consume(itemStack));
                itemStack.decrementUnlessCreative(1, user);
                user.getItemCooldownManager().set(this.item, 100);
                cooldown.set("thunderstorm_item", 100);
            } else {
                cir.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
            }
        }

    }

    public CraftingRecipe recipe(){
        return new ShapedRecipe("", CraftingRecipeCategory.MISC, RawShapedRecipe.create(
                Map.of('B', Ingredient.ofItems(Items.BREEZE_ROD),
                        'N',Ingredient.ofItems(Items.NAUTILUS_SHELL),
                        'E',Ingredient.ofItems(Items.ECHO_SHARD),
                        'C',Ingredient.ofItems(Items.END_CRYSTAL),
                        'T',Ingredient.ofItems(Items.TOTEM_OF_UNDYING)),
                List.of("BNE","CTC","ENB")),
                create()
        );
    }
}
