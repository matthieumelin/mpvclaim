package com.geeklegend.listeners;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {
	@EventHandler
	public void onEntityDamage(final EntityDamageEvent event) {
		final Entity entity = event.getEntity();

		if (entity instanceof EnderCrystal) {
			event.setCancelled(true);
		}
	}

}
