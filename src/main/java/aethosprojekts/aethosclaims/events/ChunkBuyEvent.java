package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class ChunkBuyEvent extends ChunkPlayerEvent {
    private final boolean canBuy;
    private int cost;
    private boolean hasPayed;

    public ChunkBuyEvent(@NotNull ChunkHolder holder, Chunk chunk, int cost, boolean canBuy, boolean hasPayed) {
        super(holder, chunk);
        this.cost = cost;
        this.canBuy = canBuy;
        this.hasPayed = hasPayed;
    }

    public int getCost() {
        return Math.min(cost, 0);
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

}
