package com.geeklegend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.claim.ClaimChunk;
import com.geeklegend.claim.ClaimMember;
import com.geeklegend.utils.Cuboid;
import com.geeklegend.utils.LocationUtils;

public class DatabaseConnection {
	private final Logger logger = MPVClaim.getPlugin().getLogger();
	private final DatabaseCredentials credentials;
	private Connection connection;

	public DatabaseConnection(final DatabaseCredentials credentials) {
		this.credentials = credentials;
		this.connect();
	}

	private void connect() {
		if (!isConnected()) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(credentials.toURI(), credentials.getUser(),
						credentials.getPassword());
				initTables();
				logger.info("Database connected!");
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if (isConnected()) {
			try {
				connection.close();
				logger.info("Database disconnected!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void initTables() {
		List.of("CREATE TABLE IF NOT EXISTS `claims` ( `id` VARCHAR(255) NOT NULL , `owner_name` VARCHAR(48) NOT NULL , `level` INT(11) NOT NULL , `claim_uses` INT(11) NOT NULL , `kw` FLOAT NOT NULL , `kw_capacity` FLOAT NOT NULL , `is_active` BOOLEAN NOT NULL , `is_in_vacation` BOOLEAN NOT NULL , `vacation_time` BIGINT(20) NOT NULL , `refresh_time` BIGINT(20) NOT NULL , `heart_location` VARCHAR(255) NOT NULL, UNIQUE `ID` (`id`) , UNIQUE `OWNER_NAME` (`owner_name`));",
				"CREATE TABLE IF NOT EXISTS `claims_members` ( `id` VARCHAR(255) NOT NULL , `username` VARCHAR(48) NOT NULL , `uuid` VARCHAR(255) NOT NULL , `can_open_heart` BOOLEAN NOT NULL , `can_build` BOOLEAN NOT NULL , `can_buy_kw` BOOLEAN NOT NULL , UNIQUE `USERNAME` (`username`), UNIQUE `UUID` (`uuid`));",
				"CREATE TABLE IF NOT EXISTS `claims_chunks` ( `id` VARCHAR(255) NOT NULL , `chunk_point1` VARCHAR(255) NOT NULL , `chunk_point2` VARCHAR(255) NOT NULL );")
				.forEach(query -> {
					try {
						final PreparedStatement preparedStatement = getConnection().prepareStatement(query);
						preparedStatement.execute();
						preparedStatement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
	}

	public void createClaim(final Claim claim) {
		final Location heart = claim.getHeartLocation();
		final String heartString = heart.getWorld().getName().toLowerCase() + "," + heart.getBlockX() + ","
				+ heart.getBlockY() + "," + heart.getBlockZ();

		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(
					"INSERT IGNORE INTO claims(`id`, `owner_name`, `level`, `claim_uses`, `kw`, `kw_capacity`, `is_active`, `is_in_vacation`, `vacation_time`, `refresh_time`, `heart_location`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			preparedStatement.setString(1, claim.getOwner().toString());
			preparedStatement.setString(2, claim.getOwnerName());
			preparedStatement.setInt(3, claim.getLevel());
			preparedStatement.setInt(4, claim.getClaimUses());
			preparedStatement.setFloat(5, claim.getKw());
			preparedStatement.setFloat(6, claim.getKwCapacity());
			preparedStatement.setBoolean(7, claim.isActive());
			preparedStatement.setBoolean(8, claim.isInVacation());
			preparedStatement.setLong(9, claim.getVacationTime());
			preparedStatement.setLong(10, claim.getRefreshTime());
			preparedStatement.setString(11, heartString);
			preparedStatement.executeUpdate();
			preparedStatement.close();

			final ClaimChunk newChunk = claim.getChunks().getFirst();
			final Location point1 = newChunk.getChunk().getPoint1();
			final Location point2 = newChunk.getChunk().getPoint2();
			final String chunkPoint1String = point1.getWorld().getName().toLowerCase() + "," + point1.getBlockX() + ","
					+ point1.getBlockY() + "," + point1.getBlockZ();
			final String chunkPoint2String = point2.getWorld().getName().toLowerCase() + "," + point2.getBlockX() + ","
					+ point2.getBlockY() + "," + point2.getBlockZ();

			final PreparedStatement chunkStatement = getConnection().prepareStatement(
					"INSERT IGNORE INTO claims_chunks(`id`, `chunk_point1`, `chunk_point2`) VALUES (?, ?, ?);");
			chunkStatement.setString(1, claim.getOwner().toString());
			chunkStatement.setString(2, chunkPoint1String);
			chunkStatement.setString(3, chunkPoint2String);
			chunkStatement.executeUpdate();
			chunkStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteClaim(final Claim claim) {
		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(
					"DELETE claims, claims_members, claims_chunks FROM claims LEFT JOIN claims_members ON claims.id = claims_members.id LEFT JOIN claims_chunks ON claims.id = claims_chunks.id WHERE claims.id = ?;");
			preparedStatement.setString(1, claim.getOwner().toString());
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateClaim(final Claim claim) {
		final Location heart = claim.getHeartLocation();
		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(
					"UPDATE `claims` SET `level` = ?, `claim_uses` = ?, `kw` = ?, `kw_capacity` = ?, `is_active` = ?, `is_in_vacation` = ?, `vacation_time` = ?, `refresh_time` = ?, `heart_location` = ? WHERE `id` = ?;");
			preparedStatement.setInt(1, claim.getLevel());
			preparedStatement.setInt(2, claim.getClaimUses());
			preparedStatement.setFloat(3, claim.getKw());
			preparedStatement.setFloat(4, claim.getKwCapacity());
			preparedStatement.setBoolean(5, claim.isActive());
			preparedStatement.setBoolean(6, claim.isInVacation());
			preparedStatement.setLong(7, claim.getVacationTime());
			preparedStatement.setLong(8, claim.getRefreshTime());
			preparedStatement.setString(9, Objects.requireNonNull(heart.getWorld()).getName().toLowerCase() + ","
					+ heart.getBlockX() + "," + heart.getBlockY() + "," + heart.getBlockZ());
			preparedStatement.setString(10, claim.getOwner().toString());
			preparedStatement.executeUpdate();
			preparedStatement.close();

			final PreparedStatement chunkStatement = getConnection().prepareStatement(
					"UPDATE `claims_chunks` SET `chunk_point1` = ?, `chunk_point2` = ? WHERE `id` = ?");
			chunkStatement.setString(1, claim.getOwner().toString());

			for (ClaimChunk chunk : claim.getChunks()) {
				final Location point1 = chunk.getChunk().getPoint1();
				final Location point2 = chunk.getChunk().getPoint2();
				final String chunkPoint1String = point1.getWorld().getName().toLowerCase() + "," + point1.getBlockX()
						+ "," + point1.getBlockY() + "," + point1.getBlockZ();
				final String chunkPoint2String = point2.getWorld().getName().toLowerCase() + "," + point2.getBlockX()
						+ "," + point2.getBlockY() + "," + point2.getBlockZ();

				chunkStatement.setString(2, chunkPoint1String);
				chunkStatement.setString(3, chunkPoint2String);
			}
			chunkStatement.executeUpdate();
			chunkStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addClaimMember(final UUID id, final ClaimMember claimMember) {
		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(
					"INSERT IGNORE INTO claims_members(`id`, `username`, `uuid`, `can_open_heart`, `can_build`, `can_buy_kw`) VALUES (?, ?, ?, ?,?,?);");
			preparedStatement.setString(1, id.toString());
			preparedStatement.setString(2, claimMember.getUsername());
			preparedStatement.setString(3, claimMember.getUuid().toString());
			preparedStatement.setBoolean(4, claimMember.isCanOpenHeart());
			preparedStatement.setBoolean(5, claimMember.isCanBuild());
			preparedStatement.setBoolean(6, claimMember.isCanBuyKW());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateClaimMember(final Claim claim, final ClaimMember claimMember) {
		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(
					"UPDATE `claims_members` SET `username` = ?, `uuid` = ?, `can_open_heart` = ?, `can_build` = ?, `can_buy_kw` = ? WHERE `id` = ?;");
			preparedStatement.setString(1, claimMember.getUsername());
			preparedStatement.setString(2, claimMember.getUuid().toString());
			preparedStatement.setBoolean(3, claimMember.isCanOpenHeart());
			preparedStatement.setBoolean(4, claimMember.isCanBuild());
			preparedStatement.setBoolean(5, claimMember.isCanBuyKW());
			preparedStatement.setString(6, claim.getOwner().toString());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteClaimMember(final Claim claim, final ClaimMember claimMember) {
		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(
					"DELETE FROM claims_members WHERE uuid = ? AND id = ?;");
			preparedStatement.setString(1, claimMember.getUuid().toString());
			preparedStatement.setString(2, claim.getOwner().toString());
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteChunkClaim(final ClaimChunk chunk, final Claim claim) {
		final Location point1 = chunk.getChunk().getPoint1();
		final Location point2 = chunk.getChunk().getPoint2();
		final String chunkPoint1String = point1.getWorld().getName().toLowerCase() + "," + point1.getBlockX() + ","
				+ point1.getBlockY() + "," + point1.getBlockZ();
		final String chunkPoint2String = point2.getWorld().getName().toLowerCase() + "," + point2.getBlockX() + ","
				+ point2.getBlockY() + "," + point2.getBlockZ();
		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(
					"DELETE FROM claims_chunks WHERE chunk_point1 = ? AND chunk_point2 = ? AND id = ?;");
			preparedStatement.setString(1, chunkPoint1String);
			preparedStatement.setString(2, chunkPoint2String);
			preparedStatement.setString(3, claim.getOwner().toString());
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addChunkClaim(final ClaimChunk chunk, final Claim claim) {
		final Location point1 = chunk.getChunk().getPoint1();
		final Location point2 = chunk.getChunk().getPoint2();
		final String chunkPoint1String = point1.getWorld().getName().toLowerCase() + "," + point1.getBlockX() + ","
				+ point1.getBlockY() + "," + point1.getBlockZ();
		final String chunkPoint2String = point2.getWorld().getName().toLowerCase() + "," + point2.getBlockX() + ","
				+ point2.getBlockY() + "," + point2.getBlockZ();
		
		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(
					"INSERT IGNORE INTO claims_chunks(`id`, `chunk_point1`, `chunk_point2`) VALUES (?, ?, ?);");
			preparedStatement.setString(1, claim.getOwner().toString());
			preparedStatement.setString(2, chunkPoint1String);
			preparedStatement.setString(3, chunkPoint2String);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Claim> getClaims() {
		final List<Claim> claims = new ArrayList<>();
		try {
			final PreparedStatement preparedStatement = getConnection().prepareStatement(
					"SELECT claims.*, claims_members.username, claims_members.uuid, claims_members.can_open_heart, claims_members.can_build, claims_members.can_buy_kw, claims_chunks.chunk_point1, claims_chunks.chunk_point2 FROM claims LEFT JOIN claims_members ON claims_members.id = claims.id LEFT JOIN claims_chunks ON claims_chunks.id = claims.id;");
			final ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				final String stringifyOwner = resultSet.getString("id");
				final UUID owner = UUID.fromString(stringifyOwner);
				final String ownerName = resultSet.getString("owner_name");
				final int level = resultSet.getInt("level");
				final int claimUses = resultSet.getInt("claim_uses");
				final float kw = resultSet.getFloat("kw");
				final float kwCapacity = resultSet.getFloat("kw_capacity");
				final boolean isActive = resultSet.getBoolean("is_active");
				final boolean isInVacation = resultSet.getBoolean("is_in_vacation");
				final long vacationTime = resultSet.getLong("vacation_time");
				final long refreshTime = resultSet.getLong("refresh_time");
				final Location heart = LocationUtils.getParsedLocation(resultSet.getString("heart_location"));
				final Claim claim = new Claim(owner, ownerName, level, claimUses, kw, kwCapacity, isActive, isInVacation,
						vacationTime, refreshTime, heart);
				final List<ClaimMember> members = claim.getMembers();
				final LinkedList<ClaimChunk> chunks = claim.getChunks();

				if (resultSet.getString("username") != null && resultSet.getString("uuid") != null) {
					final String username = resultSet.getString("username");
					final UUID uuid = UUID.fromString(resultSet.getString("uuid"));
					final boolean canOpenHeart = resultSet.getBoolean("can_open_heart");
					final boolean canBuild = resultSet.getBoolean("can_build");
					final boolean canBuyKW = resultSet.getBoolean("can_buy_kw");

					members.add(new ClaimMember(username, uuid, canOpenHeart, canBuild, canBuyKW));
				}

				if (resultSet.getString("chunk_point1") != null && resultSet.getString("chunk_point2") != null) {
					final Location chunkPoint1 = LocationUtils.getParsedLocation(resultSet.getString("chunk_point1"));
					final Location chunkPoint2 = LocationUtils.getParsedLocation(resultSet.getString("chunk_point2"));

					chunks.add(new ClaimChunk(claim, new Cuboid(chunkPoint1, chunkPoint2)));
				}

				claims.add(claim);
			}
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return claims;
	}

	public Connection getConnection() {
		return connection;
	}

	public boolean isConnected() {
		return connection != null;
	}
}
