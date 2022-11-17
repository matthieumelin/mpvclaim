package com.geeklegend.claim.tasks;

import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.utils.DateUtils;

public class ClaimTask extends Task {
	private final MPVClaim plugin = MPVClaim.getPlugin();

	@Override
	public void run() {
		final ClaimManager claimManager = plugin.getClaimManager();
		final List<Claim> claims = claimManager.getClaims();
		final Date currentDate = DateUtils.getCurrentDate();

		claims.stream().filter(claim -> claim != null && claim.isActive()).forEach(claim -> {
			final Date upTakeDate = DateUtils.getDate(claim.getRefreshTime());

			if (DateUtils.isNextDay(currentDate, upTakeDate)) {
				if (claim.isInVacation()) {
					final Date vacationDate = DateUtils.getDate(claim.getVacationTime());
					
					if (vacationDate.equals(currentDate)) {
						claim.setInVacation(false);
						claim.setVacationTime(0);
					}
				}

				if (claim.getKw() <= 0) {
					claimManager.disband(claim);
					Bukkit.getScheduler().runTaskAsynchronously(plugin,
							() -> plugin.getDatabaseConnection().deleteClaim(claim));
				} else {
					final int upTakeAmount = plugin.getConfig()
							.getInt("claim.levels." + claim.getLevel() + ".kw.uptake");

					claim.setKw(claim.getKw() - upTakeAmount);
					claim.setRefreshTime(DateUtils.getNextDay(1));
				}
				
				Bukkit.getScheduler().runTaskAsynchronously(plugin,
						() -> plugin.getDatabaseConnection().updateClaim(claim));
			}
		});
	}
}
