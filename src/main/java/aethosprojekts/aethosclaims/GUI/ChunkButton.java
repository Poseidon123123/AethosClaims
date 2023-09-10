package aethosprojekts.aethosclaims.GUI;

import aethosprojekts.aethosclaims.AethosClaims;
import aethosprojekts.aethosclaims.ChunkMapper;
import aethosprojekts.aethosclaims.GrundstückCommand;
import aethosprojekts.aethosclaims.Interfaces.ChunkHolder;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.ItemButton;

import java.util.List;
import java.util.Objects;

public class ChunkButton extends ItemButton<GUIMap> {

    public ChunkButton(Chunk chunk1, ChunkHolder holder) {
        ChunkMapper mapper = AethosClaims.getInstance().getWorldMapper(chunk1.getWorld());
        if (mapper.boughtChunk(chunk1)) {
            if (!Objects.equals(mapper.getChunkHolder(chunk1).getUUID(), holder.getUUID())) {
                setIcon(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("§c" + chunk1.getX() + ", " + chunk1.getZ()).build());
            } else {
                setIcon(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).name("§1" + chunk1.getX() + ", " + chunk1.getZ()).build());
            }
        } else if (mapper.claimedChunk(chunk1)) {
            setIcon(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name("§5" + chunk1.getX() + ", " + chunk1.getZ()).build());
        } else {
            setIcon(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§a" + chunk1.getX() + ", " + chunk1.getZ()).build());
        }
    }

    public ChunkButton(Chunk chunk) {
        setIcon(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name("§6" + chunk.getX() + ", " + chunk.getZ()).lore("§1Deine Position").build());
    }

    @Override
    public void onClick(GUIMap holder, InventoryClickEvent event) {
        super.onClick(holder, event);
        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) {
            if (event.getCurrentItem() != null && !event.getCurrentItem().equals(GUIMap.playerHolder)) {
                int s = event.getSlot();
                List<Integer> row = List.of(2, 11, 20, 29, 38);
                List<Integer> colum = List.of(2, 3, 4, 5, 6);

                int addRow;
                if (row.contains(s)) {
                    addRow = -2;
                } else if (row.contains(s - 1)) {
                    addRow = -1;
                } else if (row.contains(s - 3)) {
                    addRow = 1;
                } else if (row.contains(s - 4)) {
                    addRow = 2;
                } else {
                    addRow = 0;
                }


                int addColum;
                if (colum.contains(s)) {
                    addColum = -2;
                } else if (colum.contains(s - 9)) {
                    addColum = -1;
                } else if (colum.contains(s - 18)) {
                    addColum = 0;
                } else if (colum.contains(s - 27)) {
                    addColum = 1;
                } else {
                    addColum = 2;
                }
                Chunk chunk = player.getWorld().getChunkAt(player.getChunk().getX() + addRow, player.getChunk().getZ() + addColum);
                player.sendMessage(GrundstückCommand.getChunkInfoString(chunk));
            }
        }
    }
}
