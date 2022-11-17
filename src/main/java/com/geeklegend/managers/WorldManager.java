package com.geeklegend.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class WorldManager implements IManager {
	@Override
	public void register() {
		//deleteEntities();
	}

	public void deleteEntities() {
		Bukkit.getWorlds().stream()
				.filter(world -> !(world.getName().endsWith("_nether") || world.getName().endsWith("_the_end")))
				.forEach(world -> {
					world.getEntities().stream()
							.filter(entity -> entity != null && entity.getType() == EntityType.ENDER_CRYSTAL)
							.forEach(Entity::remove);
				});
	}

	public void onDisable() {
		//deleteEntities();
	}
}
