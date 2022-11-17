package com.geeklegend.claim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.geeklegend.MPVClaim;

public class Claim {
	private final UUID owner;
	private final String ownerName;
	private int level;
	private int claimUses;
	private float kw;
	private float kwCapacity;
	private boolean isActive;
	private boolean isInVacation;
	private long vacationTime;
	private long refreshTime;
	private Location heartLocation;
	private List<ClaimMember> members;
	private LinkedList<ClaimChunk> chunks;
	private final FileConfiguration config = MPVClaim.getPlugin().getConfig();

	public Claim(final UUID owner, final String ownerName, int level, int claimUses, float kw, float kwCapacity,
			boolean isActive, boolean isInVacation, long vacationTime, long refreshTime, Location heartLocation,
			final List<ClaimMember> members, final LinkedList<ClaimChunk> chunks) {
		this.owner = owner;
		this.ownerName = ownerName;
		this.level = level;
		this.claimUses = claimUses;
		this.kw = kw;
		this.kwCapacity = kwCapacity;
		this.isActive = isActive;
		this.isInVacation = isInVacation;
		this.vacationTime = vacationTime;
		this.refreshTime = refreshTime;
		this.heartLocation = heartLocation;
		this.members = members;
		this.chunks = chunks;
	}

	public Claim(final UUID owner, final String ownerName, int level, int claimUses, float kw, float kwCapacity,
			boolean isActive, boolean isInVacation, long vacationTime, long refreshTime, Location heartLocation) {
		this(owner, ownerName, level, claimUses, kw, kwCapacity, isActive, isInVacation, vacationTime, refreshTime,
				heartLocation, new ArrayList<>(), new LinkedList<>());
	}

	public Claim(UUID owner, String ownerName, int level, int claimUses, float kw, float kwCapacity, boolean isActive,
			boolean isInVacation, long refreshTime, LinkedList<ClaimChunk> chunks) {
		this(owner, ownerName, level, claimUses, kw, kwCapacity, isActive, isInVacation, 0, refreshTime, null,
				new ArrayList<>(), chunks);
	}

	public UUID getOwner() {
		return owner;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getClaimUses() {
		return claimUses;
	}

	public void setClaimUses(int claimUses) {
		this.claimUses = claimUses;
	}

	public float getKw() {
		return kw;
	}

	public void setKw(float kw) {
		this.kw = kw;
	}

	public float getKwCapacity() {
		return kwCapacity;
	}

	public void setKwCapacity(float kwCapacity) {
		this.kwCapacity = kwCapacity;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean isInVacation() {
		return isInVacation;
	}
	
	public void setInVacation(boolean isInVacation) {
		this.isInVacation = isInVacation;
	}


	public long getVacationTime() {
		return vacationTime;
	}

	public void setVacationTime(long vacationTime) {
		this.vacationTime = vacationTime;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public Location getHeartLocation() {
		return heartLocation;
	}

	public void setHeartLocation(Location heartLocation) {
		this.heartLocation = heartLocation;
	}

	public List<ClaimMember> getMembers() {
		return members;
	}

	public void setMembers(List<ClaimMember> members) {
		this.members = members;
	}
	
	public LinkedList<ClaimChunk> getChunks() {
		return chunks;
	}

	public void setChunks(LinkedList<ClaimChunk> chunks) {
		this.chunks = chunks;
	}

	public boolean isMaxLevelReached() {
		return level >= config.getConfigurationSection("claim.levels").getKeys(false).size();
	}

	public Object getDataFromConfig(final String key) {
		return config.getConfigurationSection("claim." + level + "." + key);
	}

	public boolean isMember(final Player player) {
		return isOwner(player) || members.stream().anyMatch(member -> member.getUuid().equals(player.getUniqueId()));
	}

	public boolean isMember(final String playerName) {
		return members.stream().anyMatch(member -> member.getUsername().equalsIgnoreCase(playerName));
	}

	public boolean isMemberCanOpenHeart(final Player player) {
		return isOwner(player) || members.stream()
				.anyMatch(member -> member.getUuid().equals(player.getUniqueId()) && member.isCanOpenHeart());
	}

	public boolean isMemberCanBuild(final Player player) {
		return isOwner(player) || members.stream()
				.anyMatch(member -> member.getUuid().equals(player.getUniqueId()) && member.isCanBuild());
	}

	public boolean isMemberCanBuyKW(final Player player) {
		return isOwner(player) || members.stream()
				.anyMatch(member -> member.getUuid().equals(player.getUniqueId()) && member.isCanBuyKW());
	}

	public boolean isOwner(final Player player) {
		return owner.equals(player.getUniqueId());
	}

	public ClaimMember getMember(final Player player) {
		return members.stream().filter(member -> member.getUuid().equals(player.getUniqueId())).findFirst()
				.orElse(null);
	}
	
	public ClaimMember getMember(final UUID uuid) {
		return members.stream().filter(member -> member.getUuid().equals(uuid)).findFirst()
				.orElse(null);
	}

	public ClaimMember getMember(final String playerName) {
		return members.stream().filter(member -> member.getUsername().equalsIgnoreCase(playerName)).findFirst().orElse(null);
	}

	public ClaimChunk getChunkAt(final Location location) {
		return chunks.stream().filter(chunk -> chunk.getChunk().contains(location)).findFirst().orElse(null);
	}

	public boolean isInAChunk(final Location location) {
		return chunks.stream().anyMatch(chunk -> chunk.getChunk().contains(location));
	}
}
