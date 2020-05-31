package ru.sgk.dreamtimeapi.io;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Use: <br>
 *     Чтобы создать конфиг пишем <br>
 * <code>mainConfig = new ConfigWrapper(plugin*, folderName*, fileName*);</code><br>
 * <code>mainConfig.createNewFile(null); // Создам файл без хедера</code><br>
 * Создаём мапу дефолт значений конфига:<br>
 * <code>
 * Map<String, Object> map = new HashMap<>(); <br>
 * map.put("rewards.default.title", "&aDefault"); <br>
 * map.put("rewards.default.permission", "dailyrewards.reward.default"); <br>
 * map.put("rewards.default.cooldown", 1440); <br>
 * map.put("rewards.default.time-unit", DRTimeUnit.MINUTE.toString()); <br>
 * map.put("rewards.default.gui-char", 'A'); <br>
 * map.put("rewards.default.gui-item", "DIRT"); <br>
 * ...
 * <br>
 *
 * mainConfig.setDefaults(map); // Устанавливаем мапу в нужное место <br>
 * mainConfig.loadConfig(null); //Грузим конфиг по новой
 * </code>
 */
public class ConfigWrapper {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    private final String folderName;
    private final String fileName;
    private String header;
    private int saveCoolDown = 0;
    private boolean configSaving = false;

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

    public synchronized void forceSave()
    {
        if (config == null || configFile == null) return;

        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().info(ChatColor.RED + "Could not save config to " + configFile);
            e.printStackTrace();
        } finally {
            // Отмечаем, что конфиг закончил сохранение
            configSaving = false;
        }

    }

    public synchronized void saveConfig()
    {
        if (config == null || configFile == null) return;

        if (saveCoolDown == 0) {
            forceSave();
        } else {

            // Начинаем сохранение конфига
            if (!configSaving) {
                Bukkit.getScheduler().runTaskLater(this.plugin, this::forceSave, saveCoolDown / 50);
                // Пометка того, что конфиг ждёт сохранения
                configSaving = true;
            }
        }
    }

    public synchronized void reloadConfig()
    {
        if (configFile == null) {
            if (folderName != null && !folderName.isEmpty()) {
                configFile = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
            } else {
                configFile = new File(plugin.getDataFolder(), fileName);
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * @param coolDown
     */
    public void setSaveCoolDown(int coolDown) {
        this.saveCoolDown = coolDown;
    }
}
