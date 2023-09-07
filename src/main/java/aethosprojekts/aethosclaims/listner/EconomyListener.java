package aethosprojekts.aethosclaims.listner;

import aethosprojekts.aethosclaims.UIs.UI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EconomyListener implements Listener {

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if (event.getView() instanceof UI ui) {
            ui.onClick(event);
        }
    }
}
