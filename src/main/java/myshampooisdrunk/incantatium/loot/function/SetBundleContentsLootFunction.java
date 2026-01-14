package myshampooisdrunk.incantatium.loot.function;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.*;

public class SetBundleContentsLootFunction extends ConditionalLootFunction {
    public static final MapCodec<SetBundleContentsLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> addConditionsField(instance)
                    .and(
                            instance.group(
                                    Codec.unboundedMap(Item.ENTRY_CODEC, LootNumberProviderTypes.CODEC)
                                            .fieldOf("item")
                                            .forGetter(function -> function.itemCounts)
                            ).t1()
                    )
                    .apply(instance, SetBundleContentsLootFunction::new)
    );

    private final Map<RegistryEntry<Item>, LootNumberProvider> itemCounts;

    SetBundleContentsLootFunction(List<LootCondition> conditions, Map<RegistryEntry<Item>, LootNumberProvider> itemCounts) {
        super(conditions);
        this.itemCounts = Map.copyOf(itemCounts);
    }

    @Override
    public LootFunctionType<SetBundleContentsLootFunction> getType() {
        return IncantatiumRegistry.SET_BUNDLE_CONTENTS;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if(!stack.isOf(Items.BUNDLE))return stack;
        List<ItemStack> items = new ArrayList<>();
        itemCounts.forEach((item,count)->{
            int max = item.value().getMaxCount();
            int amount = count.nextInt(context);
            int stacks = amount/max;
            int extra = amount-stacks*max;
            for (int i = 0; i < stacks; i++) {
                items.add(new ItemStack(item,max));
            }
            if(extra!=0) {
                items.add(new ItemStack(item, extra));
            }
        });
        stack.set(DataComponentTypes.BUNDLE_CONTENTS, new BundleContentsComponent(items));
        return stack;
    }

    public static class Builder extends ConditionalLootFunction.Builder<SetBundleContentsLootFunction.Builder> {
        private final ImmutableMap.Builder<RegistryEntry<Item>, LootNumberProvider> itemCounts = ImmutableMap.builder();

        public Builder() {
        }

        protected SetBundleContentsLootFunction.Builder getThisBuilder() {
            return this;
        }

        public SetBundleContentsLootFunction.Builder item(RegistryEntry<Item> item, LootNumberProvider count) {
            this.itemCounts.put(item, count);
            return this;
        }

        @Override
        public LootFunction build() {
            return new SetBundleContentsLootFunction(this.getConditions(), this.itemCounts.build());
        }
    }
}