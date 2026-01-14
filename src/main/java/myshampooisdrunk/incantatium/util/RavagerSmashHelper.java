package myshampooisdrunk.incantatium.util;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import myshampooisdrunk.incantatium.component.RavagerCooldowns;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

import static myshampooisdrunk.incantatium.Incantatium.RAVAGER_ATTACKS_COMPONENT_KEY;
import static myshampooisdrunk.incantatium.component.RavagerCustomAttacks.*;

public class RavagerSmashHelper {
    public static final double ATTACK_DUR = 20;//duration of the smash attack in ticks
    public static boolean canSmash(RavagerEntity ravager) {
        if(!ravager.hasRaid()) return false;
        RavagerCooldowns ravAtk = ravager.getComponent(RAVAGER_ATTACKS_COMPONENT_KEY);
        LivingEntity target;
        boolean bl = (target = ravager.getTarget()) != null && ravager.getTarget().isAlive();
        if(target == null) return false;
        bl &= ravAtk.getLastSmashAttack() >= SMASH_ATTACK_COOLDOWN;
        double tX = target.getX();
        double tY = target.getY();
        double tZ = target.getY();
        double x = ravager.getX();
        double y = ravager.getY();
        double z = ravager.getY();
        double dx = tX - x;
        double dy = tY - y;
        double dz = tZ - z;
        double horDist = dx * dx + dz * dz;
        double verDist = Math.clamp(dy,0,50);
        bl &= horDist >= SMASH_ATTACK_HORIZONTAL_THRESHOLD * SMASH_ATTACK_HORIZONTAL_THRESHOLD && horDist <
                SMASH_ATTACK_MAX_DISTANCE * SMASH_ATTACK_MAX_DISTANCE && verDist >= SMASH_ATTACK_VERTICAL_THRESHOLD;
        return bl;
    }

    public static void runSmash(RavagerEntity ravager, LivingEntity target){
        if(target == null || ravager.isSubmergedIn(FluidTags.WATER))return;
        RavagerCooldowns ravAtk = ravager.getComponent(RAVAGER_ATTACKS_COMPONENT_KEY);
        double tX = target.getX();
        double tY = target.getY();
        double tZ = target.getZ();
        double x = ravager.getX();
        double y = ravager.getY();
        double z = ravager.getZ();
        double dx = tX-x;
        double dy = tY-y;
        double dz = tZ-z;
        double distSqr = dx*dx+dz*dz+dy*dy;
        double vx = dx * 2d / ATTACK_DUR + target.getVelocity().getX();
        double vz = dz * 2d / ATTACK_DUR + target.getVelocity().getZ();
        if(!ravAtk.hasStartedSmashAttack() && ravAtk.getLastSmashAttack() >= SMASH_ATTACK_COOLDOWN){
//            System.out.println("delta x: " + dx);
//            System.out.println("delta z: " + dz);
//            System.out.println("player velocity: " + target.getVelocity());
            ravager.setVelocity(vx, 2d, vz);
            ravAtk.setStartedSmashAttack(true);
            ravAtk.setLastSmashAttack(0);
        }else if(ravAtk.hasStartedSmashAttack() && ravAtk.getLastSmashAttack() >= ATTACK_DUR){
            if(ravager.isOnGround()){
                runShockwave(ravager, 30-Math.clamp(0.5f * (float)distSqr,0,15));
                ravAtk.setStartedSmashAttack(false);
                ravAtk.setLastSmashAttack(0);
            } else ravager.setVelocity(0,-10,0);
        }
//        else if(ravAtk.hasStartedSmashAttack() && ravAtk.getLastSmashAttack() < 10){
//            ravager.setVelocity(vx, ravager.getVelocity().getY(), vz);
//        } this addition may have made them TOO op
    }
    public static void runShockwave(RavagerEntity ravager, float damage){
//        System.out.println("running a shockwave of " + damage + " damage");

        SoundEvent smash = SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY;
        Box box = ravager.getBoundingBox().expand(6,4,6).offset(0,0,0);
        World world;
        List<Entity> entities = (world = ravager.getEntityWorld()).getOtherEntities(ravager, box, entity -> !(entity instanceof RaiderEntity) && entity instanceof LivingEntity);
        if(world instanceof ServerWorld sWorld){
            sWorld.playSound(null, ravager.getX(), ravager.getY(), ravager.getZ(), smash, ravager.getSoundCategory(), 2.0F, 1.0F);
        }
        Object2IntMap<Pair<Integer,Integer>> surface = new Object2IntOpenHashMap<>();
        for (BlockPos pos : BlockPos.iterate(
                MathHelper.floor(box.minX), MathHelper.floor(box.minY),
                MathHelper.floor(box.minZ), MathHelper.floor(box.maxX),
                MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ)
        )){
            if(world.getBlockState(pos).isSolidBlock(world, pos) && !world.getBlockState(pos.up()).isSolidBlock(world, pos)){
                Pair<Integer, Integer> xz = Pair.of(pos.getX(),pos.getZ());
                if(!surface.containsKey(xz))surface.put(xz,pos.getY());
                else {
                    surface.put(xz,Math.max(surface.getInt(xz),pos.getY()));
                }
            }
        }
        surface.forEach((pair,y) -> {
            int x = pair.left();
            int z = pair.right();
//            System.out.println("particle should be made at " + new Vec3d(x,y,z));
            if(world instanceof ServerWorld sWorld) sWorld.spawnParticles(ParticleTypes.CRIT,
                    x, y, z, 5, 0, 1, 0, 2);
        });
        entities.forEach(e -> {
            if(e instanceof LivingEntity l){
                if(world instanceof ServerWorld sWorld) {
                    sWorld.spawnParticles(ParticleTypes.CRIT,
                            l.getX(), l.getY(), l.getZ(), 5, 0, 1, 0, 2);

                    if(l.isBlocking()) {
                        if(l instanceof PlayerEntity p){
                            ItemStack blockingItem = p.getBlockingItem();
                            BlocksAttacksComponent c;
                            if(blockingItem != null && (c = blockingItem.get(DataComponentTypes.BLOCKS_ATTACKS)) != null) {
                                c.applyShieldCooldown(sWorld, p, 7.5f, blockingItem);
                            }
                            l.velocityModified = true;
                        }
                    } else {
                        l.damage(sWorld, world.getDamageSources().mobAttack(ravager),damage);
                        l.addVelocity(0,1,0);
                        l.velocityModified = true;
                    }
                }
            }
        });
    }
}
