package com.geeklegend.managers;

import com.geeklegend.MPVClaim;
import com.geeklegend.listeners.BlockPlaceListener;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class ListenerManager implements IManager {
    @Override
    public void register() {
        Arrays.asList(
                new BlockPlaceListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, MPVClaim.getPlugin()));
    }
}
