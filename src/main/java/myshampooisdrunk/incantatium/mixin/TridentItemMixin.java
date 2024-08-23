package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.PlayerRiptideCooldown;
import myshampooisdrunk.incantatium.component.RiptideCooldown;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentItem.class)
public class TridentItemMixin {

    @Redirect(method = "use", at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean alwaysUseRiptide(PlayerEntity instance){
        RiptideCooldown cooldown = instance.getComponent(Incantatium.RIPTIDE_COOLDOWN_COMPONENT_KEY);
        if(cooldown.get() > 0) return true;

        if(!instance.getWorld().isClient()){
            instance.sendMessage(Text.literal("RIPTIDE IS ON COOLDOWN: " +
                    (cooldown.getCooldown() - cooldown.getLastUse()+19)/20 + " SECONDS REMAINING").setStyle(
                    Style.EMPTY.withColor(Colors.LIGHT_RED)
            ), true);
        }
        return false;
    }

    @Redirect(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean alwaysUseRiptide1(PlayerEntity instance){
        return true;
    }

    @Inject(method = "onStoppedUsing", at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",ordinal = 1), cancellable = true)
    public void useRiptideEnhance(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, @Local(ordinal = 0) float f){
        if(user instanceof ServerPlayerEntity p){

            float g = p.getYaw();
            float h = p.getPitch();
            float j = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
            float k = -MathHelper.sin(h * (float) (Math.PI / 180.0));
            float l = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
            float m = MathHelper.sqrt(j * j + k * k + l * l);
            j *= 2 / m; //used to be f instead of 2
            k *= 2 / m;
            l *= 2 / m;
            int max = PlayerRiptideCooldown.MAX_CHARGES;
            RiptideCooldown cooldown = p.getComponent(Incantatium.RIPTIDE_COOLDOWN_COMPONENT_KEY);
            if(f <= 3) cooldown.setCooldown(100 * (1 + max - cooldown.get()) + (100 * (int)Math.clamp(4 - f/0.75f,0,2)));//5s less per lvl of riptide but also 5s less per charge
            else {
                cooldown.setCooldown(0);
            }
            if(cooldown.useRiptide()){
                System.out.println("velocity pre mod: " + p.getVelocity());
                System.out.println("set user velocity");
                p.addVelocity(j, k, l);
                System.out.println("velocity after: " + p.getVelocity());
                p.useRiptide(20, 8.0F * f * 2f/3f, stack);
                if (p.isOnGround()) {
                    p.move(MovementType.SELF, new Vec3d(0.0, 1.1999999F, 0.0));
                }
                RegistryEntry<SoundEvent> registryEntry = (RegistryEntry<SoundEvent>)EnchantmentHelper.getEffect(stack, EnchantmentEffectComponentTypes.TRIDENT_SOUND)
                        .orElse(SoundEvents.ITEM_TRIDENT_THROW);
                world.playSoundFromEntity(null, p, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                if(!user.isTouchingWaterOrRain() && f <= 3 && user.canTakeDamage()){
                    System.out.println("trying to damage user");
                    user.damage(
                            new DamageSource(user.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(Incantatium.TRIDENT_BYPASS), user),
                            Math.clamp(10f - 8f * f/3f, 0, 10)
                    );
                }
                user.velocityModified = true;
            } else{
                System.out.println("player couldn't use riptide");
            }
//            else{
//                ci.cancel();//if you cant use riptide stop running the rest of the function which does all the shit for whether or not you can use riptide
//            }
        }
        ci.cancel();
        // 3 -> 3; 2.25 -> 2; 1.5 -> 1 | riptide = .75 * (ench_level + 1) | lvl = riptide/.75 - 1
    }

    @ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;useRiptide(IFLnet/minecraft/item/ItemStack;)V"))
    private float modifyDamage(float riptideAttackDamage, @Local(argsOnly = true) ItemStack stack, @Local PlayerEntity playerEntity){
        float f = EnchantmentHelper.getTridentSpinAttackStrength(stack, playerEntity);
        return riptideAttackDamage * f * 2f/3f;//riptide 1 -> 8 dmg | r2 -> 12 dmg | r3 -> 16 dmg
    }
}
//3 -> 2; 2 -> 4; 1 -> 6: 6 - 8r/3
//                    Math.clamp((27f-7) / 4f,0, 10)
//^^^ these are hard mode values so i gotta scale it by 1/1.5 = 2/3