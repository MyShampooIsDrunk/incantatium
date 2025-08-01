package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.OrnamentAbilities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.management.Attribute;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractOrnamentItem extends AbstractCustomItem {
    private final int cooldown; //in ticks

    public AbstractOrnamentItem(Identifier identifier, @Nullable String itemName, int cooldown) {
        super(Items.FERMENTED_SPIDER_EYE, identifier, itemName);//Incantatium.getModel(identifier)
        this.cooldown = Incantatium.DEV_MODE ? 600 : cooldown;
        addComponent(DataComponentTypes.USE_COOLDOWN, new UseCooldownComponent(cooldown / 20f, Optional.of(Incantatium.id("ornament_cooldown"))));
    }

    public void cooldownItems(PlayerEntity p){
        p.getItemCooldownManager().set(Incantatium.id("ornament_cooldown"),cooldown);
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean canUse(PlayerEntity p, Hand hand){
        if(hand == Hand.OFF_HAND) {
            OrnamentAbilities abilities = p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            return abilities.isActive(identifier);
        }
        return false;
    }

    protected abstract void getActiveEffects(ItemStack stack, World world, PlayerEntity player);

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if(entity instanceof PlayerEntity p && !world.isClient()){
            AttributeModifiersComponent attrMods = AttributeModifiersComponent.DEFAULT;
            OrnamentAbilities abilities = p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            if(abilities.isActive(identifier)) {
                if(!Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
                    stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                }
                attrMods = getAttributeModifiers();
                if(stack == p.getOffHandStack()) getActiveEffects(stack, world, p);
            }
            else if(!abilities.isActive(identifier) && Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
                stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            }
            if(!Objects.equals(stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS), attrMods)){
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, attrMods);
            }
        }
    }

    @Override
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.use(world, user, hand, cir);
        if(canUse(user, hand))
            cooldownItems(user);
    }

    public AttributeModifiersComponent getAttributeModifiers(){ // should only be active in offhand slot fyi
        return AttributeModifiersComponent.DEFAULT;
    }
}
