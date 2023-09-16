package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.GUI.GUIMap;
import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import aethosprojekts.aethosclaims.events.*;
import net.milkbowl.vault.economy.Economy;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
                "\nAngreifbar: " + mapper.isAttacable(chunk) +
                "\n§8===============";
    }

    public boolean canBuy(Chunk chunk, Player holder, boolean firstChunk) {
        ChunkMapper mapper = claims.getWorldMapper(holder.getWorld());
        PlayerChunkHolder playerChunkHolder = new PlayerChunkHolder(holder.getUniqueId());
        boolean isNearTown = firstChunk;
        if (mapper.boughtChunk(chunk)) {
            holder.sendMessage("§4Du kannst einen gekauften Chunk nicht erneut kaufen");
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
            holder.sendMessage("§4Du kannst diesen Chunk nicht kaufen da er zu weit von deinen gekauften Chunks ist");
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
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) throws NumberFormatException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du musst ein Spieler sein!");
            return true;
        }
        Economy economy = claims.getEconomy();
        ChunkMapper mapper = claims.getWorldMapper(player.getWorld());
        Chunk chunk = player.getChunk();
        PlayerChunkHolder holder = new PlayerChunkHolder(player.getUniqueId());
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("buy")) {
                boolean first = claims.getWorldMapper(player.getWorld()).getChunks(holder).isEmpty();
                boolean attacable = false;
                boolean canBuy = canBuy(chunk, player, first);
                if (!first) {
                    attacable = mapper.isAttacable(chunk);
                }
                ChunkBuyEvent event = new ChunkBuyEvent(holder, player.getChunk(), claims.getConfig().getInt("GS.Buy.Cost"), canBuy, false, attacable, first);
                event.setCancelled(!event.canBuy());
                claims.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (event.canBuy()) {
                        if (event.isNotAttackable() || event.isFirstChunk()) {
                            if (!event.hasPayed()) {
                                EconomyResponse response = economy.withdrawPlayer(player, event.getCost());
                                if (!response.transactionSuccess()) {
                                    return true;
                                } else {
                                    player.sendMessage("§4Du hast nicht genug Geld um diesen Chunk zu kaufen");
                                }
                            }
                            mapper.buyChunk(chunk, holder);
                            player.sendMessage("§aDu hast den Chunk " + chunk.getX() + ", " + chunk.getZ() + " für dich gekauft");
                            if (first) {
                                mapper.claimChunk(chunk, holder);
                                player.sendMessage("§2Da es dein Erster Chunk ist wurde dir das beanspruchen erlassen");
                            }
                        } else {
                            player.sendMessage("§4Die Zeit um dein Gebiet vor dem Kauf anzugreifen ist noch nicht um");
                            return true;
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("claim")) {
                ChunkClaimEvent event = new ChunkClaimEvent(holder, chunk, claims.getConfig().getInt("GS.Claim.Cost"), canClaim(chunk, player), false);
                event.setCancelled(!event.canClaim());
                claims.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (event.canClaim()) {
                        if (!event.hasPayed()) {
                            EconomyResponse response = economy.withdrawPlayer(player, player.getWorld().getName(), event.getCost());
                            if (!response.transactionSuccess()) {
                                return true;
                            } else {
                                player.sendMessage("§4Du hast nicht genug Geld um diesen Chunk zu kaufen");
                            }
                        }
                        mapper.claimChunk(chunk, holder);
                        if (ClaimFighter.getByChunk(chunk) != null) {
                            ClaimFighter fighter = ClaimFighter.getByChunk(chunk);
                            for (ChunkHolder holder1 : fighter.getFighter()) {
                                holder1.sendMessage("$bDie Partei: " + holder.getName() + " hat ebenfalls Anspruch erhoben und kämpft daher mit");
                            }
                            fighter.addFighter(holder);
                            player.sendMessage("§4Um den beanspruchten Chunk wird gerade gekämpft, du kannst dich beteiligen: \n" +
                                    "§3Kampfart: " + (fighter.isKill() ? "§cTötungen" : "§aWirtschaft") + " \n" +
                                    "§3Kämpfer: " + fighter.getFighter().stream().map(ChunkHolder::getName).collect(Collectors.toSet()) + "\n" +
                                    "StartZeitPunkt: " + fighter.getStart().toString() + "\n" +
                                    (fighter.isKill() ? "Deine Tötungen: " : "Dein Gebot: ") + fighter.getKillMoneyCount().get(holder));
                        }
                        player.sendMessage("§2Du hast den Chunk " + chunk.getX() + ", " + chunk.getZ() + " für dich beansprucht");
                        if (mapper.getHolders(chunk).size() > 1) {
                            player.sendMessage("§6Der beanspruchte Chunk wurde auch von anderen Parteien beansprucht nutze /gs startfight um darum zu kämpfen");
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("info")) {
                ChunkInfoEvent event = new ChunkInfoEvent(holder, chunk, getChunkInfoString(chunk));
                claims.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    player.sendMessage(event.getMessage());
                }
            }
            if (args[0].equalsIgnoreCase("map")) {
                ChunkMapEvent event = new ChunkMapEvent(holder, chunk);
                if (!event.isCancelled()) {
                    player.openInventory(new GUIMap("Map", event.getChunk(), holder).getInventory());
                }
            }
            if (args[0].equalsIgnoreCase("fight")) {
                if (ClaimFighter.getByHoler(holder) == null) {
                    player.sendMessage("§4Du bist gerade in keinen Kämpfen beteiligt");
                    return true;
                }
                ClaimFighter fighter = ClaimFighter.getByHoler(holder);
                player.sendMessage("§3Kampfart: " + (fighter.isKill() ? "§cTötungen" : "§aWirtschaft") + " \n" +
                        "§3Kämpfer: " + fighter.getFighter().stream().map(ChunkHolder::getName).collect(Collectors.toSet()) + "\n" +
                        "StartZeitPunkt: " + fighter.getStart().toString() + "\n" +
                        (fighter.isKill() ? "Deine Tötungen: " : "Dein Gebot: ") + fighter.getKillMoneyCount().get(holder));
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
                    if (mapper.getHolders(chunk).contains(holder)) {
                        ChunkSellEvent event = new ChunkSellEvent(holder, chunk, SellType.Claim, claims.getConfig().getInt("GS.Sell.ClaimReturn"));
                        claims.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            mapper.unClaimChunk(chunk, holder);
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
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("fight")) {
                if (args[1].equalsIgnoreCase("start")) {
                    if (!mapper.claimedChunk(chunk) || mapper.getHolders(chunk).size() <= 1) {
                        player.sendMessage("§4Der Chunk muss von mindestens 2 Parteien beansprucht sein, damit man um diesen kämpfen kann");
                        return true;
                    }
                    Set<ChunkHolder> holderSet = mapper.getHolders(chunk);
                    if (!holderSet.contains(holder)) {
                        player.sendMessage("§4Du musst den Chunk beansprucht haben um darum kämpfen zu können");
                        return true;
                    }
                    boolean allOnline = holderSet.stream().allMatch(ChunkHolder::ownerIsOnline);
                    if (!allOnline) {
                        player.sendMessage("§4Alle Parteien die diesen Chunk beanspruchen müssen online sein um den Chunk anzugreifen");
                        return true;
                    }
                    ChunkAttackEvent event = new ChunkAttackEvent(holder, chunk, holderSet, new Random().nextBoolean());
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return true;
                    }
                    ClaimFighter fighter = new ClaimFighter(LocalDateTime.now(), event.getFighter(), event.isKill(), event.getChunk());
                    holderSet.forEach(holder1 -> holder1.sendMessage("§9Der Chunk " + chunk.getZ() + ", " + chunk.getX() + " wurde von " + holder.getName() + " angegriffen. Die Kampfart ist " + (fighter.isKill() ? "Tötungen" : "Wirtschaft") + ". Der Kampf Beginnt jetzt!"));
                }
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("fight")) {
                if (args[1].equalsIgnoreCase("pay")) {
                    ClaimFighter fighter = ClaimFighter.getByHoler(holder);
                    if (fighter.isKill()) {
                        player.sendMessage("§4Du kannst das nur verwenden wenn ihr mit Wirtschaft um den Chunk kämpft");
                        return true;
                    }
                    int money;
                    try {
                        money = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        money = 0;
                        player.sendMessage("§4Es konnte keine Zahl entschlüsselt werden, daher wurde 0 eingesetzt. Schreibe nur Zahlen hin.");
                    }
                    fighter.addScore(holder, money);
                    player.sendMessage("§2Du hast " + money + " zum bieten hinzugefügt");
                }
            }
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("perm")) {
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
            return List.of("buy", "claim", "fight", "info", "map", "perm");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("Perm")) {
                return List.of("add", "remove");
            }
            if (args[0].equalsIgnoreCase("fight")) {
                return List.of("pay", "start");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("Perm")) {
                List<String> a = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    a.add(player.getName());
                }
                return a;
            }
            if (args[0].equalsIgnoreCase("fight") && args[1].equalsIgnoreCase("pay")) {
                return List.of("[Betrag]");
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("Perm")) {
            List<String> a = new ArrayList<>();
            for (DefaultPermission player : DefaultPermission.values()) {
                a.add(player.getName());
            }
            return a;
        }
        return List.of(" ");
    }

}
