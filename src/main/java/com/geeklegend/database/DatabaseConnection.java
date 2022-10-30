package com.geeklegend.database;

import com.geeklegend.claim.Claim;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseConnection {
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
                connection = DriverManager.getConnection(credentials.toURI(), credentials.getUser(), credentials.getPassword());
                initTables();
                System.out.println("Database connected!");
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if (isConnected()) {
            try {
                connection.close();
                System.out.println("Database disconnected!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initTables() {
        List.of(
                "CREATE TABLE IF NOT EXISTS `mpvclaim`.`claims` ( `id` VARCHAR(255) NOT NULL , `level` INT(11) NOT NULL , `kw` FLOAT NOT NULL , `is_empty` BOOLEAN NOT NULL , `is_active` BOOLEAN NOT NULL , `refresh_time` INT(11) NOT NULL , UNIQUE `ID` (`id`));",
                "CREATE TABLE IF NOT EXISTS `mpvclaim`.`claims_members` ( `id` VARCHAR(255) NOT NULL , `username` VARCHAR(48) NOT NULL , `uuid` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`), UNIQUE `USERNAME` (`username`), UNIQUE `UUID` (`uuid`));"
        ).forEach(query -> {
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
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT IGNORE INTO claims(`id`, `level`, `kw`, `is_empty`, `is_active`, `refresh_time`) VALUES (?, ?, ?, ?, ?, ?);");
            preparedStatement.setString(1, claim.getOwner().toString());
            preparedStatement.setInt(2, claim.getLevel());
            preparedStatement.setFloat(3, claim.getKw());
            preparedStatement.setBoolean(4, claim.isEmpty());
            preparedStatement.setBoolean(5, claim.isActive());
            preparedStatement.setLong(6, claim.getRefreshTime());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClaim(final Claim claim) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE `claims` SET `level` = ?, `kw` = ?, `is_empty` = ? `is_active` = ? `refresh_time` = ? WHERE `id` = ?;");
            preparedStatement.setInt(1, claim.getLevel());
            preparedStatement.setFloat(2, claim.getKw());
            preparedStatement.setBoolean(3, claim.isEmpty());
            preparedStatement.setBoolean(4, claim.isActive());
            preparedStatement.setLong(5, claim.getRefreshTime());
            preparedStatement.setString(6, claim.getOwner().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Claim> getClaims() {
        final List<Claim> claims = new ArrayList<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM `claims`;");
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final UUID owner = UUID.fromString(resultSet.getString("id"));
                final int level = resultSet.getInt("level");
                final float kw = resultSet.getFloat("kw");
                final boolean isEmpty = resultSet.getBoolean("is_empty");
                final boolean isActive = resultSet.getBoolean("is_active");
                final long refreshTime = resultSet.getLong("refresh_time");

                claims.add(new Claim(owner, level, kw, isEmpty, isActive, refreshTime));
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
