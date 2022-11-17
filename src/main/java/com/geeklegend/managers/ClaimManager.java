package com.geeklegend.managers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.claim.ClaimChunk;
import com.geeklegend.claim.ClaimMember;
import com.geeklegend.claim.ClaimMemberAccess;
import com.geeklegend.database.DatabaseConnection;
import com.geeklegend.guis.Gui;
import com.geeklegend.guis.ItemGui;
import com.geeklegend.utils.Cuboid;
import com.geeklegend.utils.DateUtils;
import com.geeklegend.utils.MessageUtils;

import eu.decentsoftware.holograms.api.DHAPI;
import net.milkbowl.vault.economy.Economy;

public class ClaimManager implements IManager {
	private final MPVClaim plugin = MPVClaim.getPlugin();
	private final FileConfiguration config = plugin.getConfig();
	private final DatabaseConnection databaseConnection = plugin.getDatabaseConnection();

	private final Map<String, String> invites = new HashMap<>();

	private final BukkitScheduler scheduler = Bukkit.getScheduler();

	private final List<Claim> claims = databaseConnection.getClaims();

	private final HologramManager hologramManager = plugin.getHologramManager();

	private final Map<Claim, Player> liquidAuthorizations = new HashMap<>();

	@Override
	public void register() {
		claims.stream().filter(Objects::nonNull).forEach(claim -> {
			deleteHeart(claim);
			spawnHeartAtClaimLoc(claim);
		});
	}

	private void spawnHeartAtClaimLoc(final Claim claim) {
		final Location heart = claim.getHeartLocation();
		final Location heartTop = new Location(heart.getWorld(), heart.getBlockX(), heart.getBlockY() + 3,
				heart.getBlockZ());

		hologramManager.createHologram(claim, heartTop,
				MessageUtils.getTranslatedList(config.getStringList("claim.heart.hologram"), "%claim_kw%",
						String.valueOf(claim.getKw()), "%claim_capacity%", String.valueOf(claim.getKwCapacity()),
						"%claim_uptake%",
						String.valueOf(config.getInt("claim.levels." + claim.getLevel() + ".kw.uptake"))));

		Objects.requireNonNull(heart.getWorld()).spawnEntity(heart, EntityType.ENDER_CRYSTAL);
	}

	private void spawnHeartAtLoc(final Location location, final Claim claim) {
		final World world = location.getWorld();
		final int lbx = location.getBlockX();
		final int lby = location.getBlockY();
		final int lbz = location.getBlockZ();
		final Location heartLocation = new Location(world, lbx + 0.5, world.getHighestBlockYAt(location) + 1,
				lbz + 0.5);
		final Location heartLocationTop = new Location(world, lbx + 0.5, lby + 3, lbz + 0.5);

		claim.setHeartLocation(heartLocation);

		hologramManager.createHologram(claim, heartLocationTop,
				MessageUtils.getTranslatedList(config.getStringList("claim.heart.hologram"), "%claim_kw%",
						String.valueOf(claim.getKw()), "%claim_capacity%", String.valueOf(claim.getKwCapacity()),
						"%claim_uptake%",
						String.valueOf(config.getInt("claim.levels." + claim.getLevel() + ".kw.uptake"))));

		Objects.requireNonNull(heartLocation.getWorld()).spawnEntity(heartLocation, EntityType.ENDER_CRYSTAL);
	}

	public void deleteHeart(final Claim claim) {
		for (Entity entity : claim.getHeartLocation().getChunk().getEntities()) {
			if (entity != null && entity.getType().equals(EntityType.ENDER_CRYSTAL)) {
				entity.remove();
			}
		}
	}

	public void onDisable() {
		claims.stream().filter(Objects::nonNull).forEach(claim -> deleteHeart(claim));
	}

	public boolean isNextToClaim(final Player player) {
		final Location location = player.getLocation();
		final UUID uuid = player.getUniqueId();
		final Location locSupX = new Location(location.getWorld(), location.getX() + 16, location.getY(),
				location.getZ());
		final Location locLessX = new Location(location.getWorld(), location.getX() - 16, location.getY(),
				location.getZ());
		final Location locSupZ = new Location(location.getWorld(), location.getX(), location.getY(),
				location.getZ() + 16);
		final Location locLessZ = new Location(location.getWorld(), location.getX(), location.getY(),
				location.getZ() - 16);
		final Claim claimSupX = getClaimByLoc(locSupX);
		final Claim claimLessX = getClaimByLoc(locLessX);
		final Claim claimSupZ = getClaimByLoc(locSupZ);
		final Claim claimLessZ = getClaimByLoc(locLessZ);
		final Claim current = getClaimByLoc(location);

		if (current == null) {
			return claimSupX != null && claimSupX.getOwner().equals(uuid)
					|| claimLessX != null && claimLessX.getOwner().equals(uuid)
					|| claimSupZ != null && claimSupZ.getOwner().equals(uuid)
					|| claimLessZ != null && claimLessZ.getOwner().equals(uuid);
		}
		return false;
	}

	public void claim(final Player player) {
		final Claim claim = getClaim(player.getUniqueId());
		final Location location = player.getLocation();

		if (claim == null && !isNextToClaim(player)) {
			if (isInClaim(player)) {
				MessageUtils.sendMsgFromConfig(player, "claim.already.claimed");
				return;
			}

			final int startingPrice = config.getInt("claim.levels.1.price");
			final boolean haveEnough = plugin.getEconomy().getBalance(player) >= startingPrice;

			if (!haveEnough) {
				MessageUtils.sendMsgFromConfig(player, "claim.not-enough", "%price%", String.valueOf(startingPrice));
				return;
			}

			if (player.isFlying()) {
				MessageUtils.sendMsgFromConfig(player, "claim.in-fly");
				return;
			}

			final LinkedList<ClaimChunk> chunks = new LinkedList<>();
			final Cuboid chunkCuboid = normalize(location);
			final Claim newClaim = new Claim(player.getUniqueId(), player.getName(), 1,
					config.getInt("claim.levels.1.claims-available"), config.getInt("claim.levels.1.kw.capacity"),
					config.getInt("claim.levels.1.kw.capacity"), true, false, DateUtils.getNextDay(1), chunks);
			final Location chunkHeart = chunkCuboid.getCenter();
			final int chx = chunkHeart.getBlockX();
			final int chz = chunkHeart.getBlockZ();

			chunkHeart.setX(chx);
			chunkHeart.setY(location.getWorld().getHighestBlockYAt(chx, chz) + 1);
			chunkHeart.setZ(chz);

			chunks.add(new ClaimChunk(claim, chunkCuboid));

			spawnHeartAtLoc(chunkHeart, newClaim);

			claims.add(newClaim);

			scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.createClaim(newClaim));

			MessageUtils.sendMsgFromConfig(player, "claim.created");
		} else {
			final int claimUses = claim.getClaimUses();

			if (claimUses <= 0) {
				MessageUtils.sendMsgFromConfig(player, "claim.not-available");
				return;
			}
			if (!isNextToClaim(player)) {
				MessageUtils.sendMsgFromConfig(player, "claim.not-next-to-claim");
				return;
			}
			claim.setClaimUses(claimUses - 1);

			final LinkedList<ClaimChunk> chunks = claim.getChunks();
			final Cuboid chunkCuboid = normalize(location);
			final ClaimChunk chunk = new ClaimChunk(claim, chunkCuboid);

			chunks.add(chunk);

			scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.addChunkClaim(chunk, claim));

			MessageUtils.sendMsgFromConfig(player, "claim.created");
		}
	}

	public Cuboid normalize(final Location location) {
		int xStart = (int) Math.floor(location.getX());
		int zStart = (int) Math.floor(location.getZ());

		int modulo = Math.floorMod(xStart, 16);

		if (modulo <= 16) {
			xStart -= modulo;
		}

		modulo = Math.floorMod(zStart, 16);
		if (modulo <= 16) {
			zStart -= modulo;
		}

		final Chunk chunk = location.getWorld().getChunkAt(xStart, zStart);
		final Cuboid boundingChunk = new Cuboid(chunk.getWorld(), chunk.getX(), chunk.getWorld().getMinHeight(),
				chunk.getZ(), chunk.getX() + 15, chunk.getWorld().getMaxHeight(), chunk.getZ() + 15);
		return boundingChunk;
	}

	public void refillHeart(final Player player, final Claim claim) {
		final ClaimMember claimMember = claim.getMember(player);

		if (claimMember != null && !claimMember.isCanBuyKW()) {
			MessageUtils.sendMsgFromConfig(player, "claim.heart.cant-buy");
			return;
		}

		final float kw = claim.getKw();

		if (kw >= claim.getKwCapacity()) {
			MessageUtils.sendMsgFromConfig(player, "claim.heart.full");
			return;
		}

		final Economy economy = plugin.getEconomy();
		final double balance = economy.getBalance(player);
		final int refillPrice = config.getInt("claim.heart.refill.price");

		if (balance < refillPrice) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-enough", "%price%", String.valueOf(refillPrice));
			return;
		}

		economy.withdrawPlayer(player, refillPrice);

		final float refillGiven = config.getInt("claim.heart.refill.given");

		claim.setKw(kw + refillGiven);

		updateHeartHolo(claim);
		updateMainGui(claim);

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDatabaseConnection().updateClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.heart.refill", "%claim_refill_given%",
				String.valueOf(refillGiven), "%claim_refill_price%", config.getString("claim.heart.refill.price"));
	}

	public void adminRefillHeart(final Player player, final String targetName, int amount) {
		final PermissionManager permissionManager = plugin.getPermissionManager();
		if (!permissionManager.isGranted(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.bypass.no-permission");
			return;
		}

		if (amount <= 0) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.heart.min-amount");
			return;
		}

		final Claim claim = getClaim(targetName);

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.no-claim", "%target_name%", targetName);
			return;
		}

		final String ownerName = claim.getOwnerName();

		final float kw = claim.getKw();
		final float kwCapacity = claim.getKwCapacity();

		if (amount > kw || amount >= kwCapacity) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.heart.limit-reached");
			return;
		}

		if (kw >= kwCapacity) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.heart.full", "%target_name%", ownerName);
			return;
		}

		claim.setKw(kw + amount);

		updateHeartHolo(claim);
		updateMainGui(claim);

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDatabaseConnection().updateClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.admin.heart.refill", "%target_name%", ownerName,
				"%claim_refill_given%", String.valueOf(amount), "%claim_refill_price%",
				config.getString("claim.heart.refill.price"));
	}

	public void invite(final Player sender, final String targetName) {
		final Player targetPlayer = Bukkit.getServer().getPlayer(targetName);
		final Claim claim = getClaim(sender.getUniqueId());

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(sender, "claim.invitation.no-claim");
			return;
		}
		if (targetPlayer == null) {
			MessageUtils.sendMsgFromConfig(sender, "claim.invitation.offline", "%target_name%", targetName);
			return;
		}
		if (targetPlayer == sender) {
			MessageUtils.sendMsgFromConfig(sender, "claim.invitation.cant-self");
			return;
		}

		if (!claim.isOwner(sender)) {
			MessageUtils.sendMsgFromConfig(sender, "claim.not-owner");
			return;
		}

		final Claim targetClaim = getClaim(targetPlayer.getUniqueId());

		if (targetClaim != null) {
			MessageUtils.sendMsgFromConfig(sender, "claim.invitation.have-claim", "%target_name%",
					targetPlayer.getName());
			return;
		}

		final String senderName = sender.getName();
		final String invitedName = invites.get(senderName);

		if (invitedName != null && invitedName.equalsIgnoreCase(targetName)) {
			MessageUtils.sendMsgFromConfig(sender, "claim.invitation.already", "%target_name%", targetName);
			return;
		}

		invites.put(senderName, targetName);

		MessageUtils.sendMsgFromConfig(sender, "claim.invitation.sended.sender", "%target_name%", targetName);
		MessageUtils.sendCompMsgFromConfig(targetPlayer, "claim.invitation.sended.target",
				"/claim accept " + senderName, "%sender_name%", sender.getName());

		scheduler.runTaskLater(plugin, () -> {
			if (claim.isMember(targetPlayer))
				return;
			MessageUtils.sendMsgFromConfig(sender, "claim.invitation.expired.sender", "%target_name%",
					targetPlayer.getName());
			MessageUtils.sendMsgFromConfig(targetPlayer, "claim.invitation.expired.target", "%sender_name%",
					sender.getName());
			invites.remove(senderName, targetName);
		}, 1200 * config.getInt("claim.invitation.expiration"));
	}

	public void accept(final String senderName, final Player target) {
		final String invitedName = invites.get(senderName);
		final String targetName = target.getName();

		if (invitedName == null || !invitedName.equalsIgnoreCase(targetName)) {
			MessageUtils.sendMsgFromConfig(target, "claim.invitation.dont-have", "%sender_name%", senderName);
			return;
		}

		final Claim claim = getClaim(senderName);
		if (claim == null) {
			MessageUtils.sendMsgFromConfig(target, "claim.invitation.dont-exist", "%sender_name%", senderName);
			return;
		}

		final List<ClaimMember> members = claim.getMembers();
		if (claim.isMember(target)) {
			invites.remove(senderName, targetName);
			MessageUtils.sendMsgFromConfig(target, "claim.invitation.dont-exist", "%sender_name%", senderName);
			return;
		}

		final UUID targetUUID = target.getUniqueId();
		final boolean canOpenHeart = config.getBoolean("claim.member.default.canOpenHeart");
		final boolean canBuild = config.getBoolean("claim.member.default.canBuild");
		final boolean canBuyKW = config.getBoolean("claim.member.default.canBuyKW");
		final ClaimMember claimMember = new ClaimMember(targetName, targetUUID, canOpenHeart, canBuild, canBuyKW);

		invites.remove(senderName, targetName);

		members.add(claimMember);
		claim.setMembers(members);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.addClaimMember(claim.getOwner(), claimMember));

		members.stream().filter(member -> !member.getUuid().equals(targetUUID)).forEach(member -> {
			final Player memberPlayer = Bukkit.getPlayer(member.getUuid());
			if (memberPlayer == null) {
				return;
			}
			MessageUtils.sendMsgFromConfig(memberPlayer, "claim.invitation.accepted.member", "%target_name%",
					targetName);
		});

		MessageUtils.sendMsgFromConfig(target, "claim.invitation.accepted.target", "%sender_name%", senderName);

		final Player senderPlayer = Bukkit.getPlayer(senderName);
		if (senderPlayer == null) {
			return;
		}
		MessageUtils.sendMsgFromConfig(senderPlayer, "claim.invitation.accepted.member", "%target_name%", targetName);
	}

	public void decline(final String senderName, final Player target) {
		final String invitedName = invites.get(senderName);
		final String targetName = target.getName();

		if (invitedName == null || !invitedName.equalsIgnoreCase(targetName)) {
			MessageUtils.sendMsgFromConfig(target, "claim.invitation.dont-have", "%sender_name%", senderName);
			return;
		}

		final Claim claim = getClaim(senderName);
		if (claim == null) {
			MessageUtils.sendMsgFromConfig(target, "claim.invitation.dont-exist", "%sender_name%", senderName);
			return;
		}

		final List<ClaimMember> members = claim.getMembers();
		boolean isAlreadyMember = members.stream()
				.anyMatch(member -> member.getUsername().equalsIgnoreCase(senderName));
		if (isAlreadyMember) {
			MessageUtils.sendMsgFromConfig(target, "claim.invitation.dont-exist", "%sender_name%", senderName);
			return;
		}

		invites.remove(senderName, targetName);

		MessageUtils.sendMsgFromConfig(target, "claim.invitation.declined.target", "%sender_name%", senderName);

		final Player senderPlayer = Bukkit.getPlayer(senderName);
		if (senderPlayer == null) {
			return;
		}
		MessageUtils.sendMsgFromConfig(senderPlayer, "claim.invitation.declined.member", "%target_name%", targetName);
	}

	public void moveHeart(final Player player) {
		final Location location = player.getLocation();
		final Claim claim = getClaimByLoc(location);

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-inside");
			return;
		}

		if (!claim.isOwner(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-owner");
			return;
		}

		if (player.isFlying()) {
			MessageUtils.sendMsgFromConfig(player, "claim.in-fly");
			return;
		}

		deleteHeart(claim);
		spawnHeartAtLoc(location, claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.updateClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.heart.updated");
	}

	public void adminMoveHeart(final Player player) {
		final PermissionManager permissionManager = plugin.getPermissionManager();
		if (!permissionManager.isGranted(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.bypass.no-permission");
			return;
		}

		final Location location = player.getLocation();
		final Claim claim = getClaimByLoc(location);

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.not-inside");
			return;
		}

		if (player.isFlying()) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.in-fly");
			return;
		}

		deleteHeart(claim);
		spawnHeartAtLoc(location, claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.updateClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.admin.heart.updated");
	}

	public void unclaim(final Player player) {
		final Location location = player.getLocation();
		final Claim claim = getClaimByLoc(location);

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-inside");
			return;
		}

		if (!claim.isOwner(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-owner");
			return;
		}

		final ClaimChunk chunk = claim.getChunkAt(location);

		if (chunk == null)
			return;

		if (claim.getChunks().size() <= 1) {
			MessageUtils.sendMsgFromConfig(player, "claim.unclaim.disband");
			return;
		}

		if (chunk.getChunk().contains(claim.getHeartLocation())) {
			MessageUtils.sendMsgFromConfig(player, "claim.unclaim.move-heart");
			return;
		}

		DHAPI.removeHologram(claim.getOwnerName());

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.deleteChunkClaim(chunk, claim));

		MessageUtils.sendMsgFromConfig(player, "claim.unclaim.unclaimed");
	}

	public void adminUnclaim(final Player player) {
		final PermissionManager permissionManager = plugin.getPermissionManager();
		if (!permissionManager.isGranted(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.bypass.no-permission");
			return;
		}

		final Location location = player.getLocation();
		final Claim claim = getClaimByLoc(location);

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.not-inside");
			return;
		}

		final ClaimChunk chunk = claim.getChunkAt(location);

		if (chunk == null)
			return;

		final String ownerName = claim.getOwnerName();

		if (claim.getChunks().size() <= 1) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.unclaim.disband", "%target_name%", ownerName);
			return;
		}

		if (chunk.getChunk().contains(claim.getHeartLocation())) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.unclaim.move-heart", "%target_name%", ownerName);
			return;
		}

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.deleteChunkClaim(chunk, claim));

		MessageUtils.sendMsgFromConfig(player, "claim.admin.unclaim.unclaimed", "%target_name%", ownerName);
	}

	public void disband(final Player player, final Claim claim) {
		final ClaimMember claimMember = claim.getMember(player);
		if (claimMember != null && !claimMember.isCanBuyKW()) {
			MessageUtils.sendMsgFromConfig(player, "claim.heart.cant-buy");
			return;
		}

		deleteHeart(claim);

		claims.remove(claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.deleteClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.deleted");
	}

	public void disband(final Claim claim) {
		final Player player = Bukkit.getPlayer(claim.getOwner());

		deleteHeart(claim);

		DHAPI.removeHologram(claim.getOwnerName());

		claims.remove(claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.deleteClaim(claim));

		if (player == null)
			return;

		MessageUtils.sendMsgFromConfig(player, "claim.deleted");
	}

	public void disband(final Player player) {
		final Claim claim = getClaim(player.getUniqueId());

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(player, "claim.no-claim");
			return;
		}

		if (!claim.isOwner(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-owner");
			return;
		}

		deleteHeart(claim);

		DHAPI.removeHologram(claim.getOwnerName());

		claims.remove(claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.deleteClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.deleted");
	}

	public void adminDisband(final Player player, final String targetName) {
		final PermissionManager permissionManager = plugin.getPermissionManager();
		if (!permissionManager.isGranted(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.bypass.no-permission");
			return;
		}

		final Claim claim = getClaim(targetName);

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.no-claim", "%target_name%", targetName);
			return;
		}

		deleteHeart(claim);

		DHAPI.removeHologram(claim.getOwnerName());

		claims.remove(claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.deleteClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.admin.deleted", "%target_name%", targetName);
	}

	public void levelUp(final Player player, final Claim claim) {
		final int currentLevel = claim.getLevel();
		final boolean isMaxLevelReached = claim.isMaxLevelReached();

		if (isMaxLevelReached) {
			MessageUtils.sendMsgFromConfig(player, "claim.heart.levelup.max-level-reached");
			return;
		}

		final Economy economy = plugin.getEconomy();
		final double balance = economy.getBalance(player);
		final int newLevel = currentLevel + 1;
		final int nextLevelPrice = config.getInt("claim.levels." + newLevel + ".price");

		if (balance < nextLevelPrice) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-enough", "%price%", String.valueOf(nextLevelPrice));
			return;
		}

		economy.withdrawPlayer(player, nextLevelPrice);

		final int newKwCapacity = config.getInt("claim.levels." + newLevel + ".kw.capacity");
		final int newClaimAvailable = config.getInt("claim.levels." + newLevel + ".claims-available");

		claim.setLevel(newLevel);
		claim.setKwCapacity(newKwCapacity);
		claim.setClaimUses(newClaimAvailable);

		updateHeartHolo(claim);
		updateMainGui(claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.updateClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.heart.levelup.new-level", "%claim_new_level%",
				String.valueOf(newLevel));
	}

	public void removeMember(final Player player, final String targetName) {
		final Claim claim = getClaim(player.getUniqueId());

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(player, "claim.no-claim");
			return;
		}

		if (targetName.equalsIgnoreCase(player.getName())) {
			MessageUtils.sendMsgFromConfig(player, "claim.member.cant-self");
			return;
		}

		if (!claim.isMember(targetName)) {
			MessageUtils.sendMsgFromConfig(player, "claim.member.no-member", "%target_name%", targetName);
			return;
		}

		final ClaimMember claimMember = claim.getMember(targetName);

		if (claimMember == null)
			return;

		final List<ClaimMember> members = claim.getMembers();
		members.remove(claimMember);
		claim.setMembers(members);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.deleteClaimMember(claim, claimMember));

		MessageUtils.sendMsgFromConfig(player, "claim.member.removed", "%target_name%", claimMember.getUsername());
	}

	public void toggleAccess(final Player whoClicked, final ClaimMember claimMember,
			final ClaimMemberAccess claimMemberAccess) {
		final Claim claim = getClaim(whoClicked.getUniqueId());

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(whoClicked, "claim.no-claim");
			return;
		}

		if (!claim.isOwner(whoClicked)) {
			MessageUtils.sendMsgFromConfig(whoClicked, "claim.not-owner");
			return;
		}

		switch (claimMemberAccess) {
		case CAN_OPEN_HEART:
			claimMember.setCanOpenHeart(!claimMember.isCanOpenHeart());
			break;
		case CAN_BUILD:
			claimMember.setCanBuild(!claimMember.isCanBuild());
			break;
		case CAN_BUY_KW:
			claimMember.setCanBuyKW(!claimMember.isCanBuyKW());
			break;
		default:
			break;
		}

		updateMemberSettingGui(claimMember);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.updateClaimMember(claim, claimMember));
	}

	public void enableVacation(final Player player, final Claim claim, final int days) {
		if (!claim.isOwner(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-owner");
			return;
		}
		if (claim.isInVacation()) {
			MessageUtils.sendMsgFromConfig(player, "claim.vacation.already");
			return;
		}
		claim.setVacationTime(DateUtils.getNextDay(days));
		claim.setInVacation(true);

		final List<ClaimMember> members = claim.getMembers();
		members.clear();

		claim.setMembers(members);

		updateVacationGui(claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.updateClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.vacation.enabled");
	}

	public void disableVacation(final Player player, final Claim claim) {
		if (!claim.isOwner(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.not-owner");
			return;
		}
		if (!claim.isInVacation()) {
			MessageUtils.sendMsgFromConfig(player, "claim.vacation.not-in-vacation");
			return;
		}
		claim.setVacationTime(0);
		claim.setInVacation(false);

		updateVacationGui(claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.updateClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.vacation.disabled");
	}

	public void adminDisableVacation(final Player player, final String targetName) {
		final PermissionManager permissionManager = plugin.getPermissionManager();
		if (!permissionManager.isGranted(player)) {
			MessageUtils.sendMsgFromConfig(player, "claim.bypass.no-permission");
			return;
		}

		final Claim claim = getClaim(targetName);

		if (claim == null) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.no-claim", "%target_name%", targetName);
			return;
		}
		final String ownerName = claim.getOwnerName();
		if (!claim.isInVacation()) {
			MessageUtils.sendMsgFromConfig(player, "claim.admin.vacation.not-in-vacation", "%target_name%", ownerName);
			return;
		}
		claim.setVacationTime(0);
		claim.setInVacation(false);

		updateVacationGui(claim);

		scheduler.runTaskAsynchronously(plugin, () -> databaseConnection.updateClaim(claim));

		MessageUtils.sendMsgFromConfig(player, "claim.admin.vacation.disabled", "%target_name%", ownerName);
	}

	private void updateMainGui(final Claim claim) {
		final Gui gui = plugin.getGuiManager().getGui(MessageUtils.getFromConfig("guis.claim.main.title"));

		if (gui == null) {
			return;
		}

		final List<ItemGui> updatedItems = Arrays.asList(
				new ItemGui(config.getInt("guis.claim.main.items.infos.slot"),
						Material.valueOf(
								config.getString("guis.claim.main.items.infos.icon").replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.main.items.infos.name"),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.main.items.infos.lores"),
								"%claim_level%", String.valueOf(claim.getLevel()), "%claim_kw%",
								String.valueOf(claim.getKw()), "%claim_capacity%",
								String.valueOf(claim.getKwCapacity()), "%claim_active%", claim.isActive() ? "✔" : "✖"),
						false),
				new ItemGui(config.getInt("guis.claim.main.items.refill.slot"),
						Material.valueOf(
								config.getString("guis.claim.main.items.refill.icon").replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.main.items.refill.name"),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.main.items.refill.lores"),
								"%claim_refill_price%", MessageUtils.getFromConfig("claim.heart.refill.price")),
						false),
				new ItemGui(config.getInt("guis.claim.main.items.levelup.slot"),
						Material.valueOf(
								config.getString("guis.claim.main.items.levelup.icon").replace(" ", "_").toUpperCase()),
						claim.isMaxLevelReached() ? "Niveau max atteint"
								: MessageUtils.getFromConfig("guis.claim.main.items.levelup.name", "%claim_level%",
										String.valueOf(claim.getLevel() + 1)),
						MessageUtils.getTranslatedList(
								claim.isMaxLevelReached()
										? Collections.singletonList("&7Vous avez atteint le niveau max.")
										: config.getStringList("guis.claim.main.items.levelup.lores"),
								"%claim_capacity%", String.valueOf(claim.getKwCapacity()), "%claim_uptake%",
								String.valueOf(config.getInt("claim.levels." + (claim.getLevel() + 1) + ".kw.uptake")),
								"%claim_claims_available%",
								String.valueOf(
										config.getInt("claim.levels." + (claim.getLevel() + 1) + ".claims-available")),
								"%claim_levelup_price%",
								String.valueOf(config.getInt("claim.levels." + (claim.getLevel() + 1) + ".price"))),
						false),
				new ItemGui(config.getInt("guis.claim.main.items.members.slot"),
						Material.valueOf(
								config.getString("guis.claim.main.items.members.icon").replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.main.items.members.name", "%claim_members%",
								String.valueOf(claim.getMembers().size())),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.main.items.members.lores")),
						false),
				new ItemGui(config.getInt("guis.claim.main.items.vacation.slot"),
						Material.valueOf(config.getString("guis.claim.main.items.vacation.icon").replace(" ", "_")
								.toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.main.items.vacation.name"),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.main.items.vacation.lores")),
						false));

		gui.updateInventory(updatedItems);
	}

	private void updateMemberSettingGui(final ClaimMember claimMember) {
		final Gui gui = plugin.getGuiManager().getGui(MessageUtils.getFromConfig("guis.claim.member-settings.title"));

		if (gui == null) {
			return;
		}

		final List<ItemGui> updatedItems = Arrays.asList(
				new ItemGui(config.getInt("guis.claim.member-settings.items.can-open-heart.slot"),
						Material.valueOf(config.getString("guis.claim.member-settings.items.can-open-heart.icon")
								.replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.member-settings.items.can-open-heart.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.member-settings.items.can-open-heart.lores"),
								"%can_open_heart%", claimMember.isCanOpenHeart() ? "✔" : "✖"),
						false),
				new ItemGui(config.getInt("guis.claim.member-settings.items.can-build.slot"),
						Material.valueOf(config.getString("guis.claim.member-settings.items.can-build.icon")
								.replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.member-settings.items.can-build.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.member-settings.items.can-build.lores"), "%can_build%",
								claimMember.isCanBuild() ? "✔" : "✖"),
						false),
				new ItemGui(config.getInt("guis.claim.member-settings.items.can-buy-kw.slot"),
						Material.valueOf(config.getString("guis.claim.member-settings.items.can-buy-kw.icon")
								.replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.member-settings.items.can-buy-kw.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.member-settings.items.can-buy-kw.lores"),
								"%can_buy_kw%", claimMember.isCanBuyKW() ? "✔" : "✖"),
						false));

		gui.updateInventory(updatedItems);
	}

	private void updateVacationGui(final Claim claim) {
		final Gui gui = plugin.getGuiManager().getGui(MessageUtils.getFromConfig("guis.claim.vacation.title"));

		if (gui == null) {
			return;
		}

		final List<ItemGui> updatedItems = Arrays.asList(
				new ItemGui(config.getInt("guis.claim.vacation.items.three-days.slot"),
						Material.valueOf(config.getString("guis.claim.vacation.items.three-days.icon").replace(" ", "_")
								.toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.vacation.items.three-days.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.vacation.items.three-days.lores")),
						false),
				new ItemGui(config.getInt("guis.claim.vacation.items.seven-days.slot"),
						Material.valueOf(config.getString("guis.claim.vacation.items.seven-days.icon").replace(" ", "_")
								.toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.vacation.items.seven-days.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.vacation.items.seven-days.lores")),
						false),
				new ItemGui(config.getInt("guis.claim.vacation.items.fifteen-days.slot"),
						Material.valueOf(config.getString("guis.claim.vacation.items.fifteen-days.icon")
								.replace(" ", "_").toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.vacation.items.fifteen-days.name"),
						MessageUtils.getTranslatedList(
								config.getStringList("guis.claim.vacation.items.fifteen-days.lores")),
						false),
				new ItemGui(config.getInt("guis.claim.vacation.items.disable.slot"),
						Material.valueOf(config.getString("guis.claim.vacation.items.disable.icon").replace(" ", "_")
								.toUpperCase()),
						MessageUtils.getFromConfig("guis.claim.vacation.items.disable.name"),
						MessageUtils.getTranslatedList(config.getStringList("guis.claim.vacation.items.disable.lores")),
						false));

		gui.updateInventory(updatedItems);
	}

	private void updateHeartHolo(final Claim claim) {
		plugin.getHologramManager().updateHologram(claim,
				MessageUtils.getTranslatedList(config.getStringList("claim.heart.hologram"), "%claim_kw%",
						String.valueOf(claim.getKw()), "%claim_capacity%", String.valueOf(claim.getKwCapacity()),
						"%claim_uptake%",
						String.valueOf(config.getInt("claim.levels." + claim.getLevel() + ".kw.uptake"))));
	}

	public List<Claim> getClaims() {
		return claims;
	}

	private boolean isInClaim(final Player player) {
		return claims.stream().anyMatch(claim -> claim.getChunks().stream()
				.filter(claimChunk -> claimChunk.getChunk().contains(player.getLocation())).findFirst().isPresent());
	}

	public boolean isCloseToAClaim(final Location location) {
		return claims.stream().anyMatch(claim -> claim != null && claim.isInAChunk(location));
	}

	public Claim getClaimByLoc(final Location location) {
		return claims.stream()
				.filter(claim -> claim != null && claim.getChunks().stream()
						.anyMatch(chunk -> chunk != null && chunk.getChunk().contains(location)))
				.findFirst().orElse(null);
	}

	public Claim getClaim(final Player player) {
		return claims.stream().filter(claim -> claim != null && claim.getOwner().equals(player.getUniqueId())
				|| claim.getMembers().contains(claim.getMember(player))).findFirst().orElse(null);
	}

	public Claim getClaim(final UUID uuid) {
		return claims.stream().filter(claim -> claim != null && claim.getOwner().equals(uuid)
				|| claim.getMembers().contains(claim.getMember(uuid))).findFirst().orElse(null);
	}

	public Claim getClaim(final String playerName) {
		final Player player = Bukkit.getPlayer(playerName);
		if (player == null)
			return null;
		return claims.stream().filter(claim -> claim != null && claim.getOwnerName().equalsIgnoreCase(playerName)
				|| claim.getMembers().contains(claim.getMember(player))).findFirst().orElse(null);
	}

	public Claim getOwnerClaim(final UUID uuid) {
		return claims.stream().filter(claim -> claim != null && claim.getOwner().equals(uuid)).findFirst().orElse(null);
	}

	public Map<Claim, Player> getLiquidAuthorizations() {
		return liquidAuthorizations;
	}

}
