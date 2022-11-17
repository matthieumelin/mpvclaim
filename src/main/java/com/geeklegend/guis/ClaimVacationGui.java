package com.geeklegend.guis;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.utils.MessageUtils;

public class ClaimVacationGui extends Gui {
	public ClaimVacationGui(int size, String title) {
		super(size, title);
	}

	public void loadItems(final Claim claim) {
		super.loadItems(claim);

		Arrays.asList(
				new ItemGui(config.getInt("guis.claim.vacation.items.three-days.slot"),
						Material.valueOf(config.getString("guis.claim.vacation.items.three-days.icon").replace(" ", "_")
								.toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.vacation.items.three-days.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.vacation.items.three-days.lores")),
						false),
				new ItemGui(config.getInt("guis.claim.vacation.items.seven-days.slot"),
						Material.valueOf(config.getString("guis.claim.vacation.items.seven-days.icon").replace(" ", "_")
								.toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.vacation.items.seven-days.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.vacation.items.seven-days.lores")),
						false),
				new ItemGui(config.getInt("guis.claim.vacation.items.fifteen-days.slot"),
						Material.valueOf(config.getString("guis.claim.vacation.items.fifteen-days.icon")
								.replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.vacation.items.fifteen-days.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.vacation.items.fifteen-days.lores")),
						false),
				new ItemGui(config.getInt("guis.claim.vacation.items.disable.slot"),
						Material.valueOf(config.getString("guis.claim.vacation.items.disable.icon").replace(" ", "_")
								.toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.vacation.items.disable.name"),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.vacation.items.disable.lores")),
						false))
				.forEach(item -> items.add(item));
	}

	@Override
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		final Inventory clickedInventory = event.getClickedInventory();

		if (clickedInventory == null)
			return;
		if (!event.getView().getTitle().equalsIgnoreCase(title))
			return;
		final ItemStack item = event.getCurrentItem();
		if (item == null)
			return;

		final Player whoClicked = (Player) event.getWhoClicked();
		final MPVClaim plugin = MPVClaim.getPlugin();
		final FileConfiguration config = plugin.getConfig();
		final ClaimManager claimManager = plugin.getClaimManager();
		final Claim claim = claimManager.getClaim(whoClicked.getUniqueId());

		if (claim == null)
			return;

		event.setCancelled(true);

		final int slot = event.getSlot();

		if (slot == config.getInt("guis.claim.vacation.items.three-days.slot")) {
			claimManager.enableVacation(whoClicked, claim, 3);
		} else if (slot == config.getInt("guis.claim.vacation.items.seven-days.slot")) {
			claimManager.enableVacation(whoClicked, claim, 7);
		} else if (slot == config.getInt("guis.claim.vacation.items.fifteen-days.slot")) {
			claimManager.enableVacation(whoClicked, claim, 15);
		} else if (slot == config.getInt("guis.claim.vacation.items.disable.slot")) {
			claimManager.disableVacation(whoClicked, claim);
		}
	}
}
