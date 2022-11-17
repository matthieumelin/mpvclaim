package com.geeklegend.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
	private final ItemStack itemStack;

	public ItemBuilder(final ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public ItemBuilder(final Material type) {
		this.itemStack = new ItemStack(type);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public ItemBuilder setAmount(final int amount) {
		itemStack.setAmount(amount);
		return this;
	}

	public ItemBuilder setType(final Material type) {
		itemStack.setType(type);
		return this;
	}

	public ItemBuilder setMaterial(final Material material) {
		return setType(material);
	}

	public ItemBuilder setItemMeta(final ItemMeta itemMeta) {
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder setDisplayName(final String displayName) {
		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;
		itemMeta.setDisplayName(displayName);
		return this;
	}

	public ItemBuilder setLore(final List<String> lines) {
		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;

		itemMeta.setLore(lines);
		return this;
	}

	public ItemBuilder setLore(final String line) {
		return setLore(Collections.singletonList(line));
	}

	public ItemBuilder addLore(final List<String> lines) {
		ArrayList<String> newLore = new ArrayList<>();

		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;

		newLore.addAll(itemMeta.getLore());
		newLore.addAll(lines);

		itemMeta.setLore(newLore);
		return this;
	}

	public ItemBuilder addLore(final String line) {
		return addLore(Collections.singletonList(line));
	}

	public ItemBuilder setDamage(final int damage) {
		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;
		if (!(itemMeta instanceof Damageable))
			return this;

		((Damageable) itemMeta).setDamage(damage);
		return this;
	}

	public ItemBuilder addEnchantments(final HashMap<Enchantment, Integer> enchantmentLevelsMap,
			final boolean ignoreLevelRestriction) {
		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;

		enchantmentLevelsMap
				.forEach(((enchantment, level) -> itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction)));

		return this;
	}

	public ItemBuilder addEnchantment(final Enchantment enchantment, final int level,
			final boolean ignoreLevelRestriction) {
		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;

		itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);

		return this;
	}

	public ItemBuilder addItemFlags(final ItemFlag[] itemFlags) {
		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;

		itemMeta.addItemFlags(itemFlags);

		return this;
	}

	public ItemBuilder addItemFlags(final List<ItemFlag> itemFlags) {
		return addItemFlags(itemFlags.toArray(new ItemFlag[0]));
	}

	public ItemBuilder addItemFlag(final ItemFlag itemFlag) {
		return addItemFlags(Collections.singletonList(itemFlag));
	}

	public ItemBuilder setUnbreakable(final boolean state) {

		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;

		itemMeta.setUnbreakable(state);
		return this;
	}

	public ItemBuilder setGlowing() {
		Enchantment enchantment;

		if (itemStack.getType() == Material.BOW) {
			enchantment = Enchantment.LURE;
		} else {
			enchantment = Enchantment.ARROW_INFINITE;
		}

		addEnchantment(enchantment, 0, true);
		addItemFlag(ItemFlag.HIDE_ENCHANTS);

		return this;
	}

	public ItemBuilder setSkullOwner(final OfflinePlayer offlinePlayer) {
		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;
		if (!(itemMeta instanceof SkullMeta))
			return this;

		((SkullMeta) itemMeta).setOwningPlayer(offlinePlayer);
		return this;
	}

	@Deprecated
	public ItemBuilder setSkullOwner(final String username) {
		if (!itemStack.hasItemMeta())
			return this;
		final ItemMeta itemMeta = itemStack.getItemMeta();
		assert itemMeta != null;
		if (!(itemMeta instanceof SkullMeta))
			return this;

		((SkullMeta) itemMeta).setOwner(username);
		return this;
	}

}