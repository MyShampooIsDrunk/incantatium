package myshampooisdrunk.incantatium.component.type;

import com.mojang.serialization.Codec;
import myshampooisdrunk.incantatium.Incantatium;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public class CustomDataComponentRegistry {

    public static final ComponentType<Boolean> WISDOM_EGO_MODE = register(
            "wisdom_ego_mode", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL)
    );

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Incantatium.id(id), ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
    }
}
