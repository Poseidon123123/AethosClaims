package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.api.ChunkHolder;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class ChunkClaimEvent extends ChunkPlayerEvent {
    private final boolean canClaim;
    private int cost;
    private boolean hasPayed;

    public ChunkClaimEvent(@NotNull ChunkHolder holder, Chunk chunk, int cost, boolean canClaim, boolean hasPayed) {
        super(holder, chunk);
        this.canClaim = canClaim;
        this.cost = cost;
        this.hasPayed = hasPayed;
    }

    public int getCost() {
        return Math.max(cost, 0);
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean canClaim() {
        return canClaim;
    }

    public boolean hasPayed() {
        return hasPayed;
    }

    public void setPayed(boolean hasPayed) {
        this.hasPayed = hasPayed;
    }
}
