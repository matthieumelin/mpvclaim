package com.geeklegend.guis;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

public class ItemGui {
	private final int slot;
	private final Material icon;
	private final String displayName;
	private final List<String> lores;
	private final boolean toAdd;
	private final OfflinePlayer player;

	public ItemGui(final int slot, final Material icon, final String displayName, final List<String> lores, final boolean toAdd, final OfflinePlayer player) {
		this.slot = slot;
		this.icon = icon;
		this.displayName = displayName;
		this.lores = lores;
		this.toAdd = toAdd;
		this.player = player;
	}
	
	public ItemGui(final int slot, final Material icon, final String displayName, final List<String> lores, final boolean toAdd) {
		this(slot, icon, displayName, lores, toAdd, null);
	}
	
	public ItemGui(final Material icon, final String displayName, final List<String> lores, final boolean toAdd) {
		this(0, icon, displayName, lores, toAdd, null);
	}
	
	public ItemGui(final Material icon, final String displayName, final List<String> lores, final boolean toAdd, final OfflinePlayer player) {
		this(0, icon, displayName, lores, toAdd, player);
	}

	public int getSlot() {
		return slot;
	}

	public Material getIcon() {
		return icon;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public List<String> getLores() {
		return lores;
	}
	public boolean isToAdd() {
		return toAdd;
	}
	
	public OfflinePlayer getPlayer() {
		return player;
	}
	
}
