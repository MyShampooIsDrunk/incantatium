package myshampooisdrunk.incantatium.server;

import myshampooisdrunk.incantatium.util.TickHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.tick.TickManager;

import java.util.*;

public class ServerChunkTickManager {

    private final Set<ChunkTickManager> chunks;//idea: allow the user to control the tick rate in multiple chunks differently
    private final Set<ChunkTickManager> removeQueue;
    private final MinecraftServer server;
    public ServerChunkTickManager(MinecraftServer server) {
        this.server = server;
        this.chunks = new HashSet<>();
        this.removeQueue = new HashSet<>();
    }
    public ChunkTickManager addChunk(ChunkTickManager manager){
        //man.addChunks(c);
        chunks.add(manager);
        return manager;
    }
    public void queueChunk(ChunkTickManager man){
        removeQueue.add(man);
    }
    public Set<ChunkTickManager> getQueue(){
        return removeQueue;
    }
    public Set<ChunkTickManager> getChunks(){
        return chunks;
    }
    public void untrackChunks(Collection<ChunkTickManager> managers){
        chunks.removeAll(managers);
    }
    public void untrackChunks(String id){
        Set<ChunkTickManager> temp = new HashSet<>();
        chunks.forEach(chunk -> {
            if (chunk.id.equals(id)) temp.add(chunk);
        });
        chunks.removeAll(temp);
    }
    public ChunkTickManager createManager(String id){
        return new ChunkTickManager(server, id);
    }
    public static class ChunkTickManager extends TickManager{
        private final Map<String, Pair<Vec3d, Vec2f>> playerCache;//map between UUID and Pos/Rotation
        private final Map<String,PlayerEntity> playerPacketCache;
        private int frozenFor = -1;
        private int maxFrozenTicks = -1;
        public static final float MIN_TICK_RATE = 1.0F;
        private float tickRate = 20.0F;
        private long nanosPerTick;
        private int stepTicks;
        private boolean shouldTick;
        private boolean frozen;
        private final Set<WorldChunk> chunkSet;
        private final MinecraftServer server;
        private final Set<Entity> exempt;
        private final String id;

        private ChunkTickManager(MinecraftServer server, String id){
            this.id = id;
            this.server = server;
            this.chunkSet = new HashSet<>();
            exempt = new HashSet<>();
            playerCache = new HashMap<>();
            playerPacketCache = new HashMap<>();
        }
        public void setTickRate(float tickRate) {
            this.tickRate = Math.max(tickRate, 1.0F);
            this.nanosPerTick = (long)((double)TimeHelper.SECOND_IN_NANOS / (double)this.tickRate);
        }
        public ChunkTickManager addChunk(WorldChunk chunk){
            chunkSet.add(chunk);
            return this;
        }
        public ChunkTickManager addChunks(WorldChunk... chunks){
            chunkSet.addAll(List.of(chunks));
            return this;
        }
//        public void setChunk(WorldChunk c){
//            chunks.add(c);
//        }
//        public Set<WorldChunk> getChunks(){
//            return chunks;
//        }
        public void exempt(Entity c){
            exempt.add(c);
        }
        public Set<Entity> getExempt(){
            return exempt;
        }
        public void addPlayer(PlayerEntity p){
            playerPacketCache.put(p.getUuidAsString(), p);
            playerCache.put(p.getUuidAsString(),new Pair<>(p.getEntityPos(),new Vec2f(p.getYaw(),p.getPitch())));
        }
        public void untrackPlayer(PlayerEntity p){
            playerPacketCache.remove(p.getUuidAsString());
            playerCache.remove(p.getUuidAsString());
        }
        public Map<String,Pair<Vec3d,Vec2f>> getPlayerCache(){
            return playerCache;
        }
        public Set<WorldChunk> getChunkSet(){return chunkSet;}

        public float getTickRate() {
            return this.tickRate;
        }

        public float getMillisPerTick() {
            return (float)this.nanosPerTick / (float)TimeHelper.MILLI_IN_NANOS;
        }

        public long getNanosPerTick() {
            return this.nanosPerTick;
        }

        public boolean shouldTick() {
            return this.shouldTick;
        }

        public boolean isStepping() {
            return this.stepTicks > 0;
        }

        public void setStepTicks(int stepTicks) {
            this.stepTicks = stepTicks;
        }

        public int getStepTicks() {
            return this.stepTicks;
        }

        public void setFrozen(boolean frozen) {
            //System.out.println("chunk is frozen!!");
            this.frozen = frozen;
        }
        public void freezeFor(int ticks){
            maxFrozenTicks = ticks;
            frozenFor = ticks;
            setFrozen(true);
        }
        public int getFrozenTicks(){
            return frozenFor;
        }
        public void tick() {
//            if(isFrozen()) System.out.println("frozen for: " + frozenFor);
            if(isFrozen() && frozenFor > 0){
                frozenFor--;
            }
            if(isFrozen() && frozenFor == 0){
                maxFrozenTicks = -1;
                setFrozen(false);
            }
            if(isFrozen()) {
                playerPacketCache.forEach((key, player) -> {
                    if((frozenFor + 1)%10 == 0){
                        player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.PLAYERS, 2, -1);
                    }
//                    ServerBossBar bar = new ServerBossBar(Text.literal("FROZEN FOR " + (frozenFor+20)/20 + " MORE SECOND" + (frozenFor > 20 ? "S":"")), BossBar.Color.BLUE, BossBar.Style.NOTCHED_20);
//                    bar.setPercent((float)frozenFor/maxFrozenTicks);
//                    if(player instanceof ServerPlayerEntity p) {
//                        bar.removePlayer(p);
//                        bar.addPlayer(p);
//                    } maybe ill add this later idk
                    if(chunkSet.contains(player.getEntityWorld().getWorldChunk(player.getBlockPos())) && player instanceof ServerPlayerEntity p && !exempt.contains(player)){
                        p.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(p.getId(),Vec3d.ZERO));
                    }
                    player.slowMovement(
                            player.getEntityWorld().getBlockState(player.getBlockPos()),
                            Vec3d.ZERO
                    );
                    for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
                        if(TickHelper.shouldTick(player,player.getEntityWorld().getWorldChunk(player.getBlockPos())))
                            serverPlayerEntity.networkHandler.sendPacket(UpdateTickRateS2CPacket.create(server.getTickManager()));
                        else {
                            serverPlayerEntity.networkHandler.sendPacket(new UpdateTickRateS2CPacket(1, true));
                        }
                    }
//                    this.server.getPlayerManager().sendToAround(
//                            player, player.getX(), player.getY(), player.getZ(), 0,
//                            player.getWorld().getRegistryKey(),
//                            new UpdateTickRateS2CPacket(0, true)
//                    );
                });
                exempt.forEach(
                        entity -> {
                            if(entity instanceof ServerPlayerEntity player){
                                if(!chunkSet.contains(player.getEntityWorld().getWorldChunk(player.getBlockPos())))
                                    player.networkHandler.sendPacket(UpdateTickRateS2CPacket.create(this.server.getTickManager()));
                                    //player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(),Vec3d.ZERO));
                            }

                        }
                );
            }

            if(frozenFor == 0) {
                playerPacketCache.forEach((key, player) -> {
                    player.slowMovement(
                            player.getEntityWorld().getBlockState(player.getBlockPos()),
                            new Vec3d(1d,1d,1d)
                    );
                    for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
                        if(TickHelper.shouldTick(player,player.getEntityWorld().getWorldChunk(player.getBlockPos())))
                            serverPlayerEntity.networkHandler.sendPacket(UpdateTickRateS2CPacket.create(this.server.getTickManager()));
                    }
//                    this.server.getPlayerManager().sendToAround(
//                            player, player.getX(), player.getY(), player.getZ(), 0,
//                            player.getWorld().getRegistryKey(),
//                            new UpdateTickRateS2CPacket(0, false)
//                    );
                });
                frozenFor = -1;
                playerPacketCache.clear();
                playerCache.clear();
                chunkSet.clear();
            }
        }

        public boolean isFrozen() {
            return this.frozen;
        }

        public MinecraftServer getServer(){return this.server;}

        public void step() {
            this.shouldTick = !this.frozen || this.stepTicks > 0;
            if (this.stepTicks > 0) {
                --this.stepTicks;
            }

        }

        public boolean shouldSkipTick(Entity entity) {
            return !this.shouldTick() && !(entity instanceof PlayerEntity) && entity.getPlayerPassengers() <= 0;
        }
    }
}
