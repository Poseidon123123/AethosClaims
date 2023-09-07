package aethosprojekts.aethosclaims.GUI;

import aethosprojekts.aethosclaims.AethosClaims;
import aethosprojekts.aethosclaims.ChunkMapper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapGUI {
    public static final ItemStack placeHolder = createGUIItems(Material.GRAY_STAINED_GLASS_PANE, " ");
    public static final ItemStack chunk = createGUIItems(Material.DIRT, "Error");
    public static final ItemStack pos = createGUIItems(Material.STONE, "Error");
    private static final ItemStack[] contents = {
            placeHolder, placeHolder, chunk, chunk, chunk, chunk, chunk, placeHolder, placeHolder,
            placeHolder, placeHolder, chunk, chunk, chunk, chunk, chunk, placeHolder, placeHolder,
            placeHolder, placeHolder, chunk, chunk, pos, chunk, chunk, placeHolder, placeHolder,
            placeHolder, placeHolder, chunk, chunk, chunk, chunk, chunk, placeHolder, placeHolder,
            placeHolder, placeHolder, chunk, chunk, chunk, chunk, chunk, placeHolder, placeHolder
    };

    public static Inventory getMapGUI(Player player, List<Chunk> chunkList) {
        ChunkMapper mapper = AethosClaims.getInstance().getWorldMapper(player.getWorld());
        Inventory mapGUI = Bukkit.createInventory(player, 45, Component.text("Map"));
        mapGUI.setContents(contents);
        mapGUI.setItem(mapGUI.first(pos), createGUIItems(Material.YELLOW_STAINED_GLASS_PANE, "§6" + player.getChunk().getX() + ", " + player.getChunk().getZ(), "§1Deine Position"));
        Chunk mittelpunkt = player.getChunk();
        List<Integer> NS = List.of(-2, -1, 0, 1, 2);
        List<Chunk> chunks = new ArrayList<>();
        for (int z : NS) {
            for (int x : NS) {
                chunks.add(mittelpunkt.getWorld().getChunkAt(mittelpunkt.getX() + x, mittelpunkt.getZ() + z));
            }
        }
        chunks.remove(mittelpunkt);


        for (Chunk chunk1 : chunks) {
            System.out.println(chunk1);
            if (mapper.boughtChunk(chunk1)) {
                if (!Objects.equals(mapper.getChunkHolder(chunk1).getUUID(), player.getUniqueId())) {
                    mapGUI.setItem(mapGUI.first(chunk), createGUIItems(Material.RED_STAINED_GLASS_PANE, "§c" + chunk1.getX() + ", " + chunk1.getZ()));
                } else {
                    mapGUI.setItem(mapGUI.first(chunk), createGUIItems(Material.BLUE_STAINED_GLASS_PANE, "§1" + chunk1.getX() + ", " + chunk1.getZ()));
                }
            } else if (mapper.claimedChunk(chunk1)) {
                mapGUI.setItem(mapGUI.first(chunk), createGUIItems(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§5" + chunk1.getX() + ", " + chunk1.getZ()));
            } else {
                mapGUI.setItem(mapGUI.first(chunk), createGUIItems(Material.LIME_STAINED_GLASS_PANE, "§a" + chunk1.getX() + ", " + chunk1.getZ()));
            }
        }
        return mapGUI;
    }

    public static ItemStack createGUIItems(final Material material, final String name, final String... Lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(Lore));
        item.setItemMeta(meta);
        return item;
    }
}
