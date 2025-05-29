package myshampooisdrunk.incantatium.items;

import com.mojang.datafixers.util.Either;
import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class AbstractCustomArmorItem extends AbstractCustomItem {

    private final EquippableComponent component;

    public AbstractCustomArmorItem(Item item, Identifier identifier, EquipmentType type) {
        super(item, identifier);
        this.component=EquippableComponent.builder(type.getEquipmentSlot()).build();
        addComponent(DataComponentTypes.EQUIPPABLE, component);

    }

    public AbstractCustomArmorItem(Item item, Identifier identifier, @Nullable String itemName, EquipmentType type) {
        super(item, identifier, itemName);
        this.component=EquippableComponent.builder(type.getEquipmentSlot()).build();
        addComponent(DataComponentTypes.EQUIPPABLE, component);
    }

    public AbstractCustomArmorItem(Item item, Identifier identifier, @Nullable String itemName, @Nullable Either<CustomModelDataComponent, Identifier> customModelData, EquippableComponent component) {
        super(item, identifier, itemName, customModelData);
        this.component = component;
        addComponent(DataComponentTypes.EQUIPPABLE, component);
    }

    public EquippableComponent getEquipmentComponent() {
        return component;
    }
}
