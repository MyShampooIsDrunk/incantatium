package myshampooisdrunk.incanatium;

import myshampooisdrunk.drunk_server_toolkit.WeaponAPIDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class IncantatiumDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		WeaponAPIDataGenerator.initializeDataGen(fabricDataGenerator);
	}
}
