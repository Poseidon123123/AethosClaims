package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.api.ChunkHolder;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class ChunkSellEvent extends ChunkPlayerEvent {
    private final SellType type;
    private final int withDraw;

    public ChunkSellEvent(@NotNull ChunkHolder holder, Chunk chunk, SellType type, int withDraw) {
        super(holder, chunk);
        this.type = type;
        this.withDraw = withDraw;
    }

    public SellType getType() {
        return type;
    }

    public int getWithDraw() {
        return withDraw;
    }

}


