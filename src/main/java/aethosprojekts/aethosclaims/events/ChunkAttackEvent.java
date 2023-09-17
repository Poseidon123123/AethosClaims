package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.api.ChunkHolder;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ChunkAttackEvent extends ChunkPlayerEvent {
    private final Set<ChunkHolder> fighter;
    private boolean isKill;

    public ChunkAttackEvent(@NotNull ChunkHolder holder, Chunk chunk, Set<ChunkHolder> fighter, boolean isKill) {
        super(holder, chunk);
        this.fighter = fighter;
        this.isKill = isKill;
    }

    public Set<ChunkHolder> getFighter() {
        return fighter;
    }

    public void addFighter(ChunkHolder fighter) {
        this.fighter.add(fighter);
    }

    public void removeFighter(ChunkHolder fighter) {
        this.fighter.remove(fighter);
    }

    public boolean isKill() {
        return isKill;
    }

    public void setKill(boolean kill) {
        isKill = kill;
    }
}
