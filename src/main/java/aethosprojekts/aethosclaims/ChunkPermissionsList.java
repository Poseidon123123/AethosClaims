package aethosprojekts.aethosclaims;

import java.util.UUID;

public enum ChunkPermissionsList {
    interact(new ChunkPermissionHolder("Interact", UUID.randomUUID())),
    destroy(new ChunkPermissionHolder("Destroy", UUID.randomUUID())),
    place(new ChunkPermissionHolder("Place", UUID.randomUUID()));


    private final ChunkPermissionHolder holder;

    ChunkPermissionsList(ChunkPermissionHolder holder) {
        this.holder = holder;
    }

    public static ChunkPermissionsList getByName(String s) {
        for (ChunkPermissionsList a : ChunkPermissionsList.values()) {
            if (a.getHolder().getName().equalsIgnoreCase(s)) {
                return a;
            }
        }
        return null;
    }

    public ChunkPermissionHolder getHolder() {
        return holder;
    }
}
