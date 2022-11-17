package com.geeklegend.commands;

import java.util.List;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.geeklegend.MPVClaim;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.managers.PermissionManager;
import com.geeklegend.utils.MessageUtils;

import net.luckperms.api.model.user.User;

public class MPVCommand implements CommandExecutor {
	private final MPVClaim plugin = MPVClaim.getPlugin();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof final Player player))
			return false;
		final ClaimManager claimManager = plugin.getClaimManager();

		if (args.length == 4) {
			if (args[0].equalsIgnoreCase("claim")) {
				if (args[1].equalsIgnoreCase("admin")) {
					if (args[2].equalsIgnoreCase("disband")) {
						claimManager.adminDisband(player, args[3]);
					} else if (args[2].equalsIgnoreCase("addkw")) {
						claimManager.adminRefillHeart(player, args[3], Integer.parseInt(args[4]));
					}
				}
			}
			return true;

		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("claim")) {
				if (args[1].equalsIgnoreCase("admin")) {
					if (args[2].equalsIgnoreCase("unclaim")) {
						claimManager.adminUnclaim(player);
					} else if (args[2].equalsIgnoreCase("heart")) {
						claimManager.adminMoveHeart(player);
					}
				} else if (args[1].equalsIgnoreCase("invite")) {
					claimManager.invite(player, args[2]);
				} else if (args[1].equalsIgnoreCase("accept")) {
					claimManager.accept(args[2], player);
				} else if (args[1].equalsIgnoreCase("remove")) {
					claimManager.removeMember(player, args[2]);
				}
			}
			return true;

		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("claim")) {
				if (args[1].equalsIgnoreCase("heart")) {
					claimManager.moveHeart(player);
				} else if (args[1].equalsIgnoreCase("disband")) {
					claimManager.disband(player);
				}
			}
			return true;
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("claim")) {
				claimManager.claim(player);
			} else if (args[0].equalsIgnoreCase("unclaim")) {
				claimManager.unclaim(player);
			}
			return true;
		} else {
			sendUsage(player);
		}
		return false;
	}

	private void sendUsage(final Player player) {
		final PermissionManager permissionManager = plugin.getPermissionManager();
		final User user = permissionManager.getUser(player.getUniqueId());
		if (user == null)
			return;
		final FileConfiguration config = plugin.getConfig();
		if (permissionManager.isGranted(player)) {
			final List<String> adminMessages = MessageUtils
					.getTranslatedList(config.getStringList("messages.commands.claim.usage.admin"));
			adminMessages.stream().filter(Objects::nonNull)
					.forEach(message -> MessageUtils.sendMessage(player, message));
		} else {
			final List<String> userMessages = MessageUtils
					.getTranslatedList(config.getStringList("messages.commands.claim.usage.user"));
			userMessages.stream().filter(Objects::nonNull)
					.forEach(message -> MessageUtils.sendMessage(player, message));
		}
	}
}
