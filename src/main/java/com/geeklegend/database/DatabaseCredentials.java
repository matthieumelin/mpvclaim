package com.geeklegend.database;

public class DatabaseCredentials {
    private final String host;
    private final String user;
    private final String password;
    private final String database;
    private final int port;

    public DatabaseCredentials(String host, String user, String password, String database, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    public String toURI() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false";
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public int getPort() {
        return port;
    }
}
