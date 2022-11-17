package com.geeklegend.claim;

import java.util.UUID;

public class ClaimMember {
    private final String username;

    private final UUID uuid;
    
    private boolean canOpenHeart;
    private boolean canBuild;
    private boolean canBuyKW;

    public ClaimMember(final String username, final UUID uuid, final boolean canOpenHeart, final boolean canBuild, final boolean canBuyKW) {
        this.username = username;
        this.uuid = uuid;
        this.canOpenHeart = canOpenHeart;
        this.canBuild = canBuild;
        this.canBuyKW = canBuyKW;
    }
    
    public ClaimMember(final String username, final UUID uuid) {
    	this(username, uuid, false, false, false);
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }
    
    public boolean isCanOpenHeart() {
		return canOpenHeart;
	}
    
    public void setCanOpenHeart(boolean canOpenHeart) {
		this.canOpenHeart = canOpenHeart;
	}
    
    public boolean isCanBuild() {
		return canBuild;
	}
    
    public void setCanBuild(boolean canBuild) {
		this.canBuild = canBuild;
	}
    
    public boolean isCanBuyKW() {
		return canBuyKW;
	}
    
    public void setCanBuyKW(boolean canBuyKW) {
		this.canBuyKW = canBuyKW;
	}
}
