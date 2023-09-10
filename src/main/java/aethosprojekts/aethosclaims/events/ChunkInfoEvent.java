package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class ChunkInfoEvent extends ChunkPlayerEvent {
    private String message;

    public ChunkInfoEvent(@NotNull ChunkHolder holder, Chunk chunk, String message) {
        super(holder, chunk);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
