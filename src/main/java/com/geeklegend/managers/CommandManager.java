package com.geeklegend.managers;

import org.bukkit.Bukkit;

import com.geeklegend.commands.MPVCommand;

public class CommandManager implements IManager {
    @Override
    public void register() {
        Bukkit.getPluginCommand("mpv").setExecutor(new MPVCommand());
    }
}
