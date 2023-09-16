package aethosprojekts.aethosclaims;

import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public class ClaimFighter {
    private static final HashMap<Long, ClaimFighter> fighterHashMap = new HashMap<>();
    private static final HashMap<ChunkHolder, ClaimFighter> holderMap = new HashMap<>();

    private static final BukkitTask task = Bukkit.getScheduler().runTaskTimer(AethosClaims.getInstance(), () -> {
        for (Map.Entry<Long, ClaimFighter> entry : fighterHashMap.entrySet()) {
            ClaimFighter fighter1 = entry.getValue();
            if (fighter1.fightDone()) {
                if (fighter1.doneFight()) {
                    fighterHashMap.remove(entry.getKey(), fighter1);
                }
            }
        }
    }, 0, 72000);

    private final LocalDateTime start;
    private final Set<ChunkHolder> fighter;
    private final HashMap<ChunkHolder, Integer> killMoneyCount = new HashMap<>();
    private final boolean kill;
    private final UUID worldName;
    private final long chunkKey;

    public ClaimFighter(LocalDateTime start, Set<ChunkHolder> fighter, boolean kill, Chunk chunk) {
        this.start = start;
        this.fighter = fighter;
        this.kill = kill;
        for (ChunkHolder holder : fighter) {
            killMoneyCount.put(holder, 0);
            holderMap.put(holder, this);
        }
        this.worldName = chunk.getWorld().getUID();
        this.chunkKey = chunk.getChunkKey();
        fighterHashMap.put(chunk.getChunkKey(), this);
    }


    public static void StopDuration() {
        task.cancel();
    }

    public static ClaimFighter getByHoler(ChunkHolder holder) {
        return holderMap.get(holder);
    }

    public static ClaimFighter getByChunk(Chunk chunk) {
        return fighterHashMap.get(chunk.getChunkKey());
    }

    /**
     * @return ends the fight with reward
     */
    public boolean doneFight() {
        World world = Bukkit.getWorld(this.getWorldName());
        if (world != null) {
            ChunkMapper mapper = AethosClaims.getInstance().getWorldMapper(world);
            if (this.fightDone()) {
                ChunkHolder map = Helper.getHighestHolder(this.killMoneyCount);
                if (this.isKill()) {
                    mapper.transferChunk(this.getChunkKey(), map);
                } else {
                    if (map.withdrawMoney(this.killMoneyCount.get(map)) || this.killMoneyCount.size() == 1) {
                        mapper.transferChunk(this.getChunkKey(), map);
                    } else {
                        if (this.killMoneyCount.size() == 0) {
                            mapper.unClaimChunk(getChunkKey());
                        } else {
                            map.sendMessage("§4Ihr habt nicht genug Geld um euer Höchstgebot zu erfüllen, ihr scheidet aus.");
                            this.killMoneyCount.remove(map);
                            this.doneFight();
                        }
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public LocalDateTime getStart() {
        return start;
    }

    public Set<ChunkHolder> getFighter() {
        return fighter;
    }

    public void addFighter(ChunkHolder holder) {
        this.fighter.add(holder);
    }

    public void removeFighter(ChunkHolder holder) {
        this.fighter.remove(holder);
    }

    public HashMap<ChunkHolder, Integer> getKillMoneyCount() {
        return killMoneyCount;
    }

    public boolean isKill() {
        return kill;
    }

    public void addScore(ChunkHolder holder, int score) {
        if (killMoneyCount.containsKey(holder)) {
            killMoneyCount.replace(holder, killMoneyCount.get(holder) + score);
        }
    }

    /**
     * @return Checks if fight is done
     */
    public boolean fightDone() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(start, now);
        long difference = duration.toHours();
        int time = AethosClaims.getInstance().getConfig().getInt("GS.FightClaim.FightDuration");
        return Math.abs(difference) == time || this.getFighter().size() <= 1;
    }

    public UUID getWorldName() {
        return worldName;
    }

    public long getChunkKey() {
        return chunkKey;
    }

}
