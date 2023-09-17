package aethosprojekts.aethosclaims.gui;

import aethosprojekts.aethosclaims.AethosClaims;
import aethosprojekts.aethosclaims.api.ChunkHolder;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GUIMap extends MenuHolder<AethosClaims> {
    public static ItemStack playerHolder = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();

    public GUIMap(String title, Chunk mittelpunkt, ChunkHolder holder) {
        super(AethosClaims.getInstance(), 45, title);
        Set<Integer> a = Set.of(0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 25, 26, 27, 28, 34, 35, 36, 37, 43, 44);
        List<Integer> NS = List.of(-2, -1, 0, 1, 2);
        List<Chunk> chunks = new ArrayList<>();
        for (int z : NS) {
            for (int x : NS) {
                chunks.add(mittelpunkt.getWorld().getChunkAt(mittelpunkt.getX() + x, mittelpunkt.getZ() + z));
            }
        }
        int pos = 0;
        for (int i = 0; i < 45; i++) {
            if (a.contains(i)) {
                setButton(i, new ItemButton<>(playerHolder));
            } else if (i == 22) {
                setButton(i, new ChunkButton(mittelpunkt));
                pos++;
            } else {
                setButton(i, new ChunkButton(chunks.get(pos), holder));
                pos++;
            }
        }
    }

}
