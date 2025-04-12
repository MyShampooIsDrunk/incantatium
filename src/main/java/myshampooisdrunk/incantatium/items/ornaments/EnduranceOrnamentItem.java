package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.EnduranceEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class EnduranceOrnamentItem extends AbstractOrnamentItem{
    public EnduranceOrnamentItem() {
        super(Incantatium.id("endurance_ornament"), "Endurance", 3600);
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public boolean canUse(PlayerEntity p, Hand hand) {
        return super.canUse(p, hand) && p.isSneaking();
    }

    @Override
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable cir) {
        super.use(world, user, hand, cir);
        if(canUse(user, hand)){
            EnduranceEffect e = user.getComponent(Incantatium.ENDURANCE_COMPONENT_KEY);
            e.activate(150);//7.5 sec
        }
    }
}
