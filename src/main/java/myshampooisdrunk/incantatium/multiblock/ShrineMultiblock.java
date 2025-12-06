package myshampooisdrunk.incantatium.multiblock;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockCoreEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockDisplayEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.MultiblockEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructureType;
import myshampooisdrunk.drunk_server_toolkit.util.BlockUtil;
import myshampooisdrunk.drunk_server_toolkit.world.MultiblockCacheI;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.InventoryStorage;
import myshampooisdrunk.incantatium.multiblock.entity.PedestalEntity;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Predicate;

public class ShrineMultiblock extends MultiblockStructure {
    public static final double RT2 = Math.sqrt(2);
    public static final Vec3d[] offsets = PedestalEntity.offsets(2);

    private final PedestalEntry[] entries;

    public ShrineMultiblock(MultiblockStructureType<ShrineMultiblock> type, MultiblockCoreEntity core) {
        super(type, core);
        entries = core.getEntity().getComponent(Incantatium.INVENTORY_STORAGE_COMPONENT_KEY).getPedestals();
    }

    public Optional<PedestalEntry> getEntry(int i) {
        PedestalEntry ret = null;
        if(i < 8 && i >= 0) {
            ret = entries[i];
        }
//        if(ret == null) Incantatium.LOGGER.info("null lol");
        return Optional.ofNullable(ret);
    }

    @Override
    public boolean spawnStructure(BlockPos pos) {
        boolean ret = super.spawnStructure(pos);
        if(ret) {
            MultiblockDisplayEntity display = new MultiblockDisplayEntity(IncantatiumMultiblockRegistry.ITEM_DISPLAY, world, null);
            display.set(IncantatiumRegistry.SHRINE_DISPLAY_ITEM.create(), null);
            display.getEntity().setTransformation(new AffineTransformation(new Vector3f(0,-2.5f,0), new Quaternionf(), new Vector3f(2,2,2), new Quaternionf()));
            MultiblockEntity.spawnEntity(display, this, pos, new Vec3d(0.5,1.5,0.5));

            for (int i = 0; i < offsets.length; i++) {
                PedestalEntity.PedestalItemEntity item = new PedestalEntity.PedestalItemEntity(IncantatiumMultiblockRegistry.PEDESTAL_ITEM,
                        this.world, i);

                PedestalEntity.PedestalTextEntity text = new PedestalEntity.PedestalTextEntity(IncantatiumMultiblockRegistry.PEDESTAL_TEXT,
                        this.world, i);

                PedestalEntity.PedestalInteractionEntity interaction = new PedestalEntity.PedestalInteractionEntity(IncantatiumMultiblockRegistry.PEDESTAL_INTERACTION,
                        this.world, i);

                MultiblockEntity.spawnEntity(item, this, pos, offsets[i]);
                MultiblockEntity.spawnEntity(text, this, pos, offsets[i]);
                MultiblockEntity.spawnEntity(interaction, this, pos, offsets[i]);

                PedestalEntry entry = new PedestalEntry(item, text, interaction);
                entries[i] = entry;
            }
            core.getEntity().getComponent(Incantatium.INVENTORY_STORAGE_COMPONENT_KEY).copyPedestals(entries);
        }
        return ret;
    }

    public static Template getTemplate() {
        char[][] layer1 = new char[][]{
                "n n n".toCharArray(),
                "  P  ".toCharArray(),
                "nPnPn".toCharArray(),
                "  P  ".toCharArray(),
                "n n n".toCharArray()
        };

        char[][] layer2 = new char[][]{
                "b b b".toCharArray(),
                "  p  ".toCharArray(),
                "bpnpb".toCharArray(),
                "  p  ".toCharArray(),
                "b b b".toCharArray()
        };

        char[][] layer3 = new char[][]{
                "     ".toCharArray(),
                "     ".toCharArray(),
                "  e  ".toCharArray(),
                "     ".toCharArray(),
                "     ".toCharArray()
        };
        TemplateBuilder builder = Template.builder();

        Predicate<BlockState> n = BlockUtil.simpleLookup(Blocks.NETHERITE_BLOCK);
        Predicate<BlockState> b = BlockUtil.simpleLookup(Blocks.BREWING_STAND);
        Predicate<BlockState> p = BlockUtil.simpleLookup(Blocks.PURPUR_STAIRS);
        Predicate<BlockState> P = BlockUtil.simpleLookup(Blocks.PURPUR_PILLAR);

        builder.add(0,-1,0, 'n', n);
        builder.add(0,-2,0, 'n', n);

        builder.add(0,-2,-2, 'n', n);
        builder.add(0,-2,2, 'n', n);
        builder.add(-2,-2,0, 'n', n);
        builder.add(2,-2,0, 'n', n);
        builder.add(-2,-2,-2, 'n', n);
        builder.add(2,-2,2, 'n', n);
        builder.add(2,-2,-2, 'n', n);
        builder.add(-2,-2,2, 'n', n);

        builder.add(-1,-2,0, 'P', P);
        builder.add(1,-2,0, 'P', P);
        builder.add(0,-2,-1, 'P', P);
        builder.add(0,-2,1, 'P', P);

        builder.add(-1,-1,0, 'p', p);
        builder.add(1,-1,0, 'p', p);
        builder.add(0,-1,-1, 'p', p);
        builder.add(0,-1,1, 'p', p);

        builder.add(0,-1,-2, 'b', b);
        builder.add(0,-1,2, 'b', b);
        builder.add(-2,-1,0, 'b', b);
        builder.add(2,-1,0, 'b', b);
        builder.add(-2,-1,-2, 'b', b);
        builder.add(2,-1,2, 'b', b);
        builder.add(2,-1,-2, 'b', b);
        builder.add(-2,-1,2, 'b', b);

        return builder.build();
    }

    public void setEntry(int i, PedestalEntry entry) {
        if(i < 8 && i >= 0) {
//            Incantatium.LOGGER.info("entry has been set!!!");
            entries[i] = entry;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public static class PedestalEntry {
        private final UUID item;
        private final UUID text;
        private final UUID interaction;
        private final World world;

        public PedestalEntry(UUID item, UUID text, UUID interaction, World world) {
            this.item = item;
            this.text = text;
            this.interaction = interaction;
            this.world = world;
        }

        private PedestalEntry(PedestalEntity.PedestalItemEntity item, PedestalEntity.PedestalTextEntity text,
                              PedestalEntity.PedestalInteractionEntity interaction) {
            this.item = item.getUuid();
            this.text = text.getUuid();
            this.interaction = interaction.getUuid();
            this.world = item.getWorld();
        }

        public Optional<PedestalEntity.PedestalItemEntity> item() {
            if((MultiblockEntity<?,?>)((MultiblockCacheI) this.world).drunk_server_toolkit$getMultiblockEntity(item)
                    instanceof PedestalEntity.PedestalItemEntity e) return Optional.of(e);
            return Optional.empty();
        }

        public Optional<PedestalEntity.PedestalTextEntity> text() {
            if((MultiblockEntity<?,?>)((MultiblockCacheI) this.world).drunk_server_toolkit$getMultiblockEntity(text)
                    instanceof PedestalEntity.PedestalTextEntity e) return Optional.of(e);
            return Optional.empty();
        }

        public Optional<PedestalEntity.PedestalInteractionEntity> interaction() {
            if((MultiblockEntity<?,?>)((MultiblockCacheI) this.world).drunk_server_toolkit$getMultiblockEntity(interaction)
                    instanceof PedestalEntity.PedestalInteractionEntity e) return Optional.of(e);
            return Optional.empty();
        }

        public UUID itemUuid() {
            return item;
        }

        public UUID textUuid() {
            return text;
        }

        public UUID interactionUuid() {
            return interaction;
        }
    }
}
