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
import com.geeklegend.claim.ClaimMember;
import com.geeklegend.claim.ClaimMemberAccess;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.utils.MessageUtils;

public class ClaimMemberSettingGui extends Gui {
	public ClaimMemberSettingGui(int size, String title) {
		super(size, title);
	}

	@Override
	public void loadItems(final Player player, final Claim claim, final ClaimMember claimMember) {
		super.loadItems(player, claim, claimMember);

		Arrays.asList(
				new ItemGui(config.getInt("guis.claim.member-settings.items.can-open-heart.slot"),
						Material.valueOf(config.getString("guis.claim.member-settings.items.can-open-heart.icon")
								.replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.member-settings.items.can-open-heart.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.member-settings.items.can-open-heart.lores"),
								"%can_open_heart%", claimMember.isCanOpenHeart() ? "✔" : "✖"),
						false),
				new ItemGui(config.getInt("guis.claim.member-settings.items.can-build.slot"),
						Material.valueOf(config.getString("guis.claim.member-settings.items.can-build.icon")
								.replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.member-settings.items.can-build.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.member-settings.items.can-build.lores"), "%can_build%",
								claimMember.isCanBuild() ? "✔" : "✖"),
						false),
				new ItemGui(config.getInt("guis.claim.member-settings.items.can-buy-kw.slot"),
						Material.valueOf(config.getString("guis.claim.member-settings.items.can-buy-kw.icon")
								.replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.member-settings.items.can-buy-kw.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.member-settings.items.can-buy-kw.lores"),
								"%can_buy_kw%", claimMember.isCanBuyKW() ? "✔" : "✖"),
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
		
		event.setCancelled(true);

		final Player whoClicked = (Player) event.getWhoClicked();
		final int slot = event.getSlot();
		final MPVClaim plugin = MPVClaim.getPlugin();
		final FileConfiguration config = plugin.getConfig();
		final ClaimManager claimManager = plugin.getClaimManager();
		final Claim claim = claimManager.getClaim(whoClicked.getUniqueId());

		if (claim == null)
			return;
		
		if (!claim.isOwner(whoClicked)) {
			MessageUtils.sendMsgFromConfig(whoClicked, "claim.not-owner");
			return;
		}

		if (claimMember == null)
			return;

		if (slot == config.getInt("guis.claim.member-settings.items.can-open-heart.slot")) {
			claimManager.toggleAccess(whoClicked, claimMember, ClaimMemberAccess.CAN_OPEN_HEART);
		} else if (slot == config.getInt("guis.claim.member-settings.items.can-build.slot")) {
			claimManager.toggleAccess(whoClicked, claimMember, ClaimMemberAccess.CAN_BUILD);
		} else if (slot == config.getInt("guis.claim.member-settings.items.can-buy-kw.slot")) {
			claimManager.toggleAccess(whoClicked, claimMember, ClaimMemberAccess.CAN_BUY_KW);
		}
	}

}
