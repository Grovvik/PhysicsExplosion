package org.grovv.tnt;

import org.bukkit.plugin.java.JavaPlugin;
import org.grovv.tnt.listeners.ExplosionListener;

public class ExplosionPlugin extends JavaPlugin {

    private Settings settings;

    @Override
    public void onEnable() {
        settings = new Settings(this);
        new ExplosionListener(this);
    }

    public Settings getSettings() {
        return settings;
    }    

}