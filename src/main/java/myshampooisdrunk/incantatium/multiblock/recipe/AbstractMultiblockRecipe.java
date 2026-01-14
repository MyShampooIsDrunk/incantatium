package myshampooisdrunk.incantatium.multiblock.recipe;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.recipe.CustomIngredient;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.multiblock.inventory.MultiblockInventory;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractMultiblockRecipe implements Recipe<MultiblockRecipeInput> {
    protected final ItemStack result;

    public AbstractMultiblockRecipe(ItemStack result) {
        this.result = result;
    }

    @Override
    public ItemStack craft(MultiblockRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return result;
    }

    @Override
    public RecipeSerializer<? extends Recipe<MultiblockRecipeInput>> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<? extends Recipe<MultiblockRecipeInput>> getType() {
        return null;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return null;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return null;
    }

    public static class MultiblockEntryIngredient {
        private final Map<Item,Integer> items;
        private final Map<TagKey<Item>, Integer> tags;
        private final Map<IngredientProvider, Integer> stacks;
        private final Map<Item,ComponentMap> itemComponents;
        private final Map<TagKey<Item>, ComponentMap> tagComponents;

        private MultiblockEntryIngredient(Map<Item,Integer> items, Map<TagKey<Item>,Integer> tags,
                                          Map<IngredientProvider,Integer> stacks, Map<Item, ComponentMap> itemComponents,
                                          Map<TagKey<Item>, ComponentMap> tagComponents) {
            this.items = items;
            this.tags = tags;
            this.stacks = stacks;
            this.itemComponents = itemComponents;
            this.tagComponents = tagComponents;
        }

        public boolean test(MultiblockInventory.Entry entry, RegistryWrapper.WrapperLookup registries) {
            //this doesnt work entirely for custom data
            ItemStack testStack = entry.stack();
            int count = entry.count();
            for (Item item : items.keySet()) {
//                System.out.println("item: " + item);
                boolean or = testStack.isOf(item) && count == items.get(item);//if item is correct, temp variable is true
//                System.out.println("or before components: " + or);
                if(itemComponents.containsKey(item) && or) {
                    ComponentMap map = itemComponents.get(item);
//                    System.out.println("map: " + map + " size: " + map.size());
                    for (Component<?> c : map) {
//                        System.out.println("component: " + c.value());
                        if(!or) break;
                        if(!testStack.contains(c.type())) {
                            or = false;
//                            System.out.println("item has component");
//                            System.out.println("item component value: " + testStack.get(c.type()));
                        }
                        if(!(or && Objects.equals(c.value(), testStack.get(c.type())))) or = false;
                    }
//                    System.out.println("or after components: " + or);
                }
                if(or) return true;
            }

            for (TagKey<Item> tag : tags.keySet()) {
                boolean or = testStack.isIn(tag) && count == tags.get(tag);//if item is correct, temp variable is true
                if(tagComponents.containsKey(tag)) {
                    ComponentMap map = tagComponents.get(tag);
                    for (Component<?> c : map) {
                        if(!testStack.contains(c.type()) && c.value().equals(testStack.get(c.type()))) or = false;//if item is specified to require a component map, and not all components are the same
                    }
                }
                if(or) return true;
            }

            CustomIngredient imLazy = CustomIngredient.ofStacks(true, testStack);
            for (IngredientProvider stack : stacks.keySet()) {
                if(stacks.get(stack) == count && imLazy.test(stack.accept(registries))) return true;
            }

            return false;
        }

        public boolean test(ItemStack stack, RegistryWrapper.WrapperLookup registries) {
            int count = stack.getCount();
            if(stack.contains(DataComponentTypes.CUSTOM_DATA)) {
                NbtCompound c = Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt();
                if(c.contains("singletonItemCount")) count = c.getInt("singletonItemCount").orElse(0);
            }
            return test(MultiblockInventory.Singleton.create(stack, count), registries);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static MultiblockEntryIngredient ofCustom(AbstractCustomItem item, int count, @Nullable List<ComponentType<?>> types) {
            if(types == null || types.isEmpty()) {
                return builder().addItem(item.getItem(), count, IncantatiumRegistry.getCustomData(item)).build();
            } else {
                return builder().addItem(item.getItem(), count, IncantatiumRegistry.getCustomData(item, types.toArray(new ComponentType<?>[]{}))).build();
            }
        }

        public record Builder(Map<Item,Integer> items, Map<TagKey<Item>,Integer> tags,
                              Map<IngredientProvider,Integer> stacks, Map<Item, ComponentMap> itemComponents,
                              Map<TagKey<Item>, ComponentMap> tagComponents) {

            private Builder() {
                this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
            }


            public Builder add(MultiblockInventory.Entry entry) {
                stacks.put(IngredientProvider.fromStack(entry.stack()), entry.count());
                return this;
            }

            public Builder addItem(Item item, int count) {
                items.put(item,count);
                return this;
            }

            public Builder addItem(Item item, int count, ComponentMap components) {
                items.put(item,count);
                itemComponents.put(item,components);
                return this;
            }

            public Builder addTag(TagKey<Item> tag, int count) {
                tags.put(tag,count);
                return this;
            }

            public Builder addTag(TagKey<Item> tag, int count, ComponentMap components) {
                tags.put(tag,count);
                tagComponents.put(tag,components);
                return this;
            }

            public Builder addStack(ItemStack stack, int count) {
                this.stacks.put(IngredientProvider.fromStack(stack),count);
                return this;
            }

            public Builder addStack(IngredientProvider provider, int count) {
                this.stacks.put(provider, count);
                return this;
            }

            public MultiblockEntryIngredient build() {
                return new MultiblockEntryIngredient(items, tags, stacks, itemComponents, tagComponents);
            }

        }
    }

    @FunctionalInterface
    public interface IngredientProvider {
        ItemStack accept(RegistryWrapper.WrapperLookup registries);

        static IngredientProvider fromStack(ItemStack stack) {
            return __ -> stack;
        }

        static IngredientProvider enchantedBook(RegistryKey<Enchantment> key, int level) {
            return lookup -> {
                AtomicReference<ItemStack> ret = new AtomicReference<>();
                lookup.getOptional(RegistryKeys.ENCHANTMENT).flatMap(impl -> impl.getOptional(key)).ifPresent(ench -> ret.set(EnchantmentHelper.getEnchantedBookWith(new EnchantmentLevelEntry(ench, level))));
                if(ret.get() == null) Incantatium.LOGGER.info("TS IS NULL IDK WHY");
                if(ret.get() == null) return Items.COMMAND_BLOCK.getDefaultStack();
                else return ret.get();
            };
        }
    }

}
