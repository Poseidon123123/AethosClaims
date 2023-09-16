package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class ChunkBuyEvent extends ChunkPlayerEvent {
    private final boolean canBuy;
    private int cost;
    private boolean hasPayed;
    private boolean attackable;
    private boolean isFirstChunk;

    public ChunkBuyEvent(@NotNull ChunkHolder holder, Chunk chunk, int cost, boolean canBuy, boolean hasPayed, boolean timeUp, boolean isFirstChunk) {
        super(holder, chunk);
        this.cost = cost;
        this.canBuy = canBuy;
        this.hasPayed = hasPayed;
        this.attackable = timeUp;
        this.isFirstChunk = isFirstChunk;
    }

    public int getCost() {
        return Math.max(cost, 0);
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean canBuy() {
        return canBuy;
    }

    public boolean hasPayed() {
        return hasPayed;
    }

    public void setPayed(boolean hasPayed) {
        this.hasPayed = hasPayed;
    }

    public boolean isNotAttackable() {
        return !attackable;
    }

    public void setTimeUp(boolean timeUp) {
        this.attackable = timeUp;
    }

    public boolean isFirstChunk() {
        return isFirstChunk;
    }

    public void setFirstChunk(boolean firstChunk) {
        isFirstChunk = firstChunk;
    }
}
