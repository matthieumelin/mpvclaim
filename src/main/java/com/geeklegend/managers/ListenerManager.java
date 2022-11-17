package com.geeklegend.managers;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import com.geeklegend.MPVClaim;
import com.geeklegend.listeners.AsyncPlayerPreLoginListener;
import com.geeklegend.listeners.BlockBreakListener;
import com.geeklegend.listeners.BlockBurnListener;
import com.geeklegend.listeners.BlockPlaceListener;
import com.geeklegend.listeners.BlockSpreadListener;
import com.geeklegend.listeners.EntityDamageByEntityListener;
import com.geeklegend.listeners.EntityDamageListener;
import com.geeklegend.listeners.EntityExplodeListener;
import com.geeklegend.listeners.EntitySpawnListener;
import com.geeklegend.listeners.PlayerInteractEntityListener;
import com.geeklegend.listeners.PlayerInteractListener;
import com.geeklegend.listeners.PlayerJoinListener;
import com.geeklegend.listeners.PlayerMoveListener;

public class ListenerManager implements IManager {
    private final MPVClaim plugin = MPVClaim.getPlugin();
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    @Override
    public void register() {
        Arrays.asList(
        		new BlockSpreadListener(),
        		new BlockBurnListener(),
                new BlockPlaceListener(),
                new BlockBreakListener(),
                new EntityDamageByEntityListener(),
                new EntityDamageListener(),
                new EntityExplodeListener(),
                new EntitySpawnListener(),
                new PlayerMoveListener(),
                new PlayerInteractEntityListener(),
                new PlayerInteractListener(),
                new PlayerJoinListener(),
                new AsyncPlayerPreLoginListener()
        ).forEach(listener -> pluginManager.registerEvents(listener, plugin));
    }
    
    public void register(final Listener listener) {
    	pluginManager.registerEvents(listener, plugin);
    }
}
