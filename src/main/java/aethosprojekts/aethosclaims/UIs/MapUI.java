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
            List<Integer> a = List.of(2, 11, 20, 29, 38);
            List<Integer> b = List.of(2, 3, 4, 5, 6);
            
            int addZ;
            if (a.contains(s)) {
                addZ = -2;
            } else if (a.contains(s - 1)) {
                addZ = -1;
            } else if (a.contains(s - 3)) {
                addZ = 1;
            } else if (a.contains(s - 4)) {
                addZ = 2;
            } else {
                addZ = 0;
            }


            int addX;
            if (b.contains(s)) {
                addX = -2;
            } else if (b.contains(s - 9)) {
                addX = -1;
            } else if (b.contains(s - 18)) {
                addX = 0;
            } else if (b.contains(s - 27)) {
                addX = 1;
            } else {
                addX = 2;
            }
            Chunk chunk = player.getWorld().getChunkAt(player.getChunk().getX() + addX, player.getChunk().getZ() + addZ);
            player.sendMessage(GrundstückCommand.getChunkInfoString(chunk));
        }
    }
}
