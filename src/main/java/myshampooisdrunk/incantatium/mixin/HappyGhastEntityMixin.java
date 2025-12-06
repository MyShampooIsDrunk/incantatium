package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HappyGhastEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HappyGhastEntity.class)
public class HappyGhastEntityMixin {
    @WrapMethod(method = "createHappyGhastAttributes")
    private static DefaultAttributeContainer.Builder wrapHappyGhastAttributes(Operation<DefaultAttributeContainer.Builder> original) {
        return original.call()
                .add(EntityAttributes.TEMPT_RANGE, 64.0)
                .add(EntityAttributes.FLYING_SPEED, 0.15);
    }
}
