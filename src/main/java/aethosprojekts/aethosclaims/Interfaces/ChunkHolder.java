package aethosprojekts.aethosclaims.Interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface ChunkHolder {
    UUID getUUID();

    Map<UUID, List<ChunkPermission>> getPermissionList();

    void setPermissionList(Map<UUID, List<ChunkPermission>> permissionList);

    default boolean hasPermission(UUID uuid, ChunkPermission permission) {
        return getPermissionList().get(uuid).contains(permission) || uuid.equals(getUUID());
    }

    default void addPermission(UUID uuid, ChunkPermission permission) {
        Map<UUID, List<ChunkPermission>> list = getPermissionList();
        List<ChunkPermission> permissions = getPermissionList().getOrDefault(uuid, new ArrayList<>());
        permissions.add(permission);
        list.put(uuid, permissions);
        setPermissionList(list);
    }

    default void removePermission(UUID uuid, ChunkPermission permission) {
        Map<UUID, List<ChunkPermission>> list = getPermissionList();
        List<ChunkPermission> permissions = getPermissionList().getOrDefault(uuid, new ArrayList<>());
        permissions.remove(permission);
        list.put(uuid, permissions);
        setPermissionList(list);
    }

}
