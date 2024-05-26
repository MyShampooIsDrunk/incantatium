package myshampooisdrunk.incanatium.items;

import myshampooisdrunk.drunk_server_toolkit.cooldown.CustomItemCooldownManagerI;
import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.incanatium.Incantatium;
import myshampooisdrunk.incanatium.server.ServerChunkTickManager;
import myshampooisdrunk.incanatium.server.ServerChunkTickManagerInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

public class TimeStopItem extends AbstractCustomItem {
    public TimeStopItem() {
        super(Items.NETHERITE_SWORD, new Identifier(Incantatium.LOGGER.getName(), "time_stop_sword"),"salvarisweapons.time_stop_sword");
    }

    @Override
    public void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable cir) {
        if (!world.isClient) {
            MinecraftServer server = world.getServer();
            ServerChunkTickManager man = ((ServerChunkTickManagerInterface) Objects.requireNonNull(server)).getServerChunkTickManager();
            ServerChunkTickManager.ChunkTickManager chunkMan = man.addChunk(world.getWorldChunk(user.getBlockPos()));
            if (!chunkMan.isFrozen()) {
                if(!((CustomItemCooldownManagerI)user).getCustomItemCooldownManager().isCoolingDown(this)){
                    chunkMan.exempt(user);
                    chunkMan.freezeFor(200);
                    ((CustomItemCooldownManagerI)user).getCustomItemCooldownManager().set(this,1200);
                }else{
                    user.sendMessage(Text.translatable("salvarisweapons.time_stop_sword.cooldown_message",
                            (int)(0.95+((CustomItemCooldownManagerI) user).getCustomItemCooldownManager().getCooldownProgress(this,0)*60f)), true);
                }
            }
        }
        world.playSound(user,user.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS,1f,1.5f);
    }

    private static int rgbToInt(int[] rgb){
        int ret = 0;
        for(int c :rgb) {
            ret = (ret << 8) + c;
        }
        return ret;
    }
    @Override
    public ItemStack create(){
        ItemStack ret = super.create();//.withColor(rgbToInt(new int[]{0,127,128}))
        MutableText loreString = Text.translatable("salvarisweapons.time_sword.lore").setStyle(Style.EMPTY.withBold(true).withItalic(false));
        NbtCompound itemNbt = ret.getSubNbt(ItemStack.DISPLAY_KEY);
        NbtList lore = new NbtList();
//        if (itemNbt.contains(ItemStack.LORE_KEY)) {
//            lore = itemNbt.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);
//        }

        lore.add(NbtString.of(Text.Serialization.toJsonString(loreString)));
        assert itemNbt != null;
        itemNbt.put(ItemStack.LORE_KEY,lore);
        ret.setSubNbt(ItemStack.DISPLAY_KEY,itemNbt);
//        itemNbt.put(ItemStack.LORE_KEY, lore);
//        ret.setNbt(itemNbt);
        Incantatium.LOGGER.warn(ret.getNbt().toString());
        return ret;
    }
}
//{CustomModelData:1,Damage:0,
// display:{Lore:["translation{key='salvarisweapons.time_sword_lore', args=[]}[style={bold}]"],
// Name:'{"translate":"salvarisweapons.time_stop_sword","italic":false}'}}