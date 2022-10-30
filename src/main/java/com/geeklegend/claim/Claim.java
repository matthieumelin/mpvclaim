package com.geeklegend.claim;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Claim {
    private final UUID owner;
    private int level;
    private float kw;
    private boolean isEmpty;
    private boolean isActive;

    private long refreshTime;

    private List<ClaimMember> members;

    public Claim(final UUID owner, int level, float kw, boolean isEmpty, boolean isActive, long refreshTime, final List<ClaimMember> members) {
        this.owner = owner;
        this.level = level;
        this.kw = kw;
        this.isEmpty = isEmpty;
        this.isActive = isActive;
        this.refreshTime = refreshTime;
        this.members = members;
    }

    public Claim(final UUID owner, int level, float kw, boolean isEmpty, boolean isActive, long refreshTime) {
        this.owner = owner;
        this.level = level;
        this.kw = kw;
        this.isEmpty = isEmpty;
        this.isActive = isActive;
        this.refreshTime = refreshTime;
        this.members = new ArrayList<>();
    }

    public UUID getOwner() {
        return owner;
    }

    public int getLevel() {
        return level;
    }

    public void upLevel(final int amount) {
        this.level += amount;
    }

    public float getKw() {
        return kw;
    }

    public void addKw(final float amount) {
        this.kw += amount;
    }

    public void subKw(final float amount) {
        this.kw -= amount;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public List<ClaimMember> getMembers() {
        return members;
    }

    public void setMembers(List<ClaimMember> members) {
        this.members = members;
    }
}
