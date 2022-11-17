package com.geeklegend.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.managers.ClaimManager;

public class EntitySpawnListener implements Listener {
	@EventHandler
	public void onEntitySpawn(final EntitySpawnEvent event) {
		final Entity entity = event.getEntity();

		if (entity instanceof Item)
			return;

		if (entity.getType() == EntityType.ENDER_CRYSTAL || 
				entity.getType() == EntityType.ARMOR_STAND || entity instanceof Mob) {
			return;
		}

		final ClaimManager claimManager = MPVClaim.getPlugin().getClaimManager();
		final Claim claim = claimManager.getClaimByLoc(entity.getLocation());

		if (claim == null) {
			return;
		}
		if (!claim.isActive()) {
			return;
		}

		event.setCancelled(true);
	}
}
