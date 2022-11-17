package com.geeklegend.listeners;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.Claim;
import com.geeklegend.managers.ClaimManager;

public class EntityDamageByEntityListener implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity damaged = event.getEntity();
        
        if (damaged == null) return;
        
        final Entity damager = event.getDamager();
        final ClaimManager claimManager = MPVClaim.getPlugin().getClaimManager();
        final Claim claim = claimManager.getClaimByLoc(damaged.getLocation());

        if (damaged instanceof EnderCrystal) {

            if (!(damager instanceof Player)) {
                return;
            }

            if (claim == null) {
                return;
            }
            
            event.setCancelled(true);
        } else if (damaged instanceof Mob) {
            if (!(damager instanceof Player)) {
                return;
            }
            
            final Player damagerPlayer = (Player) damager;
            
            if (claim == null) {
            	return;
            }
            
            if (claim.isMember(damagerPlayer)) return;
            
            event.setCancelled(true);
        }
    }
}
