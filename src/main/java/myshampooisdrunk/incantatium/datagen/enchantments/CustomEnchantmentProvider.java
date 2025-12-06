package myshampooisdrunk.incantatium.datagen.enchantments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.block.Block;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class CustomEnchantmentProvider extends FabricDynamicRegistryProvider {
    public CustomEnchantmentProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        RegistryEntryLookup<DamageType> damageTypeLookup = registries.getOrThrow(RegistryKeys.DAMAGE_TYPE);
        RegistryEntryLookup<Enchantment> enchantmentLookup = registries.getOrThrow(RegistryKeys.ENCHANTMENT);
        RegistryEntryLookup<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
        RegistryEntryLookup<Block> blockLookup = registries.getOrThrow(RegistryKeys.BLOCK);
        RegistryEntryLookup<EntityType<?>> entityTypeLookup = registries.getOrThrow(RegistryKeys.ENTITY_TYPE);

//        register(entries, IncantatiumRegistry.RAVAGING, Enchantment.builder(Enchantment.definition(
//                        itemLookup.getOrThrow(IncantatiumRegistry.HORSE_ARMOR),
//                        2,
//                        3,
//                        Enchantment.leveledCost(25, 25),
//                        Enchantment.leveledCost(75, 25),
//                        4,
//                        AttributeModifierSlot.BODY
//                )
//        ).addEffect(EnchantmentEffectComponentTypes.ATTRIBUTES,
//                new AttributeEnchantmentEffect(Incantatium.id("ravaging_jump_height"),
//                        EntityAttributes.JUMP_STRENGTH,
//                        new EnchantmentLevelBasedValue.Linear(0.1f, 0.1f),
//                        EntityAttributeModifier.Operation.ADD_VALUE
//                )
//        ).addEffect(EnchantmentEffectComponentTypes.ATTRIBUTES,
//                new AttributeEnchantmentEffect(Incantatium.id("ravaging_safe_fall_distance"),
//                        EntityAttributes.SAFE_FALL_DISTANCE,
//                        new EnchantmentLevelBasedValue.Linear(10f, 10f),
//                        EntityAttributeModifier.Operation.ADD_VALUE
//                )));
//
//        register(entries, IncantatiumRegistry.SOUL_LINKED, Enchantment.builder(Enchantment.definition(
//                        itemLookup.getOrThrow(IncantatiumRegistry.HORSE_ARMOR),
//                        2,
//                        1,
//                        Enchantment.leveledCost(25, 25),
//                        Enchantment.leveledCost(75, 25),
//                        4,
//                        AttributeModifierSlot.BODY
//                )
//        ));
    }

    @Override
    public String getName() {
        return "CustomEnchantmentProvider";
    }

    private void register(Entries entries, RegistryKey<Enchantment> key, Enchantment.Builder builder, ResourceCondition... resourceConditions) {
        entries.add(key, builder.build(key.getValue()), resourceConditions);
    }
}
