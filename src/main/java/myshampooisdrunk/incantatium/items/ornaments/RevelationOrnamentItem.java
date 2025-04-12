package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.OrnamentAbilities;
import myshampooisdrunk.incantatium.component.PlayerOrnamentAbilities;
import myshampooisdrunk.incantatium.component.Toggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class RevelationOrnamentItem extends AbstractOrnamentItem{
    public RevelationOrnamentItem() {
        super(Incantatium.id("revelation_ornament"), "Revelation", 3600);
    }

    @Override
    protected void getActiveEffects(ItemStack stack, World world, PlayerEntity player) {
        player.removeStatusEffect(StatusEffects.DARKNESS);
        player.removeStatusEffect(StatusEffects.BLINDNESS);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        boolean bl = false;
        if(entity instanceof PlayerEntity p ) {
            OrnamentAbilities abilities = p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            if(abilities.getActive().contains(identifier)) {
                if(stack == p.getOffHandStack()) {
                    bl = true;
                }
            }
        }

        OrnamentAbilities abilities;

        if(entity instanceof ServerPlayerEntity sp && (abilities = sp.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY)).getActive().contains(identifier)){
            Toggle toggle = sp.getComponent(Incantatium.TOGGLE_COMPONENT_KEY);
            Team team = new Team(sp.getScoreboard(),"aaaaaaaaaaaaaaaaaaaaaaaaaatijkaitjidjhdlhf");
            team.setShowFriendlyInvisibles(true);
            List<? extends PlayerEntity> players = world.getPlayers();
            team.getPlayerList().addAll(players.stream().map(PlayerEntity::getNameForScoreboard).toList());
            TeamS2CPacket packet = null;
            if(bl && abilities.getCooldowns().get(identifier) >= PlayerOrnamentAbilities.COOLDOWN) {
                toggle.set(true);
                packet = TeamS2CPacket.updateTeam(team, true);
            } else {
                if(toggle.get()){
                    packet = TeamS2CPacket.updateRemovedTeam(team);
                    toggle.set(false);
                }
            }
            if(packet != null) sp.networkHandler.sendPacket(packet);
        }

        super.inventoryTick(stack, world, entity, slot, selected, ci);
    }

    @Override
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable cir) {
        super.use(world, user, hand, cir);
        if(canUse(user, hand)){
            List<Entity> entities = world.getOtherEntities(user, Box.of(user.getPos(),50,50,50), e -> e instanceof MobEntity);
            entities.forEach(e -> {
                if (e instanceof LivingEntity l) l.setStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 400), user);
            });
        }
    }
}
