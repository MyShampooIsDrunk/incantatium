package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.OrnamentAbilities;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

    public AbstractOrnamentItem(Identifier identifier, String itemName, int cooldown) {
        this(identifier, itemName, cooldown, true);
    }

    public AbstractOrnamentItem(Identifier identifier, String itemName, int cooldown, boolean useCooldown) {
        super(Items.FERMENTED_SPIDER_EYE, identifier, itemName, Incantatium.getModel(identifier));//Incantatium.getModel(identifier)
        this.cooldown = Math.min(Incantatium.DEV_MODE ? 600 : cooldown, cooldown);
        if(useCooldown) addComponent(DataComponentTypes.USE_COOLDOWN, new UseCooldownComponent(this.cooldown / 20f, Optional.of(Incantatium.id("ornament_cooldown"))));
        addComponent(DataComponentTypes.MAX_STACK_SIZE, 1);
        addComponent(DataComponentTypes.MAX_DAMAGE, 3);
        addComponent(DataComponentTypes.DAMAGE, 0);
    }

    public void cooldownItems(PlayerEntity p){
        p.getItemCooldownManager().set(Incantatium.id("ornament_cooldown"), cooldown);
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean canUse(PlayerEntity p, Hand hand) {
        ItemStack stack;
        if(hand == Hand.OFF_HAND && (stack = p.getOffHandStack()) != null && stack.getDamage() < stack.getMaxDamage()) {
            OrnamentAbilities abilities = p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            return abilities.isActive(identifier);
        }
        return false;
    }

    protected abstract void getActiveEffects(ItemStack stack, World world, PlayerEntity player);

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        if(entity instanceof ServerPlayerEntity p && !world.isClient()){
            AttributeModifiersComponent attrMods = AttributeModifiersComponent.DEFAULT;
            OrnamentAbilities abilities = p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            if(abilities.isActive(identifier) && slot == EquipmentSlot.OFFHAND && stack.getDamage() < stack.getMaxDamage()) {
                if(!Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
                    stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                    p.currentScreenHandler.sendContentUpdates();
                }
                attrMods = getAttributeModifiers();
                getActiveEffects(stack, world, p);
            }
            else if(!abilities.isActive(identifier) && Boolean.TRUE.equals(stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))) {
                stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
                p.currentScreenHandler.sendContentUpdates();
            }
            if(!Objects.equals(stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS), attrMods)){
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, attrMods);
            }
        }
    }

    @Override
    public void use(World world, LivingEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.use(world, user, hand, cir);
        if(user instanceof PlayerEntity p && canUse(p, hand))
            cooldownItems(p);
    }

    public AttributeModifiersComponent getAttributeModifiers(){ // should only be active in offhand slot fyi
        return AttributeModifiersComponent.DEFAULT;
    }

    public abstract AbstractMultiblockRecipe recipe();
}
