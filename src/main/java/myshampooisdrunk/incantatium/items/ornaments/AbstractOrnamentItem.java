package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.OrnamentAbilities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.management.Attribute;
import java.util.Objects;

public abstract class AbstractOrnamentItem extends AbstractCustomItem {
    private final int cooldown; //in ticks

    public AbstractOrnamentItem(Identifier identifier, @Nullable String itemName, int cooldown) {
        super(Items.INK_SAC,identifier, itemName, true);
        this.cooldown = Incantatium.DEV_MODE ? 600 : cooldown;
    }

    public void cooldownItems(PlayerEntity p){
        p.getItemCooldownManager().set(Items.INK_SAC,cooldown);
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean canUse(PlayerEntity p, Hand hand){
        if(hand == Hand.OFF_HAND) {
            OrnamentAbilities abilities = p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            return abilities.getActive().contains(identifier);
        }
        return false;
    }

    protected abstract void getActiveEffects(ItemStack stack, World world, PlayerEntity player);

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
//        boolean modified = false;
        if(entity instanceof PlayerEntity p){
            AttributeModifiersComponent attrMods = AttributeModifiersComponent.DEFAULT;
            OrnamentAbilities abilities = p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            if(abilities.getActive().contains(identifier)) {
//                System.out.println("ability is active");
                if(!Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
//                    System.out.println("skiby dee");
                    stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
//                    modified = true;
                }
                attrMods = getAttributeModifiers();
                if(stack == p.getOffHandStack()) getActiveEffects(stack, world, p);
            }
            else if(!abilities.getActive().contains(identifier) && Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
                stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
//                modified = true;
            }
            if(!Objects.equals(stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS), attrMods)){
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, attrMods);
//                modified = true;
            }
//            if(modified) {
//                p.getInventory().setStack(slot, stack);
////                System.out.println("modified shit");
//            }
        }
    }

    @Override
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable cir) {
        super.use(world, user, hand, cir);
        if(canUse(user, hand))
            cooldownItems(user);
    }

    public AttributeModifiersComponent getAttributeModifiers(){ // should only be active in offhand slot fyi
        return AttributeModifiersComponent.DEFAULT;
    }
}
