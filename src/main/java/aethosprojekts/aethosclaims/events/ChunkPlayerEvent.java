package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import org.bukkit.Chunk;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChunkPlayerEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Chunk chunk;
    private final ChunkHolder holder;
    private boolean isCanceled;

    public ChunkPlayerEvent(@NotNull ChunkHolder holder, Chunk chunk) {
        isCanceled = false;
        this.chunk = chunk;
        this.holder = holder;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCanceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCanceled = cancel;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public ChunkHolder getHolder() {
        return holder;
    }
}
