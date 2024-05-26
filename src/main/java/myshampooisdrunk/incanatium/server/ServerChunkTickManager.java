package myshampooisdrunk.incanatium.server;

import myshampooisdrunk.incanatium.util.TickHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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
    public ChunkTickManager addChunk(WorldChunk c){
        ChunkTickManager man = new ChunkTickManager(server ,c);
        //man.addChunks(c);
        chunks.add(man);
        return man;
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
    public static class ChunkTickManager extends TickManager{
        private Map<String, Pair<Vec3d, Vec2f>> playerCache;//map between UUID and Pos/Rotation
        private Map<String,PlayerEntity> playerPacketCache;
        private int frozenFor = -1;
        public static final float MIN_TICK_RATE = 1.0F;
        private float tickRate = 20.0F;
        private long nanosPerTick;
        private int stepTicks;
        private boolean shouldTick;
        private boolean frozen;
        private final WorldChunk chunk;
        private final MinecraftServer server;
        private final Set<Entity> exempt;
        private ChunkTickManager(MinecraftServer server, WorldChunk chunk){
            this.server=server;
            this.chunk = chunk;
            exempt = new HashSet<>();
            playerCache = new HashMap<>();
            playerPacketCache = new HashMap<>();
        }
        public void setTickRate(float tickRate) {
            this.tickRate = Math.max(tickRate, 1.0F);
            this.nanosPerTick = (long)((double)TimeHelper.SECOND_IN_NANOS / (double)this.tickRate);
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
            playerCache.put(p.getUuidAsString(),new Pair<>(p.getPos(),new Vec2f(p.getYaw(),p.getPitch())));
        }
        public void untrackPlayer(PlayerEntity p){
            playerPacketCache.remove(p.getUuidAsString());
            playerCache.remove(p.getUuidAsString());
        }
        public Map<String,Pair<Vec3d,Vec2f>> getPlayerCache(){
            return playerCache;
        }
        public WorldChunk getChunk(){return chunk;}

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
            frozenFor = ticks;
            setFrozen(true);
        }
        public int getFrozenTicks(){
            return frozenFor;
        }
        public void tick(){
            if(isFrozen() && frozenFor > 0){
                frozenFor--;
            }
            if(isFrozen() && frozenFor == 0){
                setFrozen(false);
            }
            if(isFrozen()) {
                playerPacketCache.forEach((key, player) -> {
                    if(player.getWorld().getWorldChunk(player.getBlockPos()).equals(chunk) && player instanceof ServerPlayerEntity p && !exempt.contains(player)){
                        p.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(p.getId(),Vec3d.ZERO));
                    }
//                    player.slowMovement(
//                            player.getWorld().getBlockState(player.getBlockPos()),
//                            Vec3d.ZERO
//                    );
                    for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
                        if(TickHelper.shouldTick(player,player.getWorld().getWorldChunk(player.getBlockPos())))
                            serverPlayerEntity.networkHandler.sendPacket(new UpdateTickRateS2CPacket(20, true));
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
                                if(!player.getWorld().getWorldChunk(player.getBlockPos()).equals(chunk))
                                    player.networkHandler.sendPacket(UpdateTickRateS2CPacket.create(this.server.getTickManager()));
                                    //player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(),Vec3d.ZERO));
                            }

                        }
                );
            }

            if(frozenFor == 0) {
                playerPacketCache.forEach((key, player) -> {
                    player.slowMovement(
                            player.getWorld().getBlockState(player.getBlockPos()),
                            new Vec3d(1d,1d,1d)
                    );
                    for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
                        if(TickHelper.shouldTick(player,player.getWorld().getWorldChunk(player.getBlockPos())))
                            serverPlayerEntity.networkHandler.sendPacket(UpdateTickRateS2CPacket.create(this.server.getTickManager()));
                    }
//                    this.server.getPlayerManager().sendToAround(
//                            player, player.getX(), player.getY(), player.getZ(), 0,
//                            player.getWorld().getRegistryKey(),
//                            new UpdateTickRateS2CPacket(0, false)
//                    );
                });
                frozenFor = -1;
                playerPacketCache = new HashMap<>();
                playerCache = new HashMap<>();
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
