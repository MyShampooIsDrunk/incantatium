package myshampooisdrunk.incantatium.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Redirect(method = "onTeam", at=@At(value = "INVOKE",target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    public void noWarn(Logger instance, String s, Object[] objects){
    }
}
