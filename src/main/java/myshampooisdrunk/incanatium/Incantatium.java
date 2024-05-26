package myshampooisdrunk.incanatium;

import myshampooisdrunk.drunk_server_toolkit.WeaponAPI;
import myshampooisdrunk.drunk_server_toolkit.register.CustomItemRegistry;
import myshampooisdrunk.incanatium.items.TimeStopItem;
import net.fabricmc.api.ModInitializer;

import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Incantatium implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("incantatium");

	@Override
	public void onInitialize() {
		TimeStopItem timeStopItem = new TimeStopItem();
		CustomItemRegistry.registerItem(timeStopItem);
		CustomItemRegistry.addToGroup(timeStopItem, ItemGroups.REDSTONE);
		CustomItemRegistry.registerRecipe(
				new ShapelessRecipe(
						"", CraftingRecipeCategory.MISC,
						timeStopItem.create(),
						DefaultedList.copyOf(Ingredient.ofItems(Items.BEDROCK))
				),
				timeStopItem.getIdentifier(),
				timeStopItem
		);
		WeaponAPI.initializeRecipes();
	}
}