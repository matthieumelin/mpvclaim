package com.geeklegend.managers;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.geeklegend.MPVClaim;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class PermissionManager implements IManager {
	@Override
	public void register() {
	}

	public boolean isGranted(final Player player) {
		final User user = getUser(player.getUniqueId());
		if (user == null)
			return false;
		final boolean hasPermission = hasPermission(user,
				MPVClaim.getPlugin().getConfig().getString("claim.bypass-permission"));
		if (!hasPermission) {
			return false;
		}
		return true;
	}

	public boolean hasPermission(final User user, final String permission) {
		return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
	}

	public User getUser(final UUID uuid) {
		return LuckPermsProvider.get().getUserManager().getUser(uuid);
	}
}
