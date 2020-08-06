package me.relavis.avatarcreatures.util;

import com.zaxxer.hikari.HikariDataSource;
import me.relavis.avatarcreatures.AvatarCreatures;
import me.relavis.avatarcreatures.util.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static java.util.UUID.fromString;

public class DataHandler implements Listener {

    private static DataHandler instance;
    private final ExecutorService service = Executors.newCachedThreadPool();
    private final HashMap<UUID, PlayerData> playerData = new HashMap<>();
    ConfigHandler config = ConfigHandler.getInstance();
    AvatarCreatures plugin = AvatarCreatures.getInstance();
    private HikariDataSource hikari;
    private Connection connection;

    public static DataHandler getInstance() {
        return instance;
    }

    // Initialize storage
    public void dataSetup() {
        instance = this;
        try {
            synchronized (this) {

                if (config.getStorage("type").equals("mysql")) {
                    hikari = new HikariDataSource();
                    hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
                    hikari.addDataSourceProperty("serverName", config.getStorage("host"));
                    hikari.addDataSourceProperty("port", config.getStorage("port"));
                    hikari.addDataSourceProperty("databaseName", config.getStorage("database"));
                    hikari.addDataSourceProperty("user", config.getStorage("username"));
                    hikari.addDataSourceProperty("password", config.getStorage("password"));
                }
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }

                setConnection();

                plugin.getLogger().log(Level.INFO, "Database initialization successful.");
                if (config.getStorage("type").equals("mysql")) {
                    asyncUpdate("CREATE TABLE IF NOT EXISTS avatarcreatures ( `id` INT NOT NULL AUTO_INCREMENT , `name` TINYTEXT NOT NULL , `playeruuid` TINYTEXT NOT NULL , `entityuuid` TINYTEXT NOT NULL , `type` TINYTEXT NOT NULL , `alive` BOOLEAN NOT NULL , `killed` BOOLEAN NOT NULL , PRIMARY KEY (`id`))");
                } else if (config.getStorage("type").equals("flatfile")) {
                    asyncUpdate("CREATE TABLE IF NOT EXISTS avatarcreatures ( `id` INTEGER PRIMARY KEY , `name` TINYTEXT NOT NULL , `playeruuid` TINYTEXT NOT NULL , `entityuuid` TINYTEXT NOT NULL , `type` TINYTEXT NOT NULL , `alive` BOOLEAN NOT NULL , `killed` BOOLEAN NOT NULL)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        initializeOnlinePlayers();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection() throws SQLException {
        if (config.getStorage("type").equals("mysql")) {
            try {
                Class.forName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            } catch (ClassNotFoundException e) {
                plugin.getLogger().log(Level.SEVERE, "Error setting DB connection: " + e);
            }
            connection = hikari.getConnection();
        } else if (config.getStorage("type").equals("flatfile")) {
            File dataFolder = new File(plugin.getDataFolder(), "storage.db");
            if (!dataFolder.exists()) {
                try {
                    boolean success = dataFolder.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "File write error: storage.db");
                }
            }
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            } catch (SQLException | ClassNotFoundException e) {
                plugin.getLogger().log(Level.SEVERE, "Error setting DB connection: ", e);
            }
        }
    }

    // Initialize player data
    public void initializePlayerData(Player player) {
        PlayerData playerData = new PlayerData(player.getUniqueId());
        try {
            if (playerHasData(player)) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM avatarcreatures WHERE playeruuid=?");
                statement.setString(1, player.getUniqueId().toString());

                ResultSet results = statement.executeQuery();
                while (results.next()) {
                    int mountID = results.getInt("id");
                    String entityName = results.getString("name");
                    UUID entityUUID = fromString(results.getString("entityuuid"));
                    boolean entityAlive = results.getBoolean("alive");
                    EntityType entityType = EntityType.valueOf(results.getString("type"));
                    boolean entityKilled = results.getBoolean("killed");
                    playerData.initializeEntity(mountID, entityType, entityName, entityUUID, entityAlive);

                    if (entityKilled) {
                        removeEntityFromData(entityUUID, false);
                        player.sendMessage(ChatColor.BLUE + "Your Appa has died while you were offline.");
                    }
                }
                results.close();
            }
            this.playerData.put(player.getUniqueId(), playerData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeOnlinePlayers() {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        if (onlinePlayers.size() != 0) {
            Bukkit.getLogger().warning("It is not reccommended to reload AvatarCreatures while players are online.");
            Bukkit.getLogger().warning("This can result in data loss and/or duplication.");
            Bukkit.getLogger().warning("Reloading player data for " + onlinePlayers.size() + " players");
        }
        for (Player player : onlinePlayers) {
            initializePlayerData(player);
        }
    }

    // Unload+save player data
    public void unloadPlayerData(Player player) {
        playerData.get(player.getUniqueId()).getEntityData().forEach((entityUUID, EntityData) -> {
            String entityName = playerData.get(player.getUniqueId()).getEntityDisplayName(entityUUID);
            EntityType entityType = playerData.get(player.getUniqueId()).getEntityType(entityUUID);
            boolean entityAlive = playerData.get(player.getUniqueId()).getEntityAlive(entityUUID);
            int mountID = playerData.get(player.getUniqueId()).getMountID(entityUUID);
            this.service.execute(() -> {
                if (mountID == -1) {
                    try {
                        PreparedStatement statement = getConnection()
                                .prepareStatement("INSERT INTO avatarcreatures (id,name,playeruuid,entityuuid,type,alive,killed) VALUES (?,?,?,?,?,?,?)");
                        statement.setString(1, null);
                        statement.setString(2, entityName);
                        statement.setString(3, player.getUniqueId().toString());
                        statement.setString(4, entityUUID.toString());
                        statement.setString(5, entityType.toString());
                        statement.setBoolean(6, entityAlive);
                        statement.setBoolean(7, false);
                        statement.executeUpdate();
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        PreparedStatement statement = getConnection()
                                .prepareStatement("UPDATE avatarcreatures SET name=?, entityuuid=?, alive=? WHERE id=?");
                        statement.setString(1, entityName);
                        statement.setString(2, entityUUID.toString());
                        statement.setBoolean(3, entityAlive);
                        statement.setInt(4, mountID);
                        statement.executeUpdate();
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
        playerData.remove(player.getUniqueId());
    }

    public void unloadOnlinePlayers() {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player player : onlinePlayers) {
            unloadPlayerData(player);
        }
    }

    public boolean playerHasData(Player player) {
        try {
            PreparedStatement statement = getConnection()
                    .prepareStatement("SELECT * FROM avatarcreatures WHERE playeruuid=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                results.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if entity with owner and type exists
    public boolean entityExists(UUID playerUUID, UUID entityUUID) {
        return playerData.get(playerUUID).getEntityExists(entityUUID);
    }

    @Deprecated
    public boolean entityExists(UUID playerUUID, EntityType entityType) {
        return playerData.get(playerUUID).getEntityExists(entityType);
    }

    public Entity getEntityByUniqueId(UUID entityUUID) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getUniqueId().equals(entityUUID))
                    return entity;
            }
        }

        return null;
    }

    // Get entity UUID
    @Deprecated
    public UUID getEntityUUID(UUID playerUUID, EntityType entityType) {
        return playerData.get(playerUUID).getEntityUUID(entityType);
    }

    // Get player UUID
    public UUID getEntityOwnerUUID(UUID entityUUID) {
        for (Map.Entry<UUID, PlayerData> pair : playerData.entrySet()) {
            if (playerData.get(pair.getKey()).getEntityExists(entityUUID)) {
                return pair.getKey();
            }
        }
        return null;
    }

    public boolean entityHasData(UUID entityUUID) {
        if (isInitialized(entityUUID)) {
            return true;
        } else {
            try {
                PreparedStatement statement = getConnection()
                        .prepareStatement("SELECT * FROM avatarcreatures WHERE entityuuid=?");
                statement.setString(1, entityUUID.toString());
                ResultSet results = statement.executeQuery();
                if (results.next()) {
                    results.close();
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean isInitialized(UUID entityUUID) {
        for (Map.Entry<UUID, PlayerData> pair : playerData.entrySet()) {
            if (playerData.get(pair.getKey()).getEntityExists(entityUUID)) {
                return true;
            }
        }
        return false;
    }

    // Get entity name
    public String getEntityName(UUID playerUUID, UUID entityUUID) {
        return playerData.get(playerUUID).getEntityDisplayName(entityUUID);
    }

    @Deprecated
    public String getEntityName(UUID playerUUID, EntityType entityType) {
        return playerData.get(playerUUID).getEntityDisplayName(entityType);
    }

    // Set entity name if it exists
    public void setEntityName(UUID playerUUID, UUID entityUUID, String newName, boolean rename) {
        playerData.get(playerUUID).setEntityDisplayName(entityUUID, newName, rename);
    }

    @Deprecated
    public void setEntityName(UUID playerUUID, EntityType entityType, String newName, boolean rename) {
        playerData.get(playerUUID).setEntityDisplayName(entityType, newName, rename);
    }

    @Deprecated
    // Create entity
    public void addEntityToData(String playerName, UUID playerUUID, UUID entityUUID, EntityType entityType) {
        playerData.get(playerUUID).initializeEntity(-1, entityType, playerName + "'s Appa", entityUUID, true);
    }

    public void addEntityToData(UUID playerUUID, int mountID, EntityType entityType, String entityName, UUID entityUUID, boolean entityAlive) {
        playerData.get(playerUUID).initializeEntity(mountID, entityType, entityName, entityUUID, entityAlive);
    }

    public void removeEntityFromData(UUID entityUUID, boolean removeFromPlayerData) {
        UUID playerUUID = getEntityOwnerUUID(entityUUID);
        if (removeFromPlayerData) playerData.get(playerUUID).deleteEntity(entityUUID);

        this.service.execute(() -> {
            try {
                PreparedStatement statement = getConnection()
                        .prepareStatement("DELETE FROM avatarcreatures WHERE entityuuid=?");
                statement.setString(1, String.valueOf(entityUUID));
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setEntityKilled(UUID entityUUID) {
        this.service.execute(() -> {
            try {
                PreparedStatement statement = getConnection()
                        .prepareStatement("UPDATE avatarcreatures SET alive=?, killed=? WHERE entityuuid=?");
                statement.setBoolean(1, false);
                statement.setBoolean(2, true);
                statement.setString(3, entityUUID.toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // After a player has had their entity respawned, update to its new UUID
    public void updateEntityUUID(UUID playerUUID, UUID entityUUID, UUID newEntityUUID) {
        playerData.get(playerUUID).setEntityUUID(entityUUID, newEntityUUID);
    }

    @Deprecated
    public void updateEntityUUID(UUID playerUUID, EntityType entityType, UUID newEntityUUID) {
        playerData.get(playerUUID).setEntityUUID(entityType, newEntityUUID);
    }

    // Check if player is the owner of entity
    public boolean isOwner(UUID playerUUID, UUID entityUUID) {
        return playerData.get(playerUUID).ownsEntity(entityUUID);
    }

    // Check if entity of certain player and type is alive
    public boolean isAlive(UUID playerUUID, UUID entityUUID) {
        return playerData.get(playerUUID).getEntityAlive(entityUUID);
    }

    @Deprecated
    public boolean isAlive(UUID playerUUID, EntityType entityType) {
        return playerData.get(playerUUID).getEntityAlive(entityType);
    }

    public boolean isAliveFromUUID(UUID entityUUID) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getUniqueId().equals(entityUUID))
                    return true;
            }
        }
        return false;
    }

    // Set entity's alive state
    public void setAlive(UUID playerUUID, UUID entityUUID, Boolean entityAlive) {
        playerData.get(playerUUID).setEntityAlive(entityUUID, entityAlive);
    }

    public void asyncUpdate(String query) {
        this.service.execute(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.executeUpdate();
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
