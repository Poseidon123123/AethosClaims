package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.Interfaces.ChunkPermission;

import java.util.UUID;

public class ChunkPermissionHolder implements ChunkPermission {
    private final String name;
    private final UUID uuid;

    public ChunkPermissionHolder(String name, UUID uuid) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }
}
