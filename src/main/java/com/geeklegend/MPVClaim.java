package com.geeklegend;

import com.geeklegend.database.DatabaseConnection;
import com.geeklegend.database.DatabaseCredentials;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.managers.CommandManager;
import com.geeklegend.managers.ListenerManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MPVClaim extends JavaPlugin {
    private static MPVClaim plugin;

    private DatabaseConnection databaseConnection;

    private Economy economy;

    private ClaimManager claimManager;

    @Override
    public void onEnable() {
        plugin = this;

        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        databaseConnection = new DatabaseConnection(new DatabaseCredentials(getConfig().getString("database.host"),
                getConfig().getString("database.user"), getConfig().getString("database.password"),
                getConfig().getString("database.name"),
                getConfig().getInt("database.port")));

        new CommandManager().register();
        new ListenerManager().register();

        claimManager = new ClaimManager();
    }

    @Override
    public void onDisable() {
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public static MPVClaim getPlugin() {
        return plugin;
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ClaimManager getClaimManager() {
        return claimManager;
    }
}
