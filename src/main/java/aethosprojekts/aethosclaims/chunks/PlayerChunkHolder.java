package aethosprojekts.aethosclaims.chunks;

import aethosprojekts.aethosclaims.AethosClaims;
import aethosprojekts.aethosclaims.api.ChunkHolder;
import aethosprojekts.aethosclaims.api.ChunkPermission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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

    @Override
    public boolean hasPlayer(Player player) {
        Player player1 = Bukkit.getPlayer(uuid);
        return player.equals(player1);
    }

    @Override
    public double getMoney() {
        return AethosClaims.getInstance().getEconomy().getBalance(Bukkit.getOfflinePlayer(getUUID()));
    }

    @Override
    public boolean depositMoney(double money) {
        return AethosClaims.getInstance().getEconomy().depositPlayer(getOfflinePlayer(), money).transactionSuccess();
    }

    @Override
    public boolean withdrawMoney(double money) {
        return AethosClaims.getInstance().getEconomy().withdrawPlayer(getOfflinePlayer(), money).transactionSuccess();
    }

    @Override
    public boolean ownerIsOnline() {
        Player player = Bukkit.getPlayer(uuid);
        return player != null && player.isOnline();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(getUUID());
    }

    @Override
    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public String getName() {
        Player player = Bukkit.getPlayer(uuid);
        return player != null ? player.getName() : "NoName";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerChunkHolder holder) {
            return this.getUUID().equals(holder.getUUID());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
