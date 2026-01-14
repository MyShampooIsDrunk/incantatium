package myshampooisdrunk.incantatium.items;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.item.Items;

public class ShrineDisplayItem extends AbstractCustomItem {
    public ShrineDisplayItem() {
        super(Items.FERMENTED_SPIDER_EYE, Incantatium.id("shrine_display"),null, Incantatium.getModel(Incantatium.id("shrine_model")));
    }
}
