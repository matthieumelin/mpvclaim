package com.geeklegend.commands;

import com.geeklegend.MPVClaim;
import com.geeklegend.managers.ClaimManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof final Player player)) return false;
        final ClaimManager claimManager = MPVClaim.getPlugin().getClaimManager();

        if (args.length == 0) {
            claimManager.create(player);
        }
        return false;
    }
}
