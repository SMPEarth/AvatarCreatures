package me.relavis.avatarcreatures.util;

import me.relavis.avatarcreatures.AvatarCreatures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Objects;

public class ConfigHandler {
    //TODO create config handler
    private static ConfigHandler instance;

    String appaSpawnItem;
    Double appaMovementSpeed;
    Boolean appaUnmountWhenAppaDamaged;
    Boolean appaUnmountWhenPlayerDamaged;

    String storageType;
    String host;
    int port;
    String database;
    String username;
    String password;

    int version;

    public void configSetup() {
        AvatarCreatures plugin = AvatarCreatures.getInstance();
        instance = this;
        plugin.saveDefaultConfig();

        appaSpawnItem = plugin.getConfig().getString("appa.spawn-item");
        appaMovementSpeed = plugin.getConfig().getDouble("appa.movement-speed");
        appaUnmountWhenAppaDamaged = plugin.getConfig().getBoolean("appa.unmount-when-appa-damaged");
        appaUnmountWhenPlayerDamaged = plugin.getConfig().getBoolean("appa.unmount-when-player-damaged");

        storageType = plugin.getConfig().getString("storage.storage-type");
        host = plugin.getConfig().getString("storage.host");
        port = plugin.getConfig().getInt("storage.port");
        database = plugin.getConfig().getString("storage.database");
        username = plugin.getConfig().getString("storage.username");
        password = plugin.getConfig().getString("storage.password");

        version = plugin.getConfig().getInt("version");
    }

    public String getAppaConfig(String message) {
        switch(message) {
            case "spawnItem":
                return appaSpawnItem;
            case "movementSpeed":
                return appaMovementSpeed.toString();
            case "unmountWhenAppaDamaged":
                return appaUnmountWhenAppaDamaged.toString();
            case "unmountWhenPlayerDamaged":
                return appaUnmountWhenPlayerDamaged.toString();
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

    public int getVersion() {
        return version;
    }

    public static ConfigHandler getInstance() {
        return instance;
    }
}
