package myshampooisdrunk.incantatium.items;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class BrokenArmorPotionItem extends AbstractCustomItem {

    private final PotionType type;
    private final PotionStrength strength;

    public BrokenArmorPotionItem(PotionType type, PotionStrength strength) {
        super(type.item, Incantatium.id("broken_armor_" + type));

        this.type = type;
        this.strength = strength;

        int color = rgbToInt(new int[]{158,145,112});
        int div = 1;
        if(type == PotionType.LINGER) div = 4;
        PotionContentsComponent potion = new PotionContentsComponent(Optional.empty(), Optional.of(color),
                List.of(new StatusEffectInstance(StatusEffects.UNLUCK, strength.duration, strength.potency/div)));
        addComponent(DataComponentTypes.POTION_CONTENTS, potion);
        String potionType = switch (type) {
            case DRINK -> "";
            case SPLASH -> "Splash ";
            case LINGER -> "Lingering ";
        };
        Text name = Text.literal(potionType + "Potion of Broken Armor");
        addComponent(DataComponentTypes.ITEM_NAME, name);
    }

    public PotionType getType(){
        return type;
    }
    public PotionStrength getStrength(){
        return strength;
    }

    public static int rgbToInt(int[] rgb){
        int ret = 0;
        for(int c :rgb) {
            ret = (ret << 8) + c;
        }
        return ret;
    }

    public enum PotionType {
        DRINK(Items.POTION),
        SPLASH(Items.SPLASH_POTION),
        LINGER(Items.LINGERING_POTION);

        public final Item item;
        PotionType(Item item){
            this.item = item;
        }

        @Override
        public String toString(){
            return switch (this){
                case DRINK-> "potion";
                case SPLASH -> "splash_potion";
                case LINGER -> "lingering_potion";
            };
        }
    }

    public enum PotionStrength {
        NONE(900, 0),
        LONG(1800, 0),
        STRONG(432, 1);

        private final int duration;
        private final int potency;
        PotionStrength(int duration, int potency){
            this.duration = duration;
            this.potency = potency;
        }
    }
}
