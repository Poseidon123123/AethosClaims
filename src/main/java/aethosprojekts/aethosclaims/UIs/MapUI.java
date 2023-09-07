package aethosprojekts.aethosclaims.UIs;

import aethosprojekts.aethosclaims.GUI.MapGUI;
import aethosprojekts.aethosclaims.GrundstückCommand;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapUI extends UI {
    private final Inventory inv;
    private final Player player;

    public MapUI(Player player, Inventory inv) {
        super(player, inv);
        this.inv = inv;
        if (inv.contains(MapGUI.pos) || inv.contains(MapGUI.chunk)) {
            throw new RuntimeException("ChunkError");
        }
        this.player = player;
        this.player.openInventory(this);
    }

    @Override
    public @NotNull String getTitle() {
        return "Map";
    }

    @Override
    public void setTitle(@NotNull String title) {

    }

    @Override
    public @NotNull String getOriginalTitle() {
        return "Map";
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getView() instanceof MapUI) {
            event.setCancelled(true);
        }
        if (event.getCurrentItem() != null && !event.getCurrentItem().equals(MapGUI.placeHolder)) {
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
