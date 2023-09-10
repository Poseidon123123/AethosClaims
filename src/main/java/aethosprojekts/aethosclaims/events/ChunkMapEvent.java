package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;


public class ChunkMapEvent extends ChunkPlayerEvent {
    public ChunkMapEvent(@NotNull ChunkHolder holder, Chunk chunk) {
        super(holder, chunk);
    }
}
