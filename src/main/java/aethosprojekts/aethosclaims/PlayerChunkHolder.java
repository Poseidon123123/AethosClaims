package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import aethosprojekts.aethosclaims.Interfaces.ChunkPermission;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerChunkHolder implements ChunkHolder {
    private final UUID uuid;
    private Map<UUID, Set<ChunkPermission>> permissionList;

    public PlayerChunkHolder(UUID uuid) {
        this(uuid, new HashMap<>());
    }

    public PlayerChunkHolder(UUID uuid, Map<UUID, Set<ChunkPermission>> permissionList) {
        this.uuid = uuid;
        this.permissionList = permissionList;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Map<UUID, Set<ChunkPermission>> getPermissionList() {
        return permissionList != null ? permissionList : new HashMap<>();
    }

    @Override
    public void setPermissionList(Map<UUID, Set<ChunkPermission>> permissionList) {
        this.permissionList = permissionList;
    }

}
