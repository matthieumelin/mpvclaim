package com.geeklegend.guis;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.claim.ClaimMember;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.utils.MessageUtils;

public class ClaimMemberGui extends Gui {
	public ClaimMemberGui(int size, String title) {
		super(size, title);
	}

	public void loadItems(final Player player, final Claim claim) {
		super.loadItems(claim);

		claim.getMembers().stream().filter(Objects::nonNull).forEach(member -> {
			final OfflinePlayer offlineMember = Bukkit.getOfflinePlayer(member.getUuid());
			items.add(new ItemGui(Material.PLAYER_HEAD,
					MessageUtils.getFromConfig("guis.claim.members.items.member.name", "%claim_member_name%",
							member.getUsername()),
					MessageUtils.getTranslatedList(config.getStringList("guis.claim.members.items.member.lores"),
							"%can_open_heart%", member.isCanOpenHeart() ? "✔" : "✖", "%can_build%",
							member.isCanBuild() ? "✔" : "✖", "%can_buy_kw%", member.isCanBuyKW() ? "✔" : "✖"),
					true, offlineMember));
		});
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
		final MPVClaim plugin = MPVClaim.getPlugin();
		final FileConfiguration config = plugin.getConfig();
		final ClaimManager claimManager = plugin.getClaimManager();
		final Claim claim = claimManager.getClaim(whoClicked.getUniqueId());

		if (claim == null)
			return;

		final String targetName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
		final ClaimMember claimMember = claim.getMember(targetName);

		if (claimMember == null)
			return;

		if (!claim.isOwner(whoClicked)) {
			MessageUtils.sendMsgFromConfig(whoClicked, "claim.not-owner");
			return;
		}

		final Gui claimMemberSettingGui = plugin.getGuiManager()
				.getGui(config.getString("guis.claim.member-settings.title"));
		if (claimMemberSettingGui == null)
			return;
		claimMemberSettingGui.loadItems(whoClicked, claim, claimMember);
		claimMemberSettingGui.create(whoClicked, claimMember);
	}
}
