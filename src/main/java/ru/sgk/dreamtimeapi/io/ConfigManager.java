package ru.sgk.dreamtimeapi.io;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager
{
    private static final String MAIN_CONFIG_NAME = "config.yml";
    private File dataFolder = null;
    private Map<String, FileConfiguration> configurations = new ConcurrentHashMap<>();
    private JavaPlugin plugin;
    public ConfigManager(JavaPlugin plugin)
    {
        this.plugin = plugin;
        dataFolder = this.plugin.getDataFolder();
        loadMainConfig();
    }

    public FileConfiguration loadConfig(String name) throws NullPointerException
    {
        File file = new File(dataFolder + "/" + name);
        FileConfiguration config = null;
        try (
                InputStream in = plugin.getClass().getResourceAsStream("/" + name);
                Reader reader = new InputStreamReader(in);
        )
        {
            config = YamlConfiguration.loadConfiguration(file);
            config.setDefaults(YamlConfiguration.loadConfiguration(reader));
            config.options().copyDefaults(true);

            configurations.remove(name);
            configurations.put(name, config);

            saveConfig(name);
        }
        catch (IOException e)
        {
            plugin.getLogger().warning("§cПроблема с загрузкой конфигурации " + name);
            e.printStackTrace();
        }
        plugin.getLogger().info("§aКонфигурации " + name + " успешно загружена!");
        return config;
    }

    public FileConfiguration getConfig(String name)
    {
        return configurations.containsKey(name) ? configurations.get(name) : loadConfig(name);
    }

    public FileConfiguration loadMainConfig()
    {
        return loadConfig(MAIN_CONFIG_NAME);
    }

    public FileConfiguration getMainConfig()
    {
        return getConfig(MAIN_CONFIG_NAME);
    }

    public synchronized void saveConfig(String name)
    {
        File file = new File(dataFolder + "/" + name);
        try
        {
            FileConfiguration config = configurations.get(name);
            if (config != null)
                config.save(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void saveMainConfig()
    {
        saveConfig(MAIN_CONFIG_NAME);
    }
}
