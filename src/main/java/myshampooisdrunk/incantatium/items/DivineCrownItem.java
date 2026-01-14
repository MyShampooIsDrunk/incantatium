package myshampooisdrunk.incantatium.items;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static myshampooisdrunk.incantatium.items.TimeStopItem.rgbToInt;

public class DivineCrownItem extends AbstractCustomArmorItem{
    public DivineCrownItem() {
        super(Items.FERMENTED_SPIDER_EYE, Incantatium.id("divine_crown"), "incantatium.divine_crown.name",
                Incantatium.getModel(Incantatium.id("divine_crown")),
                EquippableComponent.builder(EquipmentSlot.HEAD).equipSound(SoundEvents.ITEM_ARMOR_EQUIP_GOLD).build());
        addComponent(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                AttributeModifiersComponent.builder()
                        .add(EntityAttributes.ARMOR,
                                new EntityAttributeModifier(
                                        Incantatium.id("crown_armor"),
                                        5, EntityAttributeModifier.Operation.ADD_VALUE),
                                AttributeModifierSlot.HEAD
                        ).add(EntityAttributes.ARMOR_TOUGHNESS,
                                new EntityAttributeModifier(
                                        Incantatium.id("crown_armor_toughness"),
                                        4, EntityAttributeModifier.Operation.ADD_VALUE),
                                AttributeModifierSlot.HEAD
                        ).add(EntityAttributes.MAX_HEALTH,
                                new EntityAttributeModifier(
                                        Incantatium.id("crown_max_health"),
                                        20, EntityAttributeModifier.Operation.ADD_VALUE),
                                AttributeModifierSlot.HEAD
                        ).build()
        );
        String s = "Crown of Salvaris";
        int spaces = 0;
        int[][] nums = new int[][] {
                {212,146,0},{197,138,18},{182,129,36},{167,121,55},{151,112,73},{136,104,91},{121,95,109},
                {106,87,128},{91,79,146},{76,70,164},{61,62,182},{45,53,200},{30,45,219},{15,36,237},{0,28,255}
        };
        MutableText name = Text.literal("");
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == ' '){
                name.append(" ");
                spaces++;
            }else name.append(Text.literal(String.valueOf(s.charAt(i))).withColor(rgbToInt(nums[i-spaces])));
        }

        List<Text> lore = List.of(
                Text.literal("Soulbound").setStyle(Style.EMPTY.withItalic(false).withColor(Colors.LIGHT_GRAY)),
                Text.literal(""),
                Text.literal("The power of the entire server flows through you")
                        .setStyle(Style.EMPTY.withItalic(false).withColor(16755200)),
                Text.literal("You feel their trust in you empowering you")
                        .setStyle(Style.EMPTY.withItalic(false).withColor(16755200)),
                Text.literal("Heed this warning: ")
                        .setStyle(Style.EMPTY.withItalic(false).withColor(16755200))
                        .append(Text.literal("POWER BREEDS CORRUPTION")
                                .setStyle(Style.EMPTY.withItalic(false).withBold(true).withColor(rgbToInt(new int[]{191,6,17})))),
                Text.literal(""),
                Text.literal("Only you can stop the ")
                        .setStyle(Style.EMPTY.withItalic(false).withColor(16755200))
                        .append(Text.literal("AAAAAAAAA")
                                .setStyle(Style.EMPTY.withItalic(false).withObfuscated(true).withColor(Colors.RED))),
                Text.literal("Trusting the wrong person may end us all")
                        .setStyle(Style.EMPTY.withItalic(false).withColor(16755200)),
                Text.literal("Everyone's futures are in your hand, ")
                        .setStyle(Style.EMPTY.withItalic(false).withColor(16755200))
                        .append(Text.literal("my Liege")
                                .setStyle(Style.EMPTY.withBold(true).withItalic(false).withColor(rgbToInt(new int[]{0,28,255})))),
                Text.literal(""),
                Text.literal("PASSIVE").setStyle(Style.EMPTY.withItalic(false).withBold(true).withUnderline(true).withColor(Formatting.GOLD)),
                Text.literal("Demos: The people around you empower you"),
                Text.literal("Kratos: You feel stronger by the second")
        );
        addComponent(DataComponentTypes.CUSTOM_NAME, name.setStyle(Style.EMPTY.withItalic(false)));
        addComponent(DataComponentTypes.LORE, new LoreComponent(lore));
        addComponent(DataComponentTypes.MAX_STACK_SIZE, 1);
        addComponent(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        addComponent(DataComponentTypes.DAMAGE_RESISTANT, new DamageResistantComponent(DamageTypeTags.NO_KNOCKBACK));
        addComponent(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.HEAD).build());
    }

    @Override
    public NbtCompound getCustomNbt() {
        NbtCompound ret = super.getCustomNbt();
        ret.putBoolean("soulbound", true);
        return ret;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        if(entity instanceof PlayerEntity p && world instanceof ServerWorld sWorld && slot != null){
            if(slot.isArmorSlot() && slot == EquipmentSlot.HEAD){
                List<StatusEffectInstance> effects = new ArrayList<>();
                int demos = sWorld.getPlayers(TargetPredicate.createAttackable()
                        .setPredicate((l,w) -> p.distanceTo(l) <= 50), p, Box.of(p.getEntityPos(), 50,50,50)).size();
                effects.add(new StatusEffectInstance(StatusEffects.GLOWING, 10));
                if(demos >= 1){
                    effects.add(new StatusEffectInstance(StatusEffects.REGENERATION, 10));
                }
                if(demos >= 3){
                    effects.add(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 10));
                }
                if (demos >= 21) {
                    effects.add(new StatusEffectInstance(StatusEffects.STRENGTH, 10, 1));
                    effects.add(new StatusEffectInstance(StatusEffects.SPEED, 10, 1));
                }
                else if (demos >= 8) {
                    effects.add(new StatusEffectInstance(StatusEffects.STRENGTH, 10));
                    effects.add(new StatusEffectInstance(StatusEffects.SPEED, 10));
                }
                if (demos >= 55) {
                    effects.add(new StatusEffectInstance(StatusEffects.RESISTANCE, 10));
                }

                effects.forEach(p::addStatusEffect);
            }
        }
    }
}
