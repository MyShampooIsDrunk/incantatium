package myshampooisdrunk.incantatium.items;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractRecipeItem;
import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ThunderstormItem extends AbstractRecipeItem<CraftingRecipeInput> {
    public ThunderstormItem() {
        super(Items.FERMENTED_SPIDER_EYE, Incantatium.id("thunderstorm_item"), null , Incantatium.getModel(Incantatium.id("thunderstorm_item")));
        addComponent(DataComponentTypes.ITEM_NAME, Text.literal("Aspect of Thunder"));
        addComponent(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        addComponent(DataComponentTypes.MAX_STACK_SIZE, 16);
        addComponent(DataComponentTypes.RARITY, Rarity.RARE);
        addComponent(DataComponentTypes.USE_COOLDOWN, new UseCooldownComponent(5, Optional.of(Incantatium.id("thunderstorm_item"))));
    }

    @Override
    public void use(World world, LivingEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if(!world.isClient() && world instanceof ServerWorld sw &&
                user instanceof PlayerEntity player && sw.getDimension().hasSkyLight() && !world.getDimension().hasCeiling()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if(!player.getItemCooldownManager().isCoolingDown(itemStack)){
                sw.setWeather(0,
                        ServerWorld.THUNDER_WEATHER_DURATION_PROVIDER.get(world.getRandom()), true, true);
                cir.setReturnValue(ActionResult.CONSUME);
                itemStack.decrementUnlessCreative(1, user);
                player.getItemCooldownManager().set(itemStack, 100);

            } else cir.setReturnValue(ActionResult.FAIL);
//            cooldown.set("thunderstorm_item", 100);
//            CustomItemCooldownManager cooldown = ((CustomItemCooldownManagerI)user).getCustomItemCooldownManager();
//            if(!cooldown.isCoolingDown("thunderstorm_item")){
//
//            } else {
//                cir.setReturnValue(ActionResult.FAIL);
//            }
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
