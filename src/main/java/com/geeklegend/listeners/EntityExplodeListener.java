package com.geeklegend.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;

public class EntityExplodeListener implements Listener {
	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent event) {
		final Entity entity = event.getEntity();

		if (entity == null)
			return;

		final List<Block> blockList = event.blockList();

		if (blockList == null)
			return;

		blockList.forEach(block -> {
			final Claim claim = MPVClaim.getPlugin().getClaimManager().getClaimByLoc(block.getLocation());

			if (claim == null) {
				return;
			}
			if (!claim.isActive()) {
				return;
			}

			event.setCancelled(true);
		});
	}
}
