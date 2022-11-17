package com.geeklegend.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;

public class BlockBurnListener implements Listener {
	@EventHandler
	public void onBlockBurn(final BlockBurnEvent event) {
		final Block block = event.getBlock();

		if (block == null)
			return;

		final Claim claim = MPVClaim.getPlugin().getClaimManager().getClaimByLoc(block.getLocation());

		if (claim == null)
			return;
		event.setCancelled(true);
	}

}
