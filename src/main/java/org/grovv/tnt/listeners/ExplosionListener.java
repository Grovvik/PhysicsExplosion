package org.grovv.tnt.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.grovv.tnt.ExplosionPlugin;
import org.grovv.tnt.Utils;

public class ExplosionListener implements Listener {

    private final Utils utils;

    public ExplosionListener(ExplosionPlugin plugin) {
        this.utils = new Utils(plugin.getSettings());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        utils.explode(event);
    }
}