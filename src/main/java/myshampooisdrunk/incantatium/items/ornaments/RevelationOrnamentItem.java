package myshampooisdrunk.incantatium.items.ornaments;

import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.component.OrnamentAbilities;
import myshampooisdrunk.incantatium.component.PlayerOrnamentAbilities;
import myshampooisdrunk.incantatium.component.Toggle;
import myshampooisdrunk.incantatium.multiblock.recipe.AbstractMultiblockRecipe;
import myshampooisdrunk.incantatium.multiblock.recipe.ShapelessMultiblockRecipe;
import myshampooisdrunk.incantatium.registry.IncantatiumRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
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
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        boolean bl = false;
        if(entity instanceof PlayerEntity p ) {
            OrnamentAbilities abilities = p.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY);
            if(abilities.isActive(identifier)) {
                if(stack == p.getOffHandStack()) {
                    bl = true;
                }
            }
        }

        OrnamentAbilities abilities;

        if(entity instanceof ServerPlayerEntity sp && (abilities = sp.getComponent(Incantatium.ORNAMENT_ABILITIES_COMPONENT_KEY)).isActive(identifier)){
            Toggle toggle = sp.getComponent(Incantatium.TOGGLE_COMPONENT_KEY);
            Team team = new Team(sp.getEntityWorld().getScoreboard(),"aaaaaaaaaaaaaaaaaaaaaaaaaatijkaitjidjhdlhf");
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

        super.inventoryTick(stack, world, entity, slot, ci);
    }

    @Override
    public void use(World world, LivingEntity l, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        super.use(world, l, hand, cir);
        if(l instanceof PlayerEntity user && canUse(user, hand)){
            List<Entity> entities = world.getOtherEntities(user, Box.of(user.getEntityPos(),50,50,50), e -> e instanceof LivingEntity);
            entities.forEach(e -> {
                if (e instanceof LivingEntity l1) l1.setStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 400), user);
            });
            cir.setReturnValue(ActionResult.SUCCESS);
        } else cir.setReturnValue(ActionResult.FAIL);
    }

    @Override
    public AbstractMultiblockRecipe recipe() {
        return new ShapelessMultiblockRecipe(this.create())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.BEACON, 4).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, 16).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addTag(IncantatiumRegistry.FROGLIGHTS, 64).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.END_CRYSTAL, 64).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.TORCHFLOWER, 64).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.SEA_LANTERN, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addTag(ItemTags.CANDLES, 128).build())
                .addIngredient(AbstractMultiblockRecipe.MultiblockEntryIngredient.builder().addItem(Items.SPECTRAL_ARROW, 256).build());
    }

}
