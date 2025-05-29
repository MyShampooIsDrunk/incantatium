package myshampooisdrunk.incantatium.multiblock.entity;

import myshampooisdrunk.drunk_server_toolkit.multiblock.entity.AbstractMultiblockStructureEntity;
import myshampooisdrunk.drunk_server_toolkit.multiblock.structure.MultiblockStructure;
import myshampooisdrunk.incantatium.util.BoxPolyhedron;
import myshampooisdrunk.incantatium.util.BoxPolyhedronDecomposer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

public record SolidHitboxGenerator(Set<Box> hitboxes, Map<Box, BlockState> blocks){
    public Map<Vec3d, List<AbstractHitboxEntity<?>>> createAndGetEntities(Vec3d relative) {
        Map<Vec3d, List<AbstractHitboxEntity<?>>> map = new HashMap<>();
        BoxPolyhedronDecomposer decomposer = BoxPolyhedronDecomposer.decomposer(hitboxes);
        Map<Vec3d, Double> ideal = decomposer.decompose();
        ideal.forEach((v, d) -> {
            SolidHitboxEntity entity = new SolidHitboxEntity(d.floatValue());
            List<AbstractHitboxEntity<?>> ents;
            Vec3d v2 = v.subtract(0.5,1-d/2,0.5).add(relative);
            if(!map.containsKey(v2)) ents = new ArrayList<>();
            else ents = map.get(v2);
            ents.add(entity);
            map.put(v2, ents);
        });

        HashMap<BlockState,Set<Box>> stateBoxes = new HashMap<>();
        for (Box box : blocks.keySet()) {
            BlockState state;
            if(!stateBoxes.containsKey(state = blocks.get(box)))
                stateBoxes.put(state, new HashSet<>());
            Set<Box> b = new HashSet<>();
            b.add(box);
            b.addAll(stateBoxes.get(state));
            stateBoxes.replace(state, b);
        }

        stateBoxes.forEach((s, bs) -> {
            Map<Vec3d, Double> i = BoxPolyhedronDecomposer.decomposer(bs).decompose();
            i.forEach((v, d) -> {
                Vec3d v2 = v.subtract(0.5).add(relative);
                List<AbstractHitboxEntity<?>> ents;
                if(!map.containsKey(v2)) ents = new ArrayList<>();
                else ents = map.get(v2);
                BlockHitboxEntity e1 = new BlockHitboxEntity(s, d.floatValue());
                SolidHitboxEntity e2 = new SolidHitboxEntity(d.floatValue());
                ents.add(e1);
                map.put(v2,ents);

                Vec3d v3 = v.subtract(0.5,1-d/2,0.5).add(relative);
                if(!map.containsKey(v3)) ents = new ArrayList<>();
                else ents = map.get(v3);
                ents.add(e2);
                map.put(v3,ents);
            });
        });

        return map;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static abstract class AbstractHitboxEntity<E extends Entity> extends AbstractMultiblockStructureEntity<E> {
        private final float sizeMultiplier;

        private AbstractHitboxEntity(EntityType<E> type, String id, float sizeMultiplier) {
            super(type, id);
            this.sizeMultiplier = sizeMultiplier;
        }
        private AbstractHitboxEntity(EntityType<E> type, String id) {
            this(type, id, 1);
        }
        public float getSizeMultiplier() {return sizeMultiplier;}
    }

    public static class SolidHitboxEntity extends AbstractHitboxEntity<ShulkerEntity> {
        public SolidHitboxEntity() {
            super(EntityType.SHULKER, "solid_hitbox");
        }

        public SolidHitboxEntity(float sizeMultiplier) {
            super(EntityType.SHULKER, "solid_hitbox", sizeMultiplier);
        }

        @Override
        public void tick(Entity me, CallbackInfo ci) {
            tick(true, me, ci);
        }

        @Override
        public ShulkerEntity create(ServerWorld world, MultiblockStructure structure, BlockPos center, Vec3d relative){
            ShulkerEntity shulker = super.create(world,structure,center,relative);
            shulker.refreshPositionAndAngles(relative.add(center.toCenterPos()),0 ,0);
            shulker.setInvisible(true);
            shulker.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, -1, 0, false, false));
            shulker.getAttributes().getCustomInstance(EntityAttributes.SCALE).setBaseValue(getSizeMultiplier());
            return shulker;
        }
    }

    public static class BlockHitboxEntity extends AbstractHitboxEntity<DisplayEntity.BlockDisplayEntity> {
        private final BlockState state;

        private BlockHitboxEntity(BlockState state) {
            this(state, 1);
        }
        private BlockHitboxEntity(BlockState state, float sizeMultiplier) {
            super(EntityType.BLOCK_DISPLAY, "block_hitbox", sizeMultiplier);
            this.state = state;
        }

        @Override
        public DisplayEntity.BlockDisplayEntity create(ServerWorld world, MultiblockStructure structure, BlockPos center, Vec3d relative){
            DisplayEntity.BlockDisplayEntity entity = super.create(world,structure,center,relative);
            entity.setBlockState(state);
            float f = getSizeMultiplier();
            entity.setTransformation(new AffineTransformation(new Vector3f(), new Quaternionf(), new Vector3f(f,f,f), new Quaternionf()));
            return entity;
        }
    }

    public static class Builder {
        private final Set<Box> hitboxes;
        private final Map<Box, BlockState> blocks;

        private Builder(){
            this.hitboxes = new HashSet<>();
            this.blocks = new HashMap<>();
        }

        public Builder add(Box hitbox) {
            hitboxes.add(hitbox);
            return this;
        }

        public Builder add(Box... hitboxes) {
            this.hitboxes.addAll(List.of(hitboxes));
            return this;
        }

        public Builder add(Box hitbox, BlockState block) {
            blocks.put(hitbox, block);
            return this;
        }

        public SolidHitboxGenerator build() {
            if(hitboxes.isEmpty() && blocks.isEmpty()) throw new RuntimeException("Attempted building SolidHitboxGenerator with no added hitboxes");
            return new SolidHitboxGenerator(hitboxes, blocks);
        }
    }
}
