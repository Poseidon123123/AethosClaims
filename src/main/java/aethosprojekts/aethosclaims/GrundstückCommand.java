package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.GUI.MapGUI;
import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import aethosprojekts.aethosclaims.UIs.MapUI;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GrundstückCommand extends Command implements PluginIdentifiableCommand {
    private final AethosClaims claims;

    protected GrundstückCommand(AethosClaims claims) {
        super("Grundstück");
        setAliases(List.of("gs"));
        this.claims = claims;
    }

    public static List<Chunk> koordinatenImUmkreis(Chunk mittelpunkt, int radius) {
        List<Chunk> koordinatenListe = new ArrayList<>();

        for (int x = (mittelpunkt.getX() - radius); x <= (mittelpunkt.getX() + radius); x++) {
            for (int y = (mittelpunkt.getZ() - radius); y <= (mittelpunkt.getZ() + radius); y++) {
                koordinatenListe.add(mittelpunkt.getWorld().getChunkAt(x, y));
            }
        }
        koordinatenListe.remove(mittelpunkt);
        return koordinatenListe;
    }

    public static String getChunkInfoString(Chunk chunk) {
        ChunkMapper mapper = AethosClaims.getInstance().getWorldMapper(chunk.getWorld());
        return "§8===============" +
                "\n§6Chunk: " + chunk.getX() + ", " + chunk.getZ() + " " +
                "\nBeansprucht: " + (mapper.claimedChunk(chunk) ? mapper.getHolders(chunk).stream().map(ChunkHolder::getUUID).map(Bukkit::getOfflinePlayer).map(OfflinePlayer::getName).collect(Collectors.toSet()) : "Keine") +
                "\nGekauft: " + (mapper.boughtChunk(chunk) ? Bukkit.getOfflinePlayer((mapper.getChunkHolder(chunk).getUUID())).getName() : "Keiner") +
                "\n§8===============";
    }

    public boolean canBuy(Chunk chunk, Player holder, boolean firstChunk) {
        ChunkMapper mapper = claims.getWorldMapper(holder.getWorld());
        PlayerChunkHolder playerChunkHolder = new PlayerChunkHolder(holder.getUniqueId());
        boolean isNearTown = firstChunk;
        if (mapper.boughtChunk(chunk)) {
            return false;
        }
        if (!mapper.claimedChunk(chunk) && !firstChunk) {
            holder.sendMessage("§4Der Chunk muss vor dem Kaufen beansprucht werden, das geht mit /gs claim");
            return false;
        }

        for (Chunk nearChunk : koordinatenImUmkreis(chunk, claims.getConfig().getInt("GS.Buy.EnemyChunkRange"))) {
            if (mapper.boughtChunk(nearChunk)) {
                if (!mapper.getChunkHolder(nearChunk).getUUID().equals(playerChunkHolder.getUUID())) {
                    holder.sendMessage("§4Du kannst hier keinen Chunk kaufen da es zu nahe an einem feindlichen Chunk ist, für nähre Infos nutze /gs map");
                    return false;
                }
            }
        }

        for (Chunk nearChunk : koordinatenImUmkreis(chunk, claims.getConfig().getInt("GS.Buy.NearTown"))) {
            if (mapper.boughtChunk(nearChunk)) {
                if (mapper.getChunkHolder(nearChunk).getUUID().equals(playerChunkHolder.getUUID())) {
                    isNearTown = true;
                }
            }
        }
        if (!isNearTown) {
            holder.sendMessage("§4Du kannst diesen Chunk nicht beanspruchen da er zu weit von deinen gekauften Chunks ist");
        }
        return isNearTown;
    }

    public boolean canClaim(Chunk chunk, Player holder) {
        ChunkMapper mapper = claims.getWorldMapper(holder.getWorld());
        PlayerChunkHolder playerChunkHolder = new PlayerChunkHolder(holder.getUniqueId());
        boolean isNearTown = false;
        if (mapper.boughtChunk(chunk)) {
            holder.sendMessage("§4Du kannst keinen gekauften Chunk beanspruchen");
            return false;
        }

        for (Chunk nearChunk : koordinatenImUmkreis(chunk, claims.getConfig().getInt("GS.Buy.EnemyChunkRange"))) {
            if (mapper.boughtChunk(nearChunk)) {
                if (!mapper.getChunkHolder(nearChunk).getUUID().equals(playerChunkHolder.getUUID())) {
                    holder.sendMessage("§4Du kannst hier nicht claimen da es zu nahe an einem feindlichen Chunk ist, für nähre Infos nutze /gs map");
                    return false;
                }
            }
        }

        for (Chunk nearChunk : koordinatenImUmkreis(chunk, claims.getConfig().getInt("GS.Claim.NearTown"))) {
            if (mapper.boughtChunk(nearChunk)) {
                if (mapper.getChunkHolder(nearChunk).getUUID().equals(playerChunkHolder.getUUID())) {
                    isNearTown = true;
                }
            }
        }
        if (!isNearTown) {
            holder.sendMessage("§4Du kannst diesen Chunk nicht beanspruchen da er zu weit von deinen gekauften Chunks ist");
        }
        return isNearTown;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du musst ein Spieler sein!");
            return true;
        }
        ChunkMapper mapper = claims.getWorldMapper(player.getWorld());
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("buy")) {
                //TODO Money
                boolean first = claims.getWorldMapper(player.getWorld()).getChunks(new PlayerChunkHolder(player.getUniqueId())).isEmpty();
                System.out.println(first);
                if (canBuy(player.getChunk(), player, first)) {
                    mapper.buyChunk(player.getChunk(), new PlayerChunkHolder(player.getUniqueId()));
                    player.sendMessage("§aDu hast den Chunk " + player.getChunk().getX() + ", " + player.getChunk().getZ() + " für dich gekauft");
                    if (first) {
                        mapper.claimChunk(player.getChunk(), new PlayerChunkHolder(player.getUniqueId()));
                    }
                }
            }
            if (args[0].equalsIgnoreCase("claim")) {
                if (canClaim(player.getChunk(), player)) {
                    mapper.claimChunk(player.getChunk(), new PlayerChunkHolder(player.getUniqueId()));
                    player.sendMessage("§2Du hast den Chunk " + player.getChunk().getX() + ", " + player.getChunk().getZ() + " für dich beansprucht");
                }
            }
            if (args[0].equalsIgnoreCase("info")) {
                player.sendMessage(getChunkInfoString(player.getChunk()));
            }
            if (args[0].equalsIgnoreCase("map")) {
                List<Chunk> chunkList = koordinatenImUmkreis(player.getChunk(), 2);
                new MapUI(player, MapGUI.getMapGUI(player, chunkList));
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("addPerm")) {
                if (mapper.boughtChunk(player.getChunk())) {
                    Player player1 = Bukkit.getPlayerExact(args[1]);
                    ChunkPermissionsList chunkPermissionsList = ChunkPermissionsList.getByName(args[2]);
                    if (chunkPermissionsList != null) {
                        if (player1 != null) {
                            mapper.getChunkHolder(player.getChunk()).addPermission(player1.getUniqueId(), chunkPermissionsList.getHolder());
                            player.sendMessage("§2Du hast dem Spieler " + player1.getName() + " die Permission " + chunkPermissionsList.getHolder().getName() + " gegeben");
                        } else {
                            player.sendMessage("§4Es gibt keinen Spieler mit dem Namen " + args[1] + " auf dem Server");
                        }
                    } else {
                        player.sendMessage("§4Die angegebene Permission exisitert nicht");
                    }
                } else {
                    player.sendMessage("§4Du musst auf einem gekaufen Chunk sein um einem Spieler Rechte zu geben");
                }
            }
            if (args[0].equalsIgnoreCase("removePerm")) {
                if (mapper.boughtChunk(player.getChunk())) {
                    Player player1 = Bukkit.getPlayerExact(args[1]);
                    ChunkPermissionsList chunkPermissionsList = ChunkPermissionsList.getByName(args[2]);
                    if (chunkPermissionsList != null) {
                        if (player1 != null) {
                            mapper.getChunkHolder(player.getChunk()).addPermission(player1.getUniqueId(), chunkPermissionsList.getHolder());
                            player.sendMessage("§2Du hast dem Spieler " + player1.getName() + " die Permission " + chunkPermissionsList.getHolder().getName() + " genommen");
                        } else {
                            player.sendMessage("§4Es gibt keinen Spieler mit dem Namen " + args[1] + " auf dem Server");
                        }
                    } else {
                        player.sendMessage("§4Die angegebene Permission exisitert nicht");
                    }
                } else {
                    player.sendMessage("§4Du musst auf einem gekaufen Chunk sein um einem Spieler Rechte zu nehmen");
                }
            }
        }

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return claims;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length <= 1) {
            return List.of("claim", "buy", "info", "map", "addPerm", "removePerm");
        } else if (args.length == 2) {
            List<String> a = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                a.add(player.getName());
            }
            return a;
        } else {
            List<String> a = new ArrayList<>();
            for (ChunkPermissionsList player : ChunkPermissionsList.values()) {
                a.add(player.getHolder().getName());
            }
            return a;
        }
    }

}
