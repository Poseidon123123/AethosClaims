package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import aethosprojekts.aethosclaims.Interfaces.ChunkPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerChunkHolder implements ChunkHolder {
    private final UUID uuid;
    private Map<UUID, List<ChunkPermission>> permissionList;

    public PlayerChunkHolder(UUID uuid) {
        this(uuid, new HashMap<>());
    }

    public PlayerChunkHolder(UUID uuid, Map<UUID, List<ChunkPermission>> permissionList) {
        this.uuid = uuid;
        this.permissionList = permissionList;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Map<UUID, List<ChunkPermission>> getPermissionList() {
        return permissionList;
    }

    @Override
    public void setPermissionList(Map<UUID, List<ChunkPermission>> permissionList) {
        this.permissionList = permissionList;
    }

}
