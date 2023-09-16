package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.listner.GSListner;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AethosClaims extends JavaPlugin {
    public static Economy econ = null;
    private static AethosClaims instance;
    private final Map<String, ChunkMapper> worldMapper = new HashMap<>();
    private Permission perms;
    private Chat chat;


    public static AethosClaims getInstance() {
        return instance;
    }

    public ChunkMapper getWorldMapper(World world) {
        return worldMapper.get(world.getName());
    }

    public List<ChunkMapper> getWorldMappers() {
        return worldMapper.values().stream().toList();
    }

    @Override
    public void onEnable() {
        instance = this;
        List<World> worlds = getServer().getWorlds();
        List<String> blackList = getConfig().getStringList("Claim.BlacklistWorldsForClaim");
        Set<World> blackWorlds = blackList.stream().map(getServer()::getWorld).collect(Collectors.toSet());
        blackWorlds.forEach(worlds::remove);
        worlds.forEach(world -> worldMapper.put(world.getName(), new ChunkMapper()));
        getServer().getPluginManager().registerEvents(new GSListner(), this);
        getServer().getCommandMap().register(this.getName(), new GrundstückCommand(this));
        getConfig().addDefault("GS.BlacklistWorldsForClaim", List.of(" "));
        getConfig().addDefault("GS.Buy.EnemyChunkRange", 5);
        getConfig().addDefault("GS.Buy.NearTown", 1);
        getConfig().addDefault("GS.Claim.NearTown", 10);
        getConfig().addDefault("GS.Buy.Cost", 5000);
        getConfig().addDefault("GS.Claim.Cost", 50);
        getConfig().addDefault("GS.Sell.BuyReturn", 50);
        getConfig().addDefault("GS.Sell.ClaimReturn", 0);
        getConfig().addDefault("GS.FightClaim.Assailable", 24);
        getConfig().addDefault("GS.FightClaim.FightDuration", 6);
        getConfig().options().copyDefaults(true);
        saveConfig();
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
        this.setupPermissions();
        this.setupChat();
    }

    @Override
    public void onDisable() {
        ClaimFighter.StopDuration();
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            @NotNull Chat chat = rsp.getProvider();
        }
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
            @NotNull Permission perms = rsp.getProvider();
        }
    }

    public Economy getEconomy() {
        return econ;
    }

    public Permission getPermissions() {
        return perms;
    }

    public Chat getChat() {
        return chat;
    }

}
