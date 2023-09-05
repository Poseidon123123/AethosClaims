package aethosprojekts.aethosclaims;

import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChunkMapper {
    private final Map<Long, ChunkHolder> buyMap;
    private final Map<Long, Set<ChunkHolder>> claimMap;

    public ChunkMapper() {
        this(new HashMap<>(), new HashMap<>());
    }

    public ChunkMapper(Map<Long, ChunkHolder> buyMap, Map<Long, Set<ChunkHolder>> claimMap) {
        this.buyMap = buyMap;
        this.claimMap = claimMap;
    }

    public Set<Long> getChunks(ChunkHolder holder) {
        return buyMap.entrySet().stream().filter(entry -> entry.getValue().equals(holder)).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public void buyChunk(Chunk chunk, ChunkHolder holder) {
        if (!buyMap.containsKey(chunk.getChunkKey())) {
            buyMap.put(chunk.getChunkKey(), holder);
        }
    }

    public boolean boughtChunk(Chunk chunk) {
        return buyMap.containsKey(chunk.getChunkKey());
    }

    public ChunkHolder getChunkHolder(Chunk chunk) {
        return buyMap.get(chunk.getChunkKey());
    }

    public boolean sellChunk(Chunk chunk) {
        return buyMap.remove(chunk.getChunkKey()) != null;
    }

    public void claimChunk(Chunk chunk, ChunkHolder holder) {
        claimMap.computeIfAbsent(chunk.getChunkKey(), k -> new HashSet<>()).add(holder);
    }

    public boolean claimedChunk(Chunk chunk) {
        return claimMap.containsKey(chunk.getChunkKey());
    }

    public Set<ChunkHolder> getHolders(Chunk chunk) {
        return claimMap.get(chunk.getChunkKey());
    }
}
