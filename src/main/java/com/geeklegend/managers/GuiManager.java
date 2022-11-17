package com.geeklegend.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.geeklegend.MPVClaim;
import com.geeklegend.guis.ClaimMainGui;
import com.geeklegend.guis.ClaimMemberGui;
import com.geeklegend.guis.ClaimMemberSettingGui;
import com.geeklegend.guis.ClaimVacationGui;
import com.geeklegend.guis.Gui;
import com.geeklegend.utils.MessageUtils;

public class GuiManager implements IManager {
	private final FileConfiguration config = MPVClaim.getPlugin().getConfig();
	private final List<Gui> guis = new ArrayList<>();

	@Override
	public void register() {
		Arrays.asList(
				new ClaimMainGui(config.getInt("guis.claim.main.size"),
						MessageUtils.getFromConfig("guis.claim.main.title")),
				new ClaimMemberGui(config.getInt("guis.claim.members.size"),
						MessageUtils.getFromConfig("guis.claim.members.title")),
				new ClaimMemberSettingGui(config.getInt("guis.claim.member-settings.size"),
						MessageUtils.getFromConfig("guis.claim.member-settings.title")),
				new ClaimVacationGui(config.getInt("guis.claim.vacation.size"),
						MessageUtils.getFromConfig("guis.claim.vacation.title")))
				.forEach(gui -> guis.add(gui));
	}

	public void register(final Gui gui) {
		if (guis.contains(gui)) {
			return;
		}

		guis.add(gui);
	}

	public Gui getGui(final String title) {
		return guis.stream().filter(gui -> gui.getTitle().equalsIgnoreCase(title)).findFirst().orElse(null);
	}

	public List<Gui> getGuis() {
		return guis;
	}
}
