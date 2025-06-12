package myshampooisdrunk.incantatium.multiblock.entity;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.incantatium.multiblock.RitualMultiblock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;

public record PedestalEntityGenerator(double radius, RitualMultiblock multiblock) {
    public static class PedestalEntity extends AbstractMultiblockStructureEntity<DisplayEntity.ItemDisplayEntity> {
        public PedestalEntity(String id) {
            super(EntityType.ITEM_DISPLAY, id);
        }
    }

    public static class PedestalEntityText extends AbstractMultiblockStructureEntity<DisplayEntity.TextDisplayEntity> {
        public PedestalEntityText(String id) {
            super(EntityType.TEXT_DISPLAY, id);
        }
    }
}
