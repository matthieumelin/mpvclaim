package com.geeklegend;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.geeklegend.database.DatabaseConnection;
import com.geeklegend.database.DatabaseCredentials;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.managers.CommandManager;
import com.geeklegend.managers.GuiManager;
import com.geeklegend.managers.HologramManager;
import com.geeklegend.managers.ListenerManager;
import com.geeklegend.managers.PermissionManager;
import com.geeklegend.managers.TaskManager;
import com.geeklegend.managers.WorldManager;

import net.milkbowl.vault.economy.Economy;

public class MPVClaim extends JavaPlugin {
	private static MPVClaim plugin;

	private DatabaseConnection databaseConnection;

	private Economy economy;

	private HologramManager hologramManager;
	private WorldManager worldManager;
	private ClaimManager claimManager;
	private ListenerManager listenerManager;
	private GuiManager guiManager;
	private TaskManager taskManager;
	private PermissionManager permissionManager;

	@Override
	public void onEnable() {
		plugin = this;
		
		if (!setupEconomy()) {
			getLogger().severe(
					String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		saveDefaultConfig();

		databaseConnection = new DatabaseConnection(new DatabaseCredentials(getConfig().getString("database.host"),
				getConfig().getString("database.user"), getConfig().getString("database.password"),
				getConfig().getString("database.name"), getConfig().getInt("database.port")));

		hologramManager = new HologramManager();
		worldManager = new WorldManager();
		listenerManager = new ListenerManager();
		claimManager = new ClaimManager();
		guiManager = new GuiManager();
		taskManager = new TaskManager();
		permissionManager = new PermissionManager();

		hologramManager.register();
		worldManager.register();
		listenerManager.register();
		claimManager.register();
		guiManager.register();
		taskManager.register();
		permissionManager.register();

		new CommandManager().register();
	}

	@Override
	public void onDisable() {
		worldManager.onDisable();
		claimManager.onDisable();
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

	public WorldManager getWorldManager() {
		return worldManager;
	}

	public ClaimManager getClaimManager() {
		return claimManager;
	}

	public ListenerManager getListenerManager() {
		return listenerManager;
	}

	public GuiManager getGuiManager() {
		return guiManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public HologramManager getHologramManager() {
		return hologramManager;
	}
}
