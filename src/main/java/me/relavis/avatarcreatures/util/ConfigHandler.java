package me.relavis.avatarcreatures.util;

import lombok.Getter;
import me.relavis.avatarcreatures.AvatarCreatures;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigHandler {

    @Getter
    private static ConfigHandler instance;

    int currentVersion = 2;

    @Getter
    String appaSpawnItem;
    @Getter
    Double appaMovementSpeed;
    @Getter
    Boolean appaUnmountWhenAppaDamaged;
    @Getter
    Boolean appaUnmountWhenPlayerDamaged;
    @Getter
    Boolean appaDespawnAfterUnmount;
    @Getter
    int appaDespawnTime;

    @Getter
    String storageType;
    @Getter
    String databaseHost;
    @Getter
    int databasePort;
    @Getter
    String databaseName;
    @Getter
    String databaseUsername;
    @Getter
    String databasePassword;

    int version;

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
        databaseHost = plugin.getConfig().getString("storage.host");
        databasePort = plugin.getConfig().getInt("storage.port");
        databaseName = plugin.getConfig().getString("storage.database");
        databaseUsername = plugin.getConfig().getString("storage.username");
        databasePassword = plugin.getConfig().getString("storage.password");

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

}
