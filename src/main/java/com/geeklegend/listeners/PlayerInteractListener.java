package com.geeklegend.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.managers.PermissionManager;

import net.luckperms.api.model.user.User;

public class PlayerInteractListener implements Listener {
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final Block clickedBlock = event.getClickedBlock();
		final ItemStack item = event.getItem();
		final MPVClaim plugin = MPVClaim.getPlugin();
		final PermissionManager permissionManager = plugin.getPermissionManager();
		final User user = permissionManager.getUser(player.getUniqueId());
		
		if (clickedBlock != null && item != null) {
			final Claim claim = plugin.getClaimManager().getClaimByLoc(clickedBlock.getLocation());
			if (claim == null)
				return;
			if (claim.isMemberCanBuild(player)) {
				return;
			}
			if (player.isOp() || plugin.getPermissionManager().hasPermission(user,
					plugin.getConfig().getString("claim.bypass-permission"))) {
				return;
			}
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item.getType().name().replace("_", "").contains("EGG")) {
				event.setCancelled(true);
			} else {
				event.setCancelled(true);
			}
		}
	}

}
