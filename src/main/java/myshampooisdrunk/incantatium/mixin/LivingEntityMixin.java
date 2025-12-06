package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import myshampooisdrunk.drunk_server_toolkit.item.CustomItemHelper;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.ornaments.SalvationOrnamentItem;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SideShapeType;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.entity.effect.StatusEffects.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private static final int RAVAGING_HEIGHT_OFFSET = 2; //for the sole purpose of ravaging not being too op

    @Unique private int soulLinkedRegenTicks = 0;

    @Unique
    private static final List<RegistryEntry<StatusEffect>> negative = List.of(BLINDNESS, WEAKNESS, HUNGER,
            POISON, WITHER, GLOWING, LEVITATION, UNLUCK, DARKNESS, WIND_CHARGED, WEAVING, OOZING, INFESTED, SLOWNESS, NAUSEA, MINING_FATIGUE);

    @Shadow public abstract void jump();

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract boolean hasStackEquipped(EquipmentSlot slot);

    @Shadow public abstract void heal(float amount);

    @Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

//    @Inject(method="getPreferredEquipmentSlot",at=@At("HEAD"), cancellable = true)
//    public void addPreferredEquipmentSlotCustomArmorItem(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir){
//        CustomItemHelper.getCustomItem(stack).ifPresent(custom -> {
//            if(custom instanceof AbstractCustomArmorItem i) cir.setReturnValue(i.getSlotType());
//        });
//    }

    private final LivingEntity dis = (LivingEntity) (Object) this;


    @Inject(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyMovementEffects(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V"))
    public void injectRavagingFall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
        double height = Math.min(this.fallDistance, this.getAttributeValue(EntityAttributes.SAFE_FALL_DISTANCE) * 5d/3d + RAVAGING_HEIGHT_OFFSET);
        if(this.getEntityWorld() instanceof ServerWorld world && height >= 7 && this.hasControllingPassenger() && this.hasStackEquipped(EquipmentSlot.BODY) &&
                this.getEquippedStack(EquipmentSlot.BODY).getEnchantments().getEnchantments()
                        .contains(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(IncantatiumRegistry.RAVAGING))) {

            height -= RAVAGING_HEIGHT_OFFSET;

            double radius = 1;
            double damage = 4 * Math.min(height, 12);
            height -= Math.min(height, 12);

            if(height > 0) {
                damage += 2 * Math.min(height, 14);
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

    @Inject(method="tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void cancelReviveIfOnCooldown(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local ItemStack stack, @Local DeathProtectionComponent component){
        if(dis instanceof ServerPlayerEntity p){
            if(p.getItemCooldownManager().isCoolingDown(stack)) cir.setReturnValue(false);
        }
    }

    @Redirect(method = "tryUseDeathProtector",at= @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    public void useSalvationOrnament(ItemStack instance, int amount){
        AtomicBoolean bl = new AtomicBoolean(true);
        CustomItemHelper.getCustomItem(instance).ifPresent(custom -> {
            if(custom instanceof SalvationOrnamentItem o && dis instanceof ServerPlayerEntity p) {
                o.cooldownItems(p);
                bl.set(false);
            }
        });
        if(bl.get()) instance.decrement(amount);
    }

}
