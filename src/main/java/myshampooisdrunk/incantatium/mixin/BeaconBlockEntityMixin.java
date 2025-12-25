package myshampooisdrunk.incantatium.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @ModifyVariable(method = "applyPlayerEffects", at=@At(value="STORE"), ordinal = 0)
    private static double getBeaconRange(double d, @Local(argsOnly = true) int beaconLevel){
        return Math.pow(2,(beaconLevel+1))*5;
    }
}
