package aethosprojekts.aethosclaims.UIs;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public abstract class UI extends InventoryView {
    protected Player player;
    protected Inventory inv;

    public UI(Player player, Inventory inv) {
        this.player = player;
        this.inv = inv;
    }


    @Override
    public @NotNull Inventory getTopInventory() {
        return inv;
    }

    @Override
    public @NotNull Inventory getBottomInventory() {
        return player.getInventory();
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull InventoryType getType() {
        return InventoryType.CHEST;
    }

    @Override
    public abstract @NotNull String getTitle();

    @Override
    public abstract void setTitle(@NotNull String title);

    @Override
    public abstract String getOriginalTitle();

    public abstract void onClick(InventoryClickEvent event);

}
