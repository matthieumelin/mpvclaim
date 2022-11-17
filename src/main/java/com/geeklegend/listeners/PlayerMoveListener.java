package com.geeklegend.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.utils.MessageUtils;

public class PlayerMoveListener implements Listener {
	private final Map<Player, Claim> inClaims = new HashMap<>();

	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		final MPVClaim plugin = MPVClaim.getPlugin();
		final ClaimManager claimManager = plugin.getClaimManager();
		final Claim entryClaim = claimManager.getClaimByLoc(player.getLocation());
		final Claim claimInMap = inClaims.get(player);
		
		if (entryClaim == null) {
			if (inClaims.containsKey(player)) {
				MessageUtils.sendTitleFromConfig(player, "claim.area.out.title", "claim.area.out.subtitle",
						"%claim_ownername%", claimInMap.getOwnerName());
				inClaims.remove(player);
			}
		} else {
			if (!inClaims.containsKey(player) || !entryClaim.getOwner().equals(claimInMap.getOwner())) {
				inClaims.put(player, entryClaim);
				MessageUtils.sendTitleFromConfig(player, "claim.area.in.title", "claim.area.in.subtitle",
						"%claim_ownername%", entryClaim.getOwnerName());
			}
		}
	}
}
