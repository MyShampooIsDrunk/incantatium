package myshampooisdrunk.incantatium;

import myshampooisdrunk.drunk_server_toolkit.datagen.CustomRecipeProvider;
import myshampooisdrunk.incantatium.datagen.enchantments.CustomEnchantmentProvider;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class IncantatiumDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		IncantatiumRegistry.init();
		pack.addProvider(CustomRecipeProvider::new);
//		pack.addProvider(CustomEnchantmentProvider::new);
	}
}
