package com.geeklegend.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.geeklegend.claim.Claim;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

public class HologramManager implements IManager {
	private final Map<Claim, Hologram> holograms = new HashMap<>();

	@Override
	public void register() {
	}

	public void createHologram(final Claim claim, final Location location, final List<String> lines) {
		Hologram hologram = DHAPI.getHologram(claim.getOwnerName());
		if (hologram != null) {
			hologram.destroy();
			hologram.unregister();
		}
		hologram = DHAPI.createHologram(claim.getOwnerName(), location, lines);
		hologram.enable();
		holograms.put(claim, hologram);
	}

	public void deleteHologram(final Claim claim) {
		final Hologram hologram = getHologram(claim);
		if (hologram == null) return;
		hologram.destroy();
		holograms.remove(claim);
	}

	public void updateHologram(final Claim claim, final List<String> lines) {
		final Hologram hologram = getHologram(claim);
		if (hologram == null) return;
		DHAPI.setHologramLines(hologram, lines);
	}

	public Hologram getHologram(final Claim claim) {
		final Hologram hologram = holograms.get(claim);
		if (hologram == null) {
			return null;
		}
		return hologram;
	}

	public void onDisable() {
		holograms.forEach((key, value) -> deleteHologram(key));
		DecentHologramsAPI.get().getHologramManager().unregister();
	}
}
