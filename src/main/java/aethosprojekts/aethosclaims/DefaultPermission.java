package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.Interfaces.ChunkPermission;
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
