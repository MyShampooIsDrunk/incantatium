package myshampooisdrunk.incantatium.mixin;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;
//
//@Mixin(EntityTrackerEntry.class)
//public class EntityTrackerEntryMixin {
//    @Shadow @Final private Consumer<Packet<?>> watchingSender;
//
//    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 3))
//    private <T> void sendNonexistantShulkerPacket(Consumer<T> instance, T t) {
//        instance.accept(t);
//        if(t instanceof Packet<?> p && p instanceof EntityS2CPacket packet) {
//
//        }
//    }
//}
