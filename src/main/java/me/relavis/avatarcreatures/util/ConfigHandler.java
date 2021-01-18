package me.relavis.avatarcreatures.util;

import me.relavis.avatarcreatures.AvatarCreatures;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigHandler {
    private static ConfigHandler instance;

    int currentVersion = 2;

    String appaSpawnItem;
    Double appaMovementSpeed;
    Boolean appaUnmountWhenAppaDamaged;
    Boolean appaUnmountWhenPlayerDamaged;
    Boolean appaDespawnAfterUnmount;
    int appaDespawnTime;

    String storageType;
    String host;
    int port;
    String database;
    String username;
    String password;

    int version;

    public static ConfigHandler getInstance() {
        return instance;
    }

    public void configSetup() {
        AvatarCreatures plugin = AvatarCreatures.getInstance();
        instance = this;

        updateConfig();

        appaSpawnItem = plugin.getConfig().getString("appa.spawn-item");
        appaMovementSpeed = plugin.getConfig().getDouble("appa.movement-speed");
        appaUnmountWhenAppaDamaged = plugin.getConfig().getBoolean("appa.unmount-when-appa-damaged");
        appaUnmountWhenPlayerDamaged = plugin.getConfig().getBoolean("appa.unmount-when-player-damaged");
        appaDespawnAfterUnmount = plugin.getConfig().getBoolean("appa.despawn-after-unmount");
        appaDespawnTime = plugin.getConfig().getInt("appa.despawn-time");

        storageType = plugin.getConfig().getString("storage.storage-type");
        host = plugin.getConfig().getString("storage.host");
        port = plugin.getConfig().getInt("storage.port");
        database = plugin.getConfig().getString("storage.database");
        username = plugin.getConfig().getString("storage.username");
        password = plugin.getConfig().getString("storage.password");

        version = plugin.getConfig().getInt("version");

        plugin.getLogger().log(Level.INFO, "Configuration initialization successful.");
    }

    public void updateConfig() {
        AvatarCreatures plugin = AvatarCreatures.getInstance();
        plugin.saveDefaultConfig();
        plugin.getConfig().options().copyDefaults(true);
        try {
            if (new File(plugin.getDataFolder() + "/config.yml").exists()) {
                boolean changesMade = false;
                YamlConfiguration tmp = new YamlConfiguration();
                tmp.load(plugin.getDataFolder() + "/config.yml");
                FileConfiguration defaultConfig = plugin.getConfig();
                for (String str : defaultConfig.getKeys(true)) {
                    if (!tmp.getKeys(true).contains(str)) {
                        tmp.set(str, defaultConfig.get(str));
                        changesMade = true;
                    }
                }
                if (changesMade) {
                    tmp.options().header("Your config has been updated from a previous version.\nComments have not been saved.");
                    tmp.set("version", currentVersion);
                    tmp.save(plugin.getDataFolder() + "/config.yml");
                    plugin.saveDefaultConfig();
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getAppaConfig(String message) {
        switch (message) {
            case "spawnItem":
                return appaSpawnItem;
            case "movementSpeed":
                return appaMovementSpeed.toString();
            case "unmountWhenAppaDamaged":
                return appaUnmountWhenAppaDamaged.toString();
            case "unmountWhenPlayerDamaged":
                return appaUnmountWhenPlayerDamaged.toString();
            case "despawnAfterUnmount":
                return appaDespawnAfterUnmount.toString();
            case "despawnTime":
                return Integer.toString(appaDespawnTime);
            default:
                return null;
        }
    }

    public String getStorage(String value) {
        switch (value) {
            case "type":
                return storageType;
            case "host":
                return host;
            case "port":
                return Integer.toString(port);
            case "database":
                return database;
            case "username":
                return username;
            case "password":
                return password;
            default:
                return null;
        }
    }
}
