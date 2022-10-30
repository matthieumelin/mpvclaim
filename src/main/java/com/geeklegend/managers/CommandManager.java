package com.geeklegend.managers;

import com.geeklegend.commands.ClaimCommand;
import org.bukkit.Bukkit;

import java.util.Objects;

public class CommandManager implements IManager {
    @Override
    public void register() {
        Objects.requireNonNull(Bukkit.getPluginCommand("claim")).setExecutor(new ClaimCommand());
    }
}
