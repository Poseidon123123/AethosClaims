package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.GUI.GUIMap;
import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import aethosprojekts.aethosclaims.events.*;
import net.milkbowl.vault.economy.EconomyResponse;
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
        Chunk chunk = player.getChunk();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("buy")) {
                PlayerChunkHolder holder = new PlayerChunkHolder(player.getUniqueId());
                boolean first = claims.getWorldMapper(player.getWorld()).getChunks(holder).isEmpty();
                ChunkBuyEvent event = new ChunkBuyEvent(holder, player.getChunk(), claims.getConfig().getInt("GS.Buy.Cost"), canBuy(chunk, player, first), false);
                event.setCancelled(!event.canBuy());
                claims.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (event.canBuy()) {
                        if (!event.hasPayed()) {
                            EconomyResponse response = Provider.getEconomy().withdrawPlayer(player, player.getWorld().getName(), event.getCost());
                            if (!response.transactionSuccess()) {
                                return true;
                            }
                        }
                        mapper.buyChunk(chunk, holder);
                        player.sendMessage("§aDu hast den Chunk " + chunk.getX() + ", " + chunk.getZ() + " für dich gekauft");
                        if (first) {
                            mapper.claimChunk(chunk, holder);
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("claim")) {
                PlayerChunkHolder holder = new PlayerChunkHolder(player.getUniqueId());
                ChunkClaimEvent event = new ChunkClaimEvent(holder, chunk, claims.getConfig().getInt("GS.Claim.Cost"), canClaim(chunk, player), false);
                event.setCancelled(!event.canClaim());
                claims.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (event.canClaim()) {
                        if (!event.hasPayed()) {
                            EconomyResponse response = Provider.getEconomy().withdrawPlayer(player, player.getWorld().getName(), event.getCost());
                            if (!response.transactionSuccess()) {
                                return true;
                            }
                        }
                        mapper.claimChunk(chunk, holder);
                        player.sendMessage("§2Du hast den Chunk " + chunk.getX() + ", " + chunk.getZ() + " für dich beansprucht");
                    }
                }
            }
            if (args[0].equalsIgnoreCase("info")) {
                ChunkInfoEvent event = new ChunkInfoEvent(new PlayerChunkHolder(player.getUniqueId()), chunk, getChunkInfoString(chunk));
                claims.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    player.sendMessage(event.getMessage());
                }
            }
            if (args[0].equalsIgnoreCase("map")) {
                ChunkMapEvent event = new ChunkMapEvent(new PlayerChunkHolder(player.getUniqueId()), chunk);
                if (!event.isCancelled()) {
                    player.openInventory(new GUIMap("Map", event.getChunk(), new PlayerChunkHolder(player.getUniqueId())).getInventory());
                }
            }
            if (args[0].equalsIgnoreCase("sell")) {
                if (mapper.boughtChunk(chunk)) {
                    if (!mapper.getChunkHolder(chunk).getUUID().equals(player.getUniqueId())) {
                        player.sendMessage("§4Du der Besitzer dieses Chunks sein um ihn zu verkaufen");
                        return true;
                    }
                    ChunkSellEvent event = new ChunkSellEvent(new PlayerChunkHolder(player.getUniqueId()), chunk, SellType.Buy, claims.getConfig().getInt("GS.Sell.BuyReturn"));
                    claims.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        mapper.sellChunk(chunk);
                        mapper.unClaimChunk(chunk);
                        Provider.getEconomy().depositPlayer(player, event.getWithDraw());
                        player.sendMessage("§2Du hast diesen Chunk verkauft");
                    }
                } else if (mapper.claimedChunk(chunk)) {
                    if (mapper.getHolders(chunk).contains(new PlayerChunkHolder(player.getUniqueId()))) {
                        ChunkSellEvent event = new ChunkSellEvent(new PlayerChunkHolder(player.getUniqueId()), chunk, SellType.Claim, claims.getConfig().getInt("GS.Sell.ClaimReturn"));
                        claims.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            mapper.unClaimChunk(chunk);
                            Provider.getEconomy().depositPlayer(player, event.getWithDraw());
                            player.sendMessage("§2Du hast diesen Chunk freigegeben");
                        }
                    } else {
                        player.sendMessage("§4Du musst diesen Chunk beansprucht haben um ihn zu verkaufen");
                    }
                } else {
                    ChunkSellEvent event = new ChunkSellEvent(new PlayerChunkHolder(player.getUniqueId()), chunk, SellType.Nonne, 0);
                    if (!event.isCancelled()) {
                        player.sendMessage("§4Du kannst keinen unbeanspruchten Chunk verkaufen");
                    }
                }
            }
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("Perm")) {
                Player player1 = Bukkit.getPlayerExact(args[2]);
                if (player1 == null) {
                    player.sendMessage("§4Es gibt keinen Spieler mit dem Namen " + args[2] + " auf dem Server");
                    return true;
                }
                DefaultPermission defaultPermission = DefaultPermission.valueOf(args[3]);
                if (args[1].equalsIgnoreCase("add")) {
                    if (mapper.boughtChunk(player.getChunk())) {
                        ChunkAddPermEvent event = new ChunkAddPermEvent(new PlayerChunkHolder(player.getUniqueId()), chunk, defaultPermission, player1);
                        claims.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            mapper.getChunkHolder(player.getChunk()).addPermission(event.getPermAddPlayer().getUniqueId(), event.getPermissions());
                            player.sendMessage("§2Du hast dem Spieler " + event.getPermAddPlayer().getName() + " die Permission " + event.getPermissions().getName() + " gegeben");
                        }
                    } else {
                        player.sendMessage("§4Du musst auf einem gekauften Chunk sein um einem Spieler Rechte zu geben");
                    }
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    if (mapper.boughtChunk(player.getChunk())) {
                        ChunkRemovePermEvent event = new ChunkRemovePermEvent(new PlayerChunkHolder(player.getUniqueId()), chunk, defaultPermission, player1);
                        if (!event.isCancelled()) {
                            mapper.getChunkHolder(player.getChunk()).removePermission(event.getPermAddPlayer().getUniqueId(), event.getPermissions());
                            player.sendMessage("§2Du hast dem Spieler " + event.getPermAddPlayer().getName() + " die Permission " + event.getPermissions().getName() + " genommen");
                        }
                    } else {
                        player.sendMessage("§4Du musst auf einem gekauften Chunk sein um einem Spieler Rechte zu nehmen");
                    }
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
            return List.of("claim", "buy", "info", "map", "Perm");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("Perm")) {
            return List.of("add", "remove");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("Perm")) {
            List<String> a = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                a.add(player.getName());
            }
            return a;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("Perm")) {
            List<String> a = new ArrayList<>();
            for (DefaultPermission player : DefaultPermission.values()) {
                a.add(player.getName());
            }
            return a;
        } else {
            return List.of(" ");
        }
    }

}
