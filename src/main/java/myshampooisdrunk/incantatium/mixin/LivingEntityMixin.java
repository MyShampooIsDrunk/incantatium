package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import myshampooisdrunk.drunk_server_toolkit.item.CustomItemHelper;
import myshampooisdrunk.incantatium.items.ornaments.SalvationOrnamentItem;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.entity.effect.StatusEffects.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private static final int RAVAGING_HEIGHT_OFFSET = 2; //for the sole purpose of ravaging not being too op

    @Unique private int soulLinkedRegenTicks = 0;

    @Unique
    private static final List<RegistryEntry<StatusEffect>> negative = List.of(BLINDNESS, WEAKNESS, HUNGER,
            POISON, WITHER, GLOWING, LEVITATION, UNLUCK, DARKNESS, WIND_CHARGED, WEAVING, OOZING, INFESTED, SLOWNESS, NAUSEA, MINING_FATIGUE);

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract boolean hasStackEquipped(EquipmentSlot slot);

    @Shadow public abstract void heal(float amount);

    @Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @Shadow public abstract @Nullable ItemStack getBlockingItem();

    @Shadow public abstract void setHealth(float health);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    private final LivingEntity dis = (LivingEntity) (Object) this;


    @Inject(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyMovementEffects(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V"))
    public void injectRavagingFall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
        double height = Math.min(this.fallDistance, this.getAttributeValue(EntityAttributes.SAFE_FALL_DISTANCE) * 5d/3d + RAVAGING_HEIGHT_OFFSET);
        if(this.getEntityWorld() instanceof ServerWorld world && height >= 7 && this.hasControllingPassenger() && this.hasStackEquipped(EquipmentSlot.BODY) &&
                this.getEquippedStack(EquipmentSlot.BODY).getEnchantments().getEnchantments()
                        .contains(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(IncantatiumRegistry.RAVAGING))) {
            int level = this.getEquippedStack(EquipmentSlot.BODY).getEnchantments().getLevel(world.getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(IncantatiumRegistry.RAVAGING));
            height -= RAVAGING_HEIGHT_OFFSET;

            double radius = 1;
            double damage = (level + 1) * Math.min(height, 12);
            height -= Math.min(height, 12);

            if(height > 0) {
                damage += (level + 1) / 2d * Math.min(height, 14);
                height -= Math.min(height, 14);
                radius += 0.5;
            }

            if(height > 0) {
                damage += 1.5 * Math.min(height, 16);
                height -= Math.min(height, 16);
                radius += 1;
            }

            if(height > 0) {
                damage += height;
                radius += 1.5;
            }
            Box box = this.getBoundingBox().expand(radius,2,radius).offset(0,1,0);

            List<Entity> entities = world.getOtherEntities(this, box.expand(0.5), e -> !e.hasVehicle() || e.getVehicle() != this);


            world.playSound(this, this.getBlockPos(), SoundEvents.ITEM_MACE_SMASH_GROUND, SoundCategory.PLAYERS);

            Object2IntMap<Vector2i> surface = new Object2IntOpenHashMap<>();

            for (BlockPos blockPos : BlockPos.iterate(box)) {
                if(world.getBlockState(blockPos).isSideSolid(world, blockPos, Direction.UP, SideShapeType.CENTER) &&
                        !world.getBlockState(blockPos.up()).isSolidBlock(world, blockPos.up())) {
                    Vector2i xz = new Vector2i(blockPos.getX(),blockPos.getZ());
                    if(!surface.containsKey(xz)) surface.put(xz,blockPos.getY());
                    else {
                        surface.put(xz, Math.max(surface.getInt(xz),blockPos.getY()));
                    }
                }
            }

            double finalDamage = damage;
            surface.forEach((xz, y) -> {
                BlockPos pos = BlockPos.ofFloored(xz.x, y, xz.y);
                Vec3d v = pos.toCenterPos().add(0,1,0);
                double d = this.squaredDistanceTo(pos.toCenterPos());
                world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(pos)),
                        v.x,v.y,v.z, 5, 0.0, 0.0, 0.0, finalDamage / (100d * d));
            });

            for (Entity entity : entities) {
                double dist;
                if((dist = Math.max(this.squaredDistanceTo(entity), 1)) <= 16) {
                    entity.damage(world, world.getDamageSources().maceSmash(this.getControllingPassenger()), (float) (damage / dist));
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void injectSoulLinked(CallbackInfo ci) {
        if(this.getEquippedStack(EquipmentSlot.BODY).getEnchantments().getEnchantments().contains(this.getEntityWorld().getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(IncantatiumRegistry.SOUL_LINKED))) {
            if(20 == soulLinkedRegenTicks++) {
                this.heal(1);
                soulLinkedRegenTicks %= 20;
            }
        }
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    public float modifyDamage(float original, @Local(argsOnly = true) ServerWorld world, @Local(argsOnly = true) DamageSource source) {
        if(this.hasVehicle() && this.getVehicle() instanceof LivingEntity vehicle) {
            if (vehicle.hasStackEquipped(EquipmentSlot.BODY) && vehicle.getEquippedStack(EquipmentSlot.BODY).getEnchantments().getEnchantments().contains(world.getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(IncantatiumRegistry.SOUL_LINKED))) {
                vehicle.damage(world, source, original * 0.2f);
                return 0.5f * original;
            }
        }
        return original;
    }

    @Inject(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void injectSalvationOrnament(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local Hand hand, @Local ItemStack stack) {
        if(incantatium$testSaddleForDeathProtector()) cir.setReturnValue(true);

        if(hand == Hand.OFF_HAND && stack != null && dis instanceof ServerPlayerEntity p) {
            CustomItemHelper.getCustomItem(stack).ifPresent(custom -> {
                if(custom instanceof SalvationOrnamentItem o) {
                    boolean bl = true;
                    NbtComponent nbt;
                    if((nbt = stack.get(DataComponentTypes.CUSTOM_DATA)) != null) {
                        NbtCompound comp = nbt.copyNbt();
                        int charges = comp.getInt("charges").orElse(-1);
                        if(charges > 0 && charges <= 2) {
                            bl = false;
                            comp.putInt("charges", charges - 1);
                            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(comp));
                        }
                    }

                    if(bl && p.getItemCooldownManager().getCooldownProgress(stack, 0.0F) == 0.0F) {
                        o.cooldownItems(p);
                        bl = false;
                    }

                    if(!bl && dis instanceof ServerPlayerEntity player)
                        cir.setReturnValue(incantatium$useDeathProtector(stack, player));
                }
            });
        }
    }

    @Unique
    private boolean incantatium$useDeathProtector(ItemStack itemStack, ServerPlayerEntity player) {
        DeathProtectionComponent deathProtectionComponent = itemStack.get(DataComponentTypes.DEATH_PROTECTION);
        if(deathProtectionComponent == null) return false;
        player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
        Criteria.USED_TOTEM.trigger(player, itemStack);
        this.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
        this.setHealth(1.0F);
        deathProtectionComponent.applyDeathEffects(itemStack, player);
        this.getEntityWorld().sendEntityStatus(this, EntityStatuses.USE_TOTEM_OF_UNDYING);
        return true;
    }

//    @WrapOperation(method = "tryUseDeathProtector", at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
//    public void useSalvationOrnament(ItemStack stack, int amount, Operation<Void> original){
//    }

    private boolean incantatium$testSaddleForDeathProtector() {
        if(!this.hasStackEquipped(EquipmentSlot.SADDLE)) return false;
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.SADDLE);
        DeathProtectionComponent deathProtectionComponent = null;

        deathProtectionComponent = itemStack.get(DataComponentTypes.DEATH_PROTECTION);
        if (deathProtectionComponent == null)
            return false;
        else itemStack.decrement(1);


        if (dis instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
            Criteria.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
            this.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
        }

        this.setHealth(1.0F);
        deathProtectionComponent.applyDeathEffects(itemStack, dis);
        this.getEntityWorld().sendEntityStatus(this, EntityStatuses.USE_TOTEM_OF_UNDYING);

        return true;
    }

    @Inject(method = "takeShieldHit", at = @At("HEAD"))
    public void injectDisableMace(ServerWorld world, LivingEntity attacker, CallbackInfo ci) {
        if(attacker.getWeaponStack().isOf(Items.MACE) && attacker instanceof PlayerEntity p && MaceItem.shouldDealAdditionalDamage(p)) {
            p.getItemCooldownManager().set(p.getWeaponStack(), 300);
            world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1.5f ,0.5f);
            ItemStack blockingItem = this.getBlockingItem();
            BlocksAttacksComponent c;
            if(blockingItem != null && (c = blockingItem.get(DataComponentTypes.BLOCKS_ATTACKS)) != null) {
                c.applyShieldCooldown(world, dis, 7.5f, blockingItem);
            }
        }
    }

}
