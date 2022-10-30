package com.geeklegend.claim;

import java.util.UUID;

public class ClaimMember {
    private final Claim claim;

    private final String username;

    private final UUID uuid;

    public ClaimMember(final Claim claim, final String username, final UUID uuid) {
        this.claim = claim;
        this.username = username;
        this.uuid = uuid;
    }

    public Claim getClaim() {
        return claim;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }
}
