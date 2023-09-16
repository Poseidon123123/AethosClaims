package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import org.bukkit.Chunk;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChunkMapper {
    private final Map<Long, ChunkHolder> buyMap;
    private final Map<Long, Set<ChunkHolder>> claimAttackMap;
    private final Map<Long, ChunkHolder> claimDoneMap;
    private final Map<Long, LocalDateTime> timeClaimMap;

    public ChunkMapper() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public ChunkMapper(Map<Long, ChunkHolder> buyMap, Map<Long, Set<ChunkHolder>> claimMap, Map<Long, LocalDateTime> timeClaimMap, Map<Long, ChunkHolder> claimDoneMap) {
        this.buyMap = buyMap;
        this.claimAttackMap = claimMap;
        this.timeClaimMap = timeClaimMap;
        this.claimDoneMap = claimDoneMap;
    }

    public Set<Long> getChunks(ChunkHolder holder) {
        return buyMap.entrySet().stream().filter(entry -> entry.getValue().getUUID().equals(holder.getUUID())).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public void buyChunk(Chunk chunk, ChunkHolder holder) {
        if (!buyMap.containsKey(chunk.getChunkKey())) {
            buyMap.put(chunk.getChunkKey(), holder);
        }
    }

    public void transferChunk(long chunkKey, ChunkHolder holder) {
        claimAttackMap.remove(chunkKey);
        claimDoneMap.put(chunkKey, holder);
    }

    public boolean boughtChunk(Chunk chunk) {
        return buyMap.containsKey(chunk.getChunkKey());
    }

    public ChunkHolder getChunkHolder(Chunk chunk) {
        return buyMap.get(chunk.getChunkKey());
    }

    public void sellChunk(Chunk chunk) {
        buyMap.remove(chunk.getChunkKey());
    }

    public void claimChunk(Chunk chunk, ChunkHolder holder) {
        claimAttackMap.computeIfAbsent(chunk.getChunkKey(), k -> new HashSet<>()).add(holder);
        timeClaimMap.put(chunk.getChunkKey(), LocalDateTime.now());
    }

    public boolean claimedChunk(Chunk chunk) {
        return claimAttackMap.containsKey(chunk.getChunkKey()) || claimDoneMap.containsKey(chunk.getChunkKey()) || buyMap.containsKey(chunk.getChunkKey());
    }

    //TODO das hier funktioniert nicht
    public boolean isAttacable(Chunk chunk) {
        return !timeUp(chunk) || !claimDoneMap.containsKey(chunk.getChunkKey());
    }


    public void unClaimChunk(Chunk chunk, ChunkHolder holder) {
        unClaimChunk(chunk.getChunkKey(), holder);
    }

    public void unClaimChunk(long chunkKey, ChunkHolder holder) {
        Set<ChunkHolder> holders = claimAttackMap.get(chunkKey);
        holders.remove(holder);
        if (claimAttackMap.get(chunkKey).size() == 1) {
            claimAttackMap.remove(chunkKey);
        } else {
            claimAttackMap.replace(chunkKey, holders);
        }
        claimDoneMap.remove(chunkKey);
    }

    public void unClaimChunk(Chunk chunk) {
        unClaimChunk(chunk.getChunkKey());
    }

    public void unClaimChunk(long chunkKey) {
        claimDoneMap.remove(chunkKey);
        claimAttackMap.remove(chunkKey);
    }

    public Set<ChunkHolder> getHolders(Chunk chunk) {
        if (claimAttackMap.containsKey(chunk.getChunkKey())) {
            return claimAttackMap.get(chunk.getChunkKey());
        } else {
            return Set.of(claimDoneMap.get(chunk.getChunkKey()));
        }
    }


    public boolean timeUp(Chunk chunk) {
        LocalDateTime start = timeClaimMap.get(chunk.getChunkKey());
        if (start == null) {
            return true;
        }
        System.out.println(start);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(start, now);
        long difference = duration.toHours();
        int time = AethosClaims.getInstance().getConfig().getInt("GS.FightClaim.Assailable");
        return difference >= time;
    }

    public Map<Long, ChunkHolder> getBuyMap() {
        return buyMap;
    }
}
