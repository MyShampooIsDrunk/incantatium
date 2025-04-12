package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SalvationOrnamentItem extends AbstractOrnamentItem{
    public SalvationOrnamentItem() {
        super(Incantatium.id("salvation_ornament"), "Salvation", 9000);
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable cir) {
    }
}
