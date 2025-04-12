package myshampooisdrunk.incantatium.items;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class AbstractCustomArmorItem extends AbstractCustomItem implements Equipment {
    private final ArmorItem.Type type;

    public AbstractCustomArmorItem(Item item, Identifier identifier, ArmorItem.Type type) {
        super(item, identifier);
        this.type=type;
    }

    public AbstractCustomArmorItem(Item item, Identifier identifier, @Nullable String itemName, ArmorItem.Type type) {
        super(item, identifier, itemName);
        this.type=type;
    }

    public AbstractCustomArmorItem(Item item, Identifier identifier, @Nullable String itemName, boolean customModel, ArmorItem.Type type) {
        super(item, identifier, itemName, customModel);
        this.type=type;
    }

    @Override
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable cir) {
        if(world instanceof ServerWorld sWorld) sWorld.playSound(user,user.getBlockPos().up(), getEquipSound().value(), SoundCategory.PLAYERS,1f,1f);
        cir.setReturnValue(this.equipAndSwap(this.item, world, user, hand));
    }

    @Override
    public EquipmentSlot getSlotType() {
        return type.getEquipmentSlot();
    }

    @Override
    public RegistryEntry<SoundEvent> getEquipSound() {//feel free to override this
        return Equipment.super.getEquipSound();
    }

    @Override
    public TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user, Hand hand) {
        return Equipment.super.equipAndSwap(item, world, user, hand);
    }
}
