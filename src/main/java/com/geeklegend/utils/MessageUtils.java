package com.geeklegend.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.geeklegend.MPVClaim;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageUtils {
	private static FileConfiguration config = MPVClaim.getPlugin().getConfig();

	public static String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', HexColor.format(config.getString("messages.prefix")));
	}

	public static void sendMessage(final Player player, final String message) {
		player.sendMessage(message);
	}

	public static void sendTitleFromConfig(final Player player, final String keyTitle, final String keySubTitle,
			final String placeholder, final String replaced) {
		player.sendTitle(getMsgFromConfig(keyTitle, placeholder, replaced),
				getMsgFromConfig(keySubTitle, placeholder, replaced), 20, 20, 20);
	}

	public static void sendMsgFromConfig(final Player player, final String key) {
		player.sendMessage(getMsgFromConfig(key));
	}

	public static void sendMsgFromConfig(final Player player, final String key, final String placeholder,
			final String replaced) {
		player.sendMessage(getMsgFromConfig(key, placeholder, replaced));
	}

	public static void sendMsgFromConfig(final Player player, final String key, final String placeholder1,
			final String replaced1, final String placeholder2, final String replaced2) {
		player.sendMessage(getMsgFromConfig(key, placeholder1, replaced1, placeholder2, replaced2));
	}

	public static void sendMsgFromConfig(final Player player, final String key, final String placeholder1,
			final String replaced1, final String placeholder2, final String replaced2, final String placeholder3,
			final String replaced3) {
		player.sendMessage(
				getMsgFromConfig(key, placeholder1, replaced1, placeholder2, replaced2, placeholder3, replaced3));
	}

	public static void sendCompMsgFromConfig(final Player player, final String key, final String command) {
		TextComponent text = new TextComponent(getMsgFromConfig(key));
		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		player.spigot().sendMessage(text);
	}

	public static void sendCompMsgFromConfig(final Player player, final String key, final String command,
			final String placeholder, final String replaced) {
		TextComponent text = new TextComponent(getMsgFromConfig(key, placeholder, replaced));
		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		player.spigot().sendMessage(text);
	}

	public static String getMsgFromConfig(final String key) {
		return ChatColor.translateAlternateColorCodes('&',
				HexColor.format(config.getString("messages." + key).replace("%prefix%", getPrefix())));
	}

	public static String getMsgFromConfig(final String key, final String placeholder, final String replaced) {
		return ChatColor.translateAlternateColorCodes('&', HexColor.format(
				config.getString("messages." + key).replace("%prefix%", getPrefix()).replace(placeholder, replaced)));
	}

	public static String getMsgFromConfig(final String key, final String placeholder1, final String replaced1,
			final String placeholder2, final String replaced2) {
		return ChatColor.translateAlternateColorCodes('&', HexColor.format(config.getString("messages." + key)
				.replace("%prefix%", getPrefix()).replace(placeholder1, replaced1).replace(placeholder2, replaced2)));
	}

	public static String getMsgFromConfig(final String key, final String placeholder1, final String replaced1,
			final String placeholder2, final String replaced2, final String placeholder3, final String replaced3) {
		return ChatColor.translateAlternateColorCodes('&',
				HexColor.format(config.getString("messages." + key).replace("%prefix%", getPrefix())
						.replace(placeholder1, replaced1).replace(placeholder2, replaced2)
						.replace(placeholder3, replaced3)));
	}

	public static String getFromConfig(final String key) {
		return ChatColor.translateAlternateColorCodes('&', HexColor.format(config.getString(key)));
	}

	public static String getFromConfig(final String key, final String placeholder, final String replaced) {
		return ChatColor.translateAlternateColorCodes('&',
				HexColor.format(config.getString(key).replace(placeholder, replaced)));
	}

	public static List<String> getTranslatedList(final List<String> input) {
		final List<String> output = new ArrayList<>();
		input.forEach(line -> {
			output.add(ChatColor.translateAlternateColorCodes('&', HexColor.format(line)));
		});
		return output;
	}

	public static List<String> getTranslatedList(final List<String> input, final String placeholder,
			final String replaced) {
		final List<String> output = new ArrayList<>();
		input.forEach(line -> {
			output.add(
					ChatColor.translateAlternateColorCodes('&', HexColor.format(line.replace(placeholder, replaced))));
		});
		return output;
	}

	public static List<String> getTranslatedList(final List<String> input, final String placeholder,
			final String replaced, final String placeholder2, final String replaced2) {
		final List<String> output = new ArrayList<>();
		input.forEach(line -> {
			output.add(ChatColor.translateAlternateColorCodes('&',
					HexColor.format(line.replace(placeholder, replaced).replace(placeholder2, replaced2))));
		});
		return output;
	}

	public static List<String> getTranslatedList(final List<String> input, final String placeholder,
			final String replaced, final String placeholder2, final String replaced2, final String placeholder3,
			final String replaced3) {
		final List<String> output = new ArrayList<>();
		input.forEach(line -> {
			output.add(ChatColor.translateAlternateColorCodes('&', HexColor.format(line.replace(placeholder, replaced)
					.replace(placeholder2, replaced2).replace(placeholder3, replaced3))));
		});
		return output;
	}

	public static List<String> getTranslatedList(final List<String> input, final String placeholder,
			final String replaced, final String placeholder2, final String replaced2, final String placeholder3,
			final String replaced3, final String placeholder4, final String replaced4) {
		final List<String> output = new ArrayList<>();
		input.forEach(line -> {
			output.add(ChatColor.translateAlternateColorCodes('&',
					HexColor.format(line.replace(placeholder, replaced).replace(placeholder2, replaced2)
							.replace(placeholder3, replaced3).replace(placeholder4, replaced4))));
		});
		return output;
	}
}
