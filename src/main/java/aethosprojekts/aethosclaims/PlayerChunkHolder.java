package aethosprojekts.aethosclaims;

import java.util.UUID;

public class PlayerChunkHolder implements ChunkHolder {
    private final UUID uuid;

    public PlayerChunkHolder(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

}
