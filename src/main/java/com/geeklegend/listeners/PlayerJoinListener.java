package com.geeklegend.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.utils.MessageUtils;

public class PlayerJoinListener implements Listener {
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final Claim claim = MPVClaim.getPlugin().getClaimManager().getClaim(player.getUniqueId());
		
		if (claim == null) return;
		
		final float kw = claim.getKw();
		
		if (kw <= 1000) {
			MessageUtils.sendMsgFromConfig(player, "claim.heart.need-refill", "%claim_kw%", String.valueOf(kw));
		}
	}

}
