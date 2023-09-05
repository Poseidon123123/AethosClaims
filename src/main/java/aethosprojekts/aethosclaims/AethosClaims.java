package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.listner.EconomyListener;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AethosClaims extends JavaPlugin {
    private static AethosClaims instance;
    private final Map<String, ChunkMapper> worldMapper = new HashMap<>();

    public static AethosClaims getInstance() {
        return instance;
    }

    public ChunkMapper getWorldMapper(World world) {
        return worldMapper.get(world.getName());
    }

    @Override
    public void onEnable() {
        instance = this;
        List<World> worlds = getServer().getWorlds();
        List<String> blackList = getConfig().getStringList("Claim.BlacklistWorldsForClaim");
        Set<World> blackWorlds = blackList.stream().map(getServer()::getWorld).collect(Collectors.toSet());
        blackWorlds.forEach(worlds::remove);
        worlds.forEach(world -> worldMapper.put(world.getName(), new ChunkMapper()));
        getServer().getPluginManager().registerEvents(new EconomyListener(), this);
        getServer().getCommandMap().register(this.getName(), new GrundstückCommand(this));
        getConfig().addDefault("GS.BlacklistWorldsForClaim", List.of(" "));
        getConfig().addDefault("GS.Buy.EnemyChunkRange", 5);
        getConfig().addDefault("GS.Buy.NearTown", 1);
        getConfig().addDefault("GS.Claim.NearTown", 10);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {

    }

}
