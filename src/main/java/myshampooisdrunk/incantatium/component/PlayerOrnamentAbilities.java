package myshampooisdrunk.incantatium.component;

import myshampooisdrunk.drunk_server_toolkit.item.AbstractCustomItem;
import myshampooisdrunk.drunk_server_toolkit.item.CustomItemHelper;
import myshampooisdrunk.incantatium.Incantatium;
import myshampooisdrunk.incantatium.items.ornaments.AbstractOrnamentItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.*;

public class PlayerOrnamentAbilities implements OrnamentAbilities{

    public static final int COOLDOWN = Incantatium.DEV_MODE ? 20 : 600; //600 (30 sec) normally

    //if in offhand, cooldown ticks up until it reaches 2400; @ 2400 the ability activates; when not in offhand, cooldown ticks down until 0; @ 0 it removes it from hashmap

    private final PlayerEntity player;
    private final Map<Identifier, Integer> cooldowns = new HashMap<>();
    private final Set<Identifier> active = new HashSet<>();

    public PlayerOrnamentAbilities(PlayerEntity player){
        this.player = player;
    }

    @Override
    public Map<Identifier, Integer> getCooldowns() {
        return cooldowns;
    }

    @Override
    public Set<Identifier> getActive() {
        return active;
    }

    @Override
    public boolean isActive(Identifier id) {
        return active.contains(id);
    }

    @Override
    public void serverTick() {
        ItemStack stack;
        Identifier id;
        if(!(stack = player.getOffHandStack()).isEmpty() && stack.getDamage() < stack.getMaxDamage()){
            Optional<AbstractCustomItem> opt = CustomItemHelper.getCustomItem(stack);
            if(opt.isPresent()) {
                AbstractCustomItem custom = opt.get();
                if(custom instanceof AbstractOrnamentItem) {
                    id = custom.getIdentifier();
                } else {
                    id = null;
                }
            } else {
                id = null;
            }
        } else {
            id = null;
        }
        boolean bl = false;
        if(id != null && cooldowns.containsKey(id)) {
            int i = cooldowns.get(id);
//            System.out.println("i is " + i);
            if (i < COOLDOWN) {
//                System.out.println("i < cooldown");
                i++;
                bl = true;
                cooldowns.put(id, i);
//                System.out.println(+ " is the new val of i and i has been put");
                if(i == COOLDOWN) {
                    active.add(id);
                    player.sendMessage(Text.literal(id + " was activated").styled(s -> s.withColor(Colors.GREEN)), false);
                }
            }
        } else {
            if(id != null) cooldowns.put(id, 0);
        }
        Set<Identifier> keys = new HashSet<>(cooldowns.keySet());
        keys.forEach(i -> {
            if(!i.equals(id)){
//                System.out.println("i: " + i + " id: " + id);
                int j = cooldowns.get(i);
                if(j == 0) {
                    cooldowns.remove(i);
                    if(active.contains(i)) player.sendMessage(Text.literal(i + " was deactivated").styled(s -> s.withColor((Colors.LIGHT_RED))), false);
                    active.remove(i);
                }
                else cooldowns.put(i, j - 1);
            }
        });

    }

    @Override
    public void readData(ReadView readView) {
        for (ReadView ornament : readView.getListReadView("ornaments")) {
            Identifier id = Identifier.of(ornament.getString("id",""));
            int ticks = ornament.getInt("ticks", 0);
            cooldowns.put(id, ticks);
        }
        String id = readView.getString("active", "");
        if(!id.isEmpty()) active.add(Identifier.of("id"));
    }

    @Override
    public void writeData(WriteView writeView) {
        WriteView.ListView list = writeView.getList("ornaments");
        cooldowns.forEach((id, t) -> {
            WriteView o = list.add();
            o.putString("id", id.toString());
            o.putInt("ticks", t);
        });
        writeView.putString("active", active.isEmpty() ? "" : active.toArray()[0].toString());
    }
}
