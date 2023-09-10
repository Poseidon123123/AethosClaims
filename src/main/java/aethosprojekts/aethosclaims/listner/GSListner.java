package aethosprojekts.aethosclaims.listner;

import aethosprojekts.aethosclaims.AethosClaims;
import aethosprojekts.aethosclaims.ChunkMapper;
import aethosprojekts.aethosclaims.DefaultPermission;
import aethosprojekts.aethosclaims.UIs.UI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GSListner implements Listener {

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if (event.getView() instanceof UI ui) {
            ui.onClick(event);
        }
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
