package com.geeklegend.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {
	public static Location getParsedLocation(final String string) {
		final String[] args = string.trim().split(",");
		final World world = Bukkit.getWorld(args[0]);
		final double x = Double.parseDouble(args[1]);
		final double y = Double.parseDouble(args[2]);
		final double z = Double.parseDouble(args[3]);
		return new Location(world, x, y, z);
	}
}
