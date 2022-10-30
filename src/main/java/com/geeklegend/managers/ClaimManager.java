package com.geeklegend.managers;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.database.DatabaseConnection;
import com.geeklegend.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ClaimManager {
    private final DatabaseConnection databaseConnection = MPVClaim.getPlugin().getDatabaseConnection();
    private final List<Claim> claims = databaseConnection.getClaims();

    public void create(final Player player) {
        final UUID uuid = player.getUniqueId();
        final boolean haveClaim = claims.stream().anyMatch(claim -> claim.getOwner().equals(uuid));

        if (haveClaim) {
            ChatUtils.sendMsgFromConfig(player, "claim.already");
            return;
        }

        final FileConfiguration config = MPVClaim.getPlugin().getConfig();
        final int startingPrice = config.getInt("claim.levels.1.price");
        final boolean haveEnough = MPVClaim.getPlugin().getEconomy().getBalance(player) >= startingPrice;

        if (!haveEnough) {
            ChatUtils.sendMsgFromConfig(player, "claim.not-enough", "%claim_price%", String.valueOf(startingPrice));
            return;
        }

        final Claim claim = new Claim(uuid, 1, config.getInt("claim.levels.1.kw.capacity"), false, true, System.currentTimeMillis());

        claims.add(claim);

        Bukkit.broadcastMessage("size: " + claims.size());

        databaseConnection.createClaim(claim);

        ChatUtils.sendMsgFromConfig(player, "claim.created");
    }

    public List<Claim> getClaims() {
        return claims;
    }
}
