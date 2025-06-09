package myshampooisdrunk.incantatium.mixin;

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import myshampooisdrunk.incantatium.util.MultiblockCollisionSpliterator;
import myshampooisdrunk.incantatium.world.IMultiblockView;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.List;

@Mixin(CollisionView.class)
public interface CollisionViewMixin extends BlockView {
    @ModifyReturnValue(method = "getBlockCollisions", at = @At("RETURN"))
    default Iterable<VoxelShape> getBlockAndMultiblockCollisions(Iterable<VoxelShape> original, @Local(argsOnly = true) Entity entity, @Local(argsOnly = true) Box box) {
        if(this instanceof CollisionView v) {
            return () -> Iterators.concat(new BlockCollisionSpliterator<>(v, entity, box, false, (pos, voxelShape) -> voxelShape), new MultiblockCollisionSpliterator<>(v, entity, box, false, (pos, voxelShape) -> voxelShape));
        }
        return original;
    }
}
