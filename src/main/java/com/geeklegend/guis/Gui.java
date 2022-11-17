package com.geeklegend.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.claim.ClaimMember;

public class Gui implements Listener {
	public Inventory inventory;
	private final int size;
	public final String title;
	public List<ItemGui> items = new ArrayList<>();
	private final MPVClaim plugin = MPVClaim.getPlugin();
	public final FileConfiguration config = plugin.getConfig();
	public ClaimMember claimMember;

	public Gui(final int size, final String title) {
		this.inventory = null;
		this.size = size;
		this.title = title;

		plugin.getListenerManager().register(this);
	}

	public void create(final Player player) {
		inventory = Bukkit.createInventory(player, size, title);

		createItemsFromList(inventory, items);

		player.openInventory(inventory);
	}
	
	public void create(final Player player, final ClaimMember claimMember) {
		this.claimMember = claimMember;
		
		inventory = Bukkit.createInventory(player, size, title);

		createItemsFromList(inventory, items);

		player.openInventory(inventory);
	}
	
	public void onInventoryClick(final InventoryClickEvent event) {}

	public void loadItems(final Claim claim) {
		items.clear();
	}

	public void loadItems(final Player whoClicked, final Claim claim) {
		items.clear();
	}

	public void loadItems(final Player player, final Claim claim, final ClaimMember claimMember) {
		items.clear();
	}

	public void updateInventory(final List<ItemGui> updatedItems) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			final InventoryView openInventory = onlinePlayer.getOpenInventory();
			if (openInventory != null && openInventory.getTitle().equalsIgnoreCase(title)) {
				final Inventory topInventory = openInventory.getTopInventory();
				if (topInventory != null) {
					topInventory.clear();
					items.clear();
					items.addAll(updatedItems);
					createItemsFromList(topInventory, updatedItems);
				}
			}
		}
	}

	private void createItemsFromList(final Inventory inventory, final List<ItemGui> items) {
		items.stream().filter(Objects::nonNull).forEach(item -> {
			final Material icon = item.getIcon();
			ItemStack itemStack = new ItemStack(icon);

			if (icon.equals(Material.PLAYER_HEAD)) {
				SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
				skullMeta.setOwningPlayer(item.getPlayer());
				skullMeta.setDisplayName(item.getDisplayName());
				skullMeta.setLore(item.getLores());

				itemStack.setItemMeta(skullMeta);
			} else {
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(item.getDisplayName());
				itemMeta.setLore(item.getLores());

				itemStack.setItemMeta(itemMeta);
			}

			if (item.isToAdd()) {
				inventory.addItem(itemStack);
			} else {
				inventory.setItem(item.getSlot(), itemStack);
			}
		});
	}

	public Inventory getInventory() {
		return inventory;
	}

	public ItemGui getItemBySlot(final int slot) {
		return items.stream().filter(item -> item.getSlot() == slot).findFirst().orElse(null);
	}

	public int getSize() {
		return size;
	}

	public String getTitle() {
		return title;
	}

	public List<ItemGui> getItems() {
		return items;
	}

	public void setItems(List<ItemGui> items) {
		this.items = items;
	}

	public FileConfiguration getConfig() {
		return config;
	}

}
