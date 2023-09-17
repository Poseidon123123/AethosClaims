package aethosprojekts.aethosclaims.listener;

import aethosprojekts.aethosclaims.AethosClaims;
import aethosprojekts.aethosclaims.ClaimFighter;
import aethosprojekts.aethosclaims.chunks.ChunkMapper;
import aethosprojekts.aethosclaims.chunks.PlayerChunkHolder;
import aethosprojekts.aethosclaims.events.ChunkAttackEvent;
import aethosprojekts.aethosclaims.permissions.DefaultPermission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GSListner implements Listener {

    @EventHandler
    public void playerKill(PlayerDeathEvent event) {
        PlayerChunkHolder holder = new PlayerChunkHolder(event.getPlayer().getUniqueId());
        ClaimFighter killed = ClaimFighter.getByHoler(holder);
        if (killed == null) {
            return;
        }
        if (event.getEntity().getKiller() == null) {
            return;
        }
        if (!killed.isKill()) {
            return;
        }
        Player killer = event.getEntity().getKiller();
        PlayerChunkHolder killHolder = new PlayerChunkHolder(killer.getUniqueId());
        ClaimFighter killFighter = ClaimFighter.getByHoler(killHolder);
        if (killFighter == null) {
            return;
        }
        if (killed.equals(killFighter)) {
            killFighter.addScore(killHolder, 1);
            killHolder.sendMessage("§2Der Spieler " + killer.getName() + " hat eine weitere Tötung der Chunkfight hinzugefügt");
        }

    }

    //TODO Delete if BugFix
    @EventHandler
    public void kampfType(ChunkAttackEvent event) {
        event.setKill(true);
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event) {
        ChunkMapper mapper = AethosClaims.getInstance().getWorldMapper(event.getBlock().getWorld());
        if (mapper.boughtChunk(event.getBlock().getChunk())) {
            if (!mapper.getChunkHolder(event.getBlock().getChunk()).hasPermission(event.getPlayer().getUniqueId(), DefaultPermission.Place)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§4Du darfst hier nichts platzieren");
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        ChunkMapper mapper = AethosClaims.getInstance().getWorldMapper(event.getBlock().getWorld());
        if (mapper.boughtChunk(event.getBlock().getChunk())) {
            if (!mapper.getChunkHolder(event.getBlock().getChunk()).hasPermission(event.getPlayer().getUniqueId(), DefaultPermission.Destroy)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§4Du darfst hier nicht abbauen");
            }
        }
    }

    @EventHandler
    public void interActEvent(PlayerInteractEvent event) {
        ChunkMapper mapper = AethosClaims.getInstance().getWorldMapper(event.getPlayer().getWorld());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!mapper.boughtChunk(event.getPlayer().getChunk())) {
                if (mapper.getChunkHolder(event.getPlayer().getChunk()).hasPermission(event.getPlayer().getUniqueId(), DefaultPermission.Interact)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§4Du darfst dass hier nicht benutzen");
                }
            }
        }
    }

    @EventHandler
    public void interActEvent(PlayerInteractEntityEvent event) {
        ChunkMapper mapper = AethosClaims.getInstance().getWorldMapper(event.getPlayer().getWorld());
        if (mapper.boughtChunk(event.getPlayer().getChunk())) {
            if (mapper.getChunkHolder(event.getPlayer().getChunk()).hasPermission(event.getPlayer().getUniqueId(), DefaultPermission.Interact)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§4Du darfst dass hier nicht benutzen");
            }
        }
    }

    @EventHandler
    public void interActEvent(PlayerInteractAtEntityEvent event) {
        ChunkMapper mapper = AethosClaims.getInstance().getWorldMapper(event.getPlayer().getWorld());
        if (mapper.boughtChunk(event.getPlayer().getChunk())) {
            if (!mapper.getChunkHolder(event.getPlayer().getChunk()).hasPermission(event.getPlayer().getUniqueId(), DefaultPermission.Interact)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§4Du darfst dass hier nicht benutzen");
            }
        }
    }

}
