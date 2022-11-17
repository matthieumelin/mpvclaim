package com.geeklegend.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;

public class BlockSpreadListener implements Listener {
	@EventHandler
	public void onBlockSpread(final BlockSpreadEvent event) {
		final Block block = event.getBlock();

		if (block == null)
			return;

		final Claim claim = MPVClaim.getPlugin().getClaimManager().getClaimByLoc(block.getLocation());

		if (claim != null) {
			event.setCancelled(true);
		}
	}

}
