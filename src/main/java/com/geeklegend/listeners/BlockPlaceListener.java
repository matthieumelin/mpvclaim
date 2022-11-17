package com.geeklegend.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.managers.PermissionManager;

import net.luckperms.api.model.user.User;

public class BlockPlaceListener implements Listener {
	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		final MPVClaim plugin = MPVClaim.getPlugin();
		final ClaimManager claimManager = plugin.getClaimManager();
		final Claim claim = claimManager.getClaimByLoc(event.getBlock().getLocation());

		if (claim == null) {
			return;
		}
		if (!claim.isActive() || !claim.isInVacation()) {
			return;
		}
		if (claim.isMemberCanBuild(player)) {
			return;
		}
		final PermissionManager permissionManager = plugin.getPermissionManager();
		final User user = permissionManager.getUser(player.getUniqueId());

		if (player.isOp() || plugin.getPermissionManager().hasPermission(user,
				plugin.getConfig().getString("claim.bypass-permission"))) {
			return;
		}

		event.setCancelled(true);
	}
}
