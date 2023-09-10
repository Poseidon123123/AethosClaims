package aethosprojekts.aethosclaims.Interfaces;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ChunkHolder {
    UUID getUUID();

    Map<UUID, Set<ChunkPermission>> getPermissionList();

    void setPermissionList(Map<UUID, Set<ChunkPermission>> permissionList);

    default boolean hasPermission(UUID uuid, ChunkPermission permission) {
        return getPermissionList().computeIfAbsent(uuid, k -> new HashSet<>()).contains(permission) || uuid.equals(getUUID());
    }


    default void addPermission(UUID uuid, ChunkPermission permission) {
        getPermissionList().computeIfAbsent(uuid, k -> new HashSet<>()).add(permission);

    }

    default void removePermission(UUID uuid, ChunkPermission permission) {
        getPermissionList().computeIfAbsent(uuid, k -> new HashSet<>()).remove(permission);
    }

}
