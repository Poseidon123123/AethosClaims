package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.api.ChunkHolder;
import aethosprojekts.aethosclaims.api.ChunkPermission;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChunkAddPermEvent extends ChunkPlayerEvent {
    private ChunkPermission permissions;
    private Player permAddPlayer;

    public ChunkAddPermEvent(@NotNull ChunkHolder holder, Chunk chunk, ChunkPermission permissions, Player permAddPlayer) {
        super(holder, chunk);
        this.permissions = permissions;
        this.permAddPlayer = permAddPlayer;
    }

    public ChunkPermission getPermissions() {
        return permissions;
    }

    public void setPermissions(ChunkPermission permissions) {
        this.permissions = permissions;
    }

    public Player getPermAddPlayer() {
        return permAddPlayer;
    }

    public void setPermAddPlayer(Player permAddPlayer) {
        this.permAddPlayer = permAddPlayer;
    }
}
