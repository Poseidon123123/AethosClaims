package aethosprojekts.aethosclaims.events;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import aethosprojekts.aethosclaims.Interfaces.ChunkPermission;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChunkRemovePermEvent extends ChunkPlayerEvent {
    private ChunkPermission permissions;
    private Player permAddPlayer;

    public ChunkRemovePermEvent(@NotNull ChunkHolder holder, Chunk chunk, ChunkPermission permissions, Player permAddPlayer) {
        super(holder, chunk);
        this.permissions = permissions;
        this.permAddPlayer = permAddPlayer;
    }

    public Player getPermAddPlayer() {
        return permAddPlayer;
    }

    public void setPermAddPlayer(Player permAddPlayer) {
        this.permAddPlayer = permAddPlayer;
    }

    public ChunkPermission getPermissions() {
        return permissions;
    }

    public void setPermissions(ChunkPermission permissions) {
        this.permissions = permissions;
    }
}
