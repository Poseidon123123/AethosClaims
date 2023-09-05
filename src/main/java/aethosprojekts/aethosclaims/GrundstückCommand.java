package aethosprojekts.aethosclaims;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

        return koordinatenListe;
    }

    public boolean canBuy(Chunk chunk, Player holder, boolean firstChunk) {
        ChunkMapper mapper = claims.getWorldMapper(holder.getWorld());
        PlayerChunkHolder playerChunkHolder = new PlayerChunkHolder(holder.getUniqueId());
        boolean isNearTown = firstChunk;
        if (mapper.boughtChunk(chunk)) {
            return false;
        }

        for (Chunk nearChunk : koordinatenImUmkreis(chunk, claims.getConfig().getInt("GS.Buy.EnemyChunkRange"))) {
            if (mapper.boughtChunk(nearChunk)) {
                if (!mapper.getChunkHolder(nearChunk).equals(playerChunkHolder)) {
                    return false;
                }
            }
        }

        for (Chunk nearChunk : koordinatenImUmkreis(chunk, claims.getConfig().getInt("GS.Buy.NearTown"))) {
            if (mapper.boughtChunk(nearChunk)) {
                if (mapper.getChunkHolder(nearChunk).equals(playerChunkHolder)) {
                    isNearTown = true;
                }
            }
        }
        return isNearTown;
    }

    public boolean canClaim(Chunk chunk, Player holder) {
        ChunkMapper mapper = claims.getWorldMapper(holder.getWorld());
        PlayerChunkHolder playerChunkHolder = new PlayerChunkHolder(holder.getUniqueId());
        boolean isNearTown = false;
        if (mapper.boughtChunk(chunk)) {
            return false;
        }

        for (Chunk nearChunk : koordinatenImUmkreis(chunk, claims.getConfig().getInt("GS.Buy.EnemyChunkRange"))) {
            if (mapper.boughtChunk(nearChunk)) {
                if (!mapper.getChunkHolder(nearChunk).equals(playerChunkHolder)) {
                    return false;
                }
            }
        }

        for (Chunk nearChunk : koordinatenImUmkreis(chunk, claims.getConfig().getInt("GS.Claim.NearTown"))) {
            if (mapper.boughtChunk(nearChunk)) {
                if (mapper.getChunkHolder(nearChunk).equals(playerChunkHolder)) {
                    isNearTown = true;
                }
            }
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
                if (mapper.claimedChunk(player.getChunk()) && mapper.getHolders(player.getChunk()).size() == 1 && mapper.getHolders(player.getChunk()).contains(new PlayerChunkHolder(player.getUniqueId()))) {
                    if (canBuy(player.getChunk(), player, claims.getWorldMapper(player.getWorld()).getChunks(new PlayerChunkHolder(player.getUniqueId())) == null)) {
                        claims.getWorldMapper(player.getWorld()).buyChunk(player.getChunk(), new PlayerChunkHolder(player.getUniqueId()));
                        player.sendMessage("§aDu hast den Chunk " + player.getChunk().getX() + ", " + player.getChunk().getZ() + " für dich gekauft");
                    } else {
                        player.sendMessage("§4Du kannst den Chunk nicht kaufen, da der Chunk entweder zu nahe an einem anderen Chunk ist oder dieser zu weit weg von einem deiner ist ");
                    }
                } else {
                    player.sendMessage("§4Der Chunk muss vor dem Kaufen beansprucht werden, das geht mit /claim");
                }
            }
            if (args[0].equalsIgnoreCase("claim")) {
                if (canClaim(player.getChunk(), player)) {
                    mapper.claimChunk(player.getChunk(), new PlayerChunkHolder(player.getUniqueId()));
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
        return List.of("claim");
    }


}
