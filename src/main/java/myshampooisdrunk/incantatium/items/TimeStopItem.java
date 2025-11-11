package myshampooisdrunk.incantatium.items;

import myshampooisdrunk.drunk_server_toolkit.cooldown.CustomItemCooldownManagerI;
import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.server.ServerChunkTickManager;
import myshampooisdrunk.incantatium.server.ServerChunkTickManagerInterface;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

public class TimeStopItem extends AbstractCustomItem {

    public static final int ABILITY_COOLDOWN_TICKS = 1400;//70 sec (1 min + 10 sec while its being used)

    public TimeStopItem() {
        super(Items.NETHERITE_SWORD, Incantatium.id("time_stop_sword"),"incantatium.time_sword.name", Incantatium.getModel(Incantatium.id("time_sword")));
        String s = "Forgotten Deity's Greatsword";
        int spaces = 0;
        int[][] nums = new int[][] {
                {0,78,194}, {16,77,193}, {27,75,192}, {41,72,192}, {47,70,191}, {52,69,190}, {57,67,189}, {61,65,188},
                {65,63,187}, {69,61,186}, {73,59,185}, {77,57,184}, {80,55,183}, {83,53,182}, {87,51,180}, {90,48,179},
                {93,46,178}, {96,43,176}, {99,40,174}, {101,37,173}, {104,33,171}, {107,30,169}, {109,25,168}, {112,20,166},
                {114,14,164}, {113,14,164}
        };
        MutableText name = Text.literal("");
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == ' '){
                name.append(" ");
                spaces++;
            }else name.append(Text.literal(String.valueOf(s.charAt(i))).withColor(rgbToInt(nums[i-spaces])));
        }
        addComponent(DataComponentTypes.CUSTOM_NAME, name.setStyle(Style.EMPTY.withItalic(false)));

        List<Text> lore = List.of(
                Text.literal("RIGHT CLICK").setStyle(Style.EMPTY.withBold(true).withItalic(false).withColor(rgbToInt(new int[]{152,30,16})))
                        .append(Text.literal(" stops the flow of time around you for 10 seconds")
                                .setStyle(Style.EMPTY.withBold(true).withItalic(true).withColor(rgbToInt(new int[]{106,15,186})))),
                Text.literal(""),
                Text.literal("Gives its wielder the power to control time")
                        .setStyle(Style.EMPTY.withBold(true).withItalic(true).withColor(rgbToInt(new int[]{106,15,186}))),
                Text.literal("Has the power to bring about the ")
                        .setStyle(Style.EMPTY.withBold(true).withItalic(true).withColor(rgbToInt(new int[]{106,15,186})))
                        .append(Text.literal("AAAAAAAAA")
                                .setStyle(Style.EMPTY.withItalic(false).withBold(true).withObfuscated(true).withColor(Colors.RED)))
        );
        addComponent(DataComponentTypes.LORE, new LoreComponent(lore));
        addComponent(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);
    }

    @Override
    public void use(World world, LivingEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if(world.isClient()) return;

        if(user instanceof PlayerEntity player) {
            if(!((CustomItemCooldownManagerI)user).drunk_server_toolkit$getCustomItemCooldownManager().isCoolingDown("time_stop")){
                world.playSound(null,user.getBlockPos().up(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS,1f,1.5f);
                MinecraftServer server = world.getServer();
                ServerChunkTickManager man = ((ServerChunkTickManagerInterface) Objects.requireNonNull(server)).getServerChunkTickManager();
                ServerChunkTickManager.ChunkTickManager chunkMan = man.createManager("time_stop");
                for(int dx = -1; dx <=1; dx++){
                    for(int dz = -1; dz <=1; dz++){
                        chunkMan = chunkMan.addChunk(world.getWorldChunk(user.getBlockPos().add(dx * 16, 0, dz * 16)));
                    }
                }
                man.addChunk(chunkMan);
                chunkMan.exempt(user);
                chunkMan.freezeFor(200);
                ((CustomItemCooldownManagerI)user).drunk_server_toolkit$getCustomItemCooldownManager().set("time_stop",ABILITY_COOLDOWN_TICKS);
                player.getItemCooldownManager().set(user.getStackInHand(hand), ABILITY_COOLDOWN_TICKS);
            }else{
                player.sendMessage(Text.literal(String.format("There are %s second(s) left until you may use this item again",
                                (int)(0.95+((CustomItemCooldownManagerI) user).drunk_server_toolkit$getCustomItemCooldownManager()
                                        .getCooldownProgress("time_stop",0)*(ABILITY_COOLDOWN_TICKS/20f))))
                        .setStyle(Style.EMPTY.withBold(true).withColor(Colors.LIGHT_RED).withItalic(false)), true);
            }
        }

    }

    public static int rgbToInt(int[] rgb){
        int ret = 0;
        for(int c :rgb) {
            ret = (ret << 8) + c;
        }
        return ret;
    }

    @Override
    public void onSneak(boolean sneaking, PlayerEntity player, CallbackInfo ci){
        if(sneaking){
            if(((CustomItemCooldownManagerI)player).drunk_server_toolkit$getCustomItemCooldownManager().isCoolingDown("time_stop")){
                player.sendMessage(Text.literal(String.format("There are %s second(s) left until you may use this item again",
                                (int)(0.95+((CustomItemCooldownManagerI) player).drunk_server_toolkit$getCustomItemCooldownManager()
                                        .getCooldownProgress("time_stop",0)*(ABILITY_COOLDOWN_TICKS/20f))))
                        .setStyle(Style.EMPTY.withBold(true).withColor(Colors.LIGHT_RED).withItalic(false)), true);
            }else {
                player.sendMessage(
                        Text.literal("Time Stop ability is ").setStyle(
                                Style.EMPTY.withItalic(false).withColor(rgbToInt(new int[]{51,170,51}))
                        ).append(Text.literal("READY").setStyle(
                                Style.EMPTY.withItalic(false).withColor(rgbToInt(new int[]{0,136,0})).withBold(true)
                        )), true
                );
            }
        }
    }
}