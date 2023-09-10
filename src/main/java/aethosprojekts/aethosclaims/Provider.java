package aethosprojekts.aethosclaims;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;


public class Provider {
    @NotNull
    public static final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
            .getRegistration(Economy.class);

    public static Economy getEconomy() {
        return economyProvider.getProvider();
    }
}
