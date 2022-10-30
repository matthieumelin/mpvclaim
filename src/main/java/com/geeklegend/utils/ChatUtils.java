package com.geeklegend.utils;

import com.geeklegend.MPVClaim;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ChatUtils {
    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', HexColor.format(MPVClaim.getPlugin().getConfig().getString("messages.prefix")));
    }

    public static void sendMsgFromConfig(final Player player, final String key) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MPVClaim.getPlugin().getConfig().getString("messages." + key))).replace("%prefix%", getPrefix()));
    }

    public static void sendMsgFromConfig(final Player player, final String key, final String placeholder, final String replaced) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MPVClaim.getPlugin().getConfig().getString("messages." + key)).replace("%prefix%", getPrefix()).replace(placeholder, replaced)));
    }
}
