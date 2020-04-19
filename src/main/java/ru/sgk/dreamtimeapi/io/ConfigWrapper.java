package ru.sgk.dreamtimeapi.io;


import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ConfigWrapper {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    private final String folderName;
    private final String fileName;
    private String header;

    public ConfigWrapper(JavaPlugin plugin, String folderName, String fileName) {
        this.plugin = plugin;
        this.folderName = folderName;
        this.fileName = fileName;
    }
    public void setDefaults(Map<String, Object> map)
    {
        config.addDefaults(map);
        loadConfig(this.header);
    }
    public FileConfiguration getConfig()
    {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void createNewFile(final String header)
    {
        this.header = header;
        reloadConfig();
        saveConfig();
        loadConfig(this.header);
    }


    public void loadConfig(final String header)
    {
        config.options().header(header);
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void saveConfig()
    {
        if (config == null || configFile == null) return;

        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().info(ChatColor.RED + "Could not save config to " + configFile);
            e.printStackTrace();
        }

    }

    public void reloadConfig()
    {
        if (configFile != null) {
            if (folderName != null && !folderName.isEmpty()) {
                configFile = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
            } else {
                configFile = new File(plugin.getDataFolder(), fileName);
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
