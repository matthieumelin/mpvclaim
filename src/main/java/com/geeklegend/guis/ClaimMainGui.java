package com.geeklegend.guis;

import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.managers.GuiManager;
import com.geeklegend.utils.MessageUtils;

public class ClaimMainGui extends Gui {
	public ClaimMainGui(int size, String title) {
		super(size, title);
	}

	@Override
	public void loadItems(Claim claim) {
		super.loadItems(claim);
		Arrays.asList(
				new ItemGui(config.getInt("guis.claim.main.items.infos.slot"),
						Material.valueOf(
								config.getString("guis.claim.main.items.infos.icon").replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.main.items.infos.name"),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.main.items.infos.lores"),
								"%claim_level%", String.valueOf(claim.getLevel()), "%claim_kw%",
								String.valueOf(claim.getKw()), "%claim_capacity%",
								String.valueOf(claim.getKwCapacity()), "%claim_active%", claim.isActive() ? "✔" : "✖"),
						false),
				new ItemGui(config.getInt("guis.claim.main.items.refill.slot"),
						Material.valueOf(
								config.getString("guis.claim.main.items.refill.icon").replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.main.items.refill.name"),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.main.items.refill.lores"),
								"%claim_refill_price%", MessageUtils.getFromConfig("claim.heart.refill.price")),
						false),
				new ItemGui(config.getInt("guis.claim.main.items.levelup.slot"),
						Material.valueOf(
								config.getString("guis.claim.main.items.levelup.icon").replace(" ", "_").toUpperCase()),
						claim.isMaxLevelReached() ? "Niveau max atteint"
								: MessageUtils.getFromConfig("guis.claim.main.items.levelup.name", "%claim_level%",
										String.valueOf(claim.getLevel() + 1)),
						MessageUtils.getTranslatedList(
								claim.isMaxLevelReached()
										? Collections.singletonList("&7Vous avez atteint le niveau max.")
										: config.getStringList("guis.claim.main.items.levelup.lores"),
								"%claim_capacity%", String.valueOf(claim.getKwCapacity()), "%claim_uptake%",
								String.valueOf(config.getInt("claim.levels." + (claim.getLevel() + 1) + ".kw.uptake")),
								"%claim_claims_available%",
								String.valueOf(
										config.getInt("claim.levels." + (claim.getLevel() + 1) + ".claims-available")),
								"%claim_levelup_price%",
								String.valueOf(config.getInt("claim.levels." + (claim.getLevel() + 1) + ".price"))),
						false),
				new ItemGui(config.getInt("guis.claim.main.items.members.slot"),
						Material.valueOf(
								config.getString("guis.claim.main.items.members.icon").replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.main.items.members.name", "%claim_members%",
								String.valueOf(claim.getMembers().size())),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.main.items.members.lores")),
						false),
				new ItemGui(config.getInt("guis.claim.main.items.vacation.slot"),
						Material.valueOf(
								config.getString("guis.claim.main.items.vacation.icon").replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.main.items.vacation.name"),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.main.items.vacation.lores")),
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
		if (event.getCurrentItem() == null)
			return;
		
		event.setCancelled(true);

		final Player whoClicked = (Player) event.getWhoClicked();
		final int slot = event.getSlot();
		final MPVClaim plugin = MPVClaim.getPlugin();
		final FileConfiguration config = plugin.getConfig();
		final ClaimManager claimManager = plugin.getClaimManager();
		final GuiManager guiManager = plugin.getGuiManager();
		final Claim claim = claimManager.getClaim(whoClicked.getUniqueId());

		if (claim == null)
			return;

		if (slot == config.getInt("guis.claim.main.items.refill.slot")) {
			claimManager.refillHeart(whoClicked, claim);
		} else if (slot == config.getInt("guis.claim.main.items.levelup.slot")) {
			claimManager.levelUp(whoClicked, claim);
		} else if (slot == config.getInt("guis.claim.main.items.members.slot")) {
			final Gui memberGui = guiManager.getGui(MessageUtils.getFromConfig("guis.claim.members.title"));
			if (memberGui == null)
				return;
			memberGui.loadItems(whoClicked, claim);
			memberGui.create(whoClicked);
		} else if (slot == config.getInt("guis.claim.main.items.vacation.slot")) {
			if (!claim.isOwner(whoClicked)) {
				MessageUtils.sendMsgFromConfig(whoClicked, "claim.not-owner");
				return;
			}
			final Gui vacationGui = guiManager.getGui(MessageUtils.getFromConfig("guis.claim.vacation.title"));
			if (vacationGui == null)
				return;
			vacationGui.loadItems(claim);
			vacationGui.create(whoClicked);
		}
	}
}
