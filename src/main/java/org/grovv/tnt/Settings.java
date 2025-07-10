package org.grovv.tnt;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Settings {

    private final JavaPlugin plugin;
    private File storageFile;
    private FileConfiguration storageConfig;

    public Settings(JavaPlugin plugin) {
        this.plugin = plugin;
        setupStorage();
    }

    private void setupStorage() {
        storageFile = new File(plugin.getDataFolder(), "settings.yml");

        if (!storageFile.exists()) {
            plugin.saveResource("settings.yml", false);
            storageConfig = new YamlConfiguration();
            storageConfig.options().copyDefaults(true);
        } else {
            storageConfig = YamlConfiguration.loadConfiguration(storageFile);
        }
    }

    public void saveStorage() {
        try {
            storageConfig.save(storageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadStorage() {
        storageConfig = YamlConfiguration.loadConfiguration(storageFile);
    }

    public FileConfiguration getStorage() {
        return storageConfig;
    }
}