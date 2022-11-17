package com.geeklegend.listeners;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;

public class ExplosionPrimeListener implements Listener {
	@EventHandler
	public void onExplosionPrime(final ExplosionPrimeEvent event) {
		final Entity entity = event.getEntity();
		if (!(entity instanceof TNTPrimed || entity instanceof Creeper)) return; 
		final int radius = Math.round(event.getRadius());
		final ArrayList<Block> blocks = getNearbyBlocks(entity.getLocation(), radius);
		for (Block block : blocks) {
			final Claim claim = MPVClaim.getPlugin().getClaimManager().getClaimByLoc(block.getLocation());
			if (claim == null) return;
			event.setCancelled(true);
		}
	}

	public ArrayList<Block> getNearbyBlocks(Location location, int radius) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}

}
