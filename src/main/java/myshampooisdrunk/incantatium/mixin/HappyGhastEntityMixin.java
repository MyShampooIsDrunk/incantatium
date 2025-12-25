package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(HappyGhastEntity.class)
public abstract class HappyGhastEntityMixin extends AnimalEntity {

    @WrapMethod(method = "createHappyGhastAttributes")
    private static DefaultAttributeContainer.Builder wrapHappyGhastAttributes(Operation<DefaultAttributeContainer.Builder> original) {
        return original.call()
                .add(EntityAttributes.TEMPT_RANGE, 64.0);
    }

    @Inject(method = "initAdultHappyGhast", at = @At("TAIL"))
    public void injectHappyGhastAttributeModifiers(CallbackInfo ci) {
        if(this.getEntityWorld() instanceof ServerWorld) {
            if(this.getAttributes().getCustomInstance(EntityAttributes.FLYING_SPEED) != null) {
                Objects.requireNonNull(this.getAttributes().getCustomInstance(EntityAttributes.FLYING_SPEED)).setBaseValue(0.09);
            }
            if(this.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED) != null) Objects.requireNonNull(this.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(0.09);
        }
    }

    protected HappyGhastEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
}
