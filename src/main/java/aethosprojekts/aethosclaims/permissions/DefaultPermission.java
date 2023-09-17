package aethosprojekts.aethosclaims.permissions;

import aethosprojekts.aethosclaims.AethosClaims;
import aethosprojekts.aethosclaims.api.ChunkPermission;
import org.bukkit.NamespacedKey;

public enum DefaultPermission implements ChunkPermission {
    Interact,
    Destroy,
    Place;


    @Override
    public String getName() {
        return name();
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(AethosClaims.getInstance(), name());
    }
}
