package com.geeklegend.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.guis.Gui;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.utils.MessageUtils;

public class PlayerInteractEntityListener implements Listener {
	@EventHandler
	public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
		final Entity rightClicked = event.getRightClicked();

		if (rightClicked == null)
			return;
		if (rightClicked.getType() != EntityType.ENDER_CRYSTAL) {
			return;
		}

		if (!event.getHand().equals(EquipmentSlot.HAND))
			return;

		final MPVClaim plugin = MPVClaim.getPlugin();
		final ClaimManager claimManager = plugin.getClaimManager();
		final Claim claim = claimManager.getClaimByLoc(rightClicked.getLocation());

		if (claim == null) {
			return;
		}

		final Player player = event.getPlayer();

		if (!claim.isMemberCanOpenHeart(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-owner");
			return;
		}
		
		final Gui gui = plugin.getGuiManager().getGui(MessageUtils.getFromConfig("guis.claim.main.title"));

		if (gui == null) {
			return;
		}

		gui.loadItems(claim);
		gui.create(player);

		event.setCancelled(true);
	}

}
