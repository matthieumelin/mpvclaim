package com.geeklegend.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.managers.ClaimManager;
import com.geeklegend.utils.DateUtils;
import com.geeklegend.utils.MessageUtils;

public class AsyncPlayerPreLoginListener implements Listener {

	@EventHandler
	public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
		final ClaimManager claimManager = MPVClaim.getPlugin().getClaimManager();
		final Claim claim = claimManager.getClaim(event.getUniqueId());

		if (claim == null)
			return;
		if (!claim.isInVacation())
			return;

		event.setLoginResult(Result.KICK_OTHER);
		event.setKickMessage(MessageUtils.getMsgFromConfig("claim.vacation.in-vacation", "%claim_vacation_time%",
				String.valueOf(DateUtils.getDays(claim.getVacationTime())+1)));
	}
}
