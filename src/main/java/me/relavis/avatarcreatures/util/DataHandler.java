package me.relavis.avatarcreatures.util;

import me.relavis.avatarcreatures.AvatarCreatures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.UUID.fromString;

public class DataHandler implements Listener {

    // TODO finish data handler

    private final ExecutorService service = Executors.newCachedThreadPool();
    AvatarCreatures plugin = AvatarCreatures.getPlugin(AvatarCreatures.class);
    public String host = plugin.getConfig().getString("host");
    public int port = plugin.getConfig().getInt("port");
    public String database = plugin.getConfig().getString("database");
    public String username = plugin.getConfig().getString("username");
    public String password = plugin.getConfig().getString("password");
    //ArrayList<Integer> id;
    //ArrayList<String> entityName;
    //ArrayList<UUID> playerUUID;
    //ArrayList<EntityType> entityType;
    //ArrayList<Boolean> entityAlive;
    private Connection connection;

    public void mysqlSetup() {
        try {
            synchronized (this) {

                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                setConnection();

                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MySQL connection successful.");

                asyncUpdate("CREATE TABLE IF NOT EXISTS avatarcreatures ( `id` INT NOT NULL AUTO_INCREMENT , `name` TINYTEXT NOT NULL , `playeruuid` TINYTEXT NOT NULL , `entityuuid` TINYTEXT NOT NULL , `type` TINYTEXT NOT NULL , `alive` BOOLEAN NOT NULL , PRIMARY KEY (`id`))");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database,
                username, password);
    }

    // Check if player has any data associated with him
    public boolean playerHasData(UUID playerUUID) {
        try {
            setConnection();
            PreparedStatement statement = getConnection()
                    .prepareStatement("SELECT * FROM avatarcreatures WHERE playeruuid = ?");
            statement.setString(1, playerUUID.toString());

            ResultSet results = statement.executeQuery();

            if (results.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // For future use - initialize the player data on server join
    public void initializePlayerData(UUID playerUUID) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                setConnection();
                PreparedStatement statement = getConnection()
                        .prepareStatement("SELECT * FROM avatarcreatures WHERE playeruuid = ?");
                statement.setString(1, playerUUID.toString());

                //ResultSet results = statement.executeQuery();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Check if entity with owner and type exists
    public boolean entityExists(UUID playerUUID, String type) {
        try {
            setConnection();
            PreparedStatement statement = getConnection()
                    .prepareStatement("SELECT * FROM avatarcreatures WHERE playeruuid=? AND type=?");
            statement.setString(1, playerUUID.toString());
            statement.setString(2, type);

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public Entity getEntityByUniqueId(UUID uniqueId) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getUniqueId().equals(uniqueId))
                    return entity;
            }
        }

        return null;
    }

    // Get entity UUID
    public UUID getEntityUUID(UUID playerUUID, String type) {
        try {
            setConnection();
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM avatarcreatures WHERE playeruuid = ? AND type = ?");
            statement.setString(1, playerUUID.toString());
            statement.setString(2, type);
            ResultSet results = statement.executeQuery();
            results.next();

            return fromString(results.getString("entityuuid"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get player UUID
    public UUID getEntityOwnerUUID(UUID entityUUID) {
        try {
            setConnection();
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM avatarcreatures WHERE entityuuid=?");
            statement.setString(1, entityUUID.toString());
            ResultSet results = statement.executeQuery();
            results.next();

            return fromString(results.getString("playeruuid"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean isOwned(UUID entityUUID) {
        try{
            setConnection();
            PreparedStatement statement = getConnection()
                    .prepareStatement("SELECT * FROM avatarcreatures WHERE entityuuid=?");
            statement.setString(1, entityUUID.toString());

            ResultSet results = statement.executeQuery();
            if(results.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get entity name
    public String getEntityName(UUID playerUUID, String type) {
        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM avatarcreatures WHERE playeruuid = ? AND type = ?");
            statement.setString(1, playerUUID.toString());
            statement.setString(2, type);
            ResultSet results = statement.executeQuery();
            results.next();

            return results.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Set entity name if it exists
    public void setEntityName(UUID playerUUID, String type, String newName) {
        this.service.execute(() -> {
            try {
                setConnection();
                PreparedStatement statement = getConnection()
                        .prepareStatement("UPDATE avatarcreatures SET name=? WHERE playeruuid=? AND type=?");
                statement.setString(1, newName);
                statement.setString(2, playerUUID.toString());
                statement.setString(3, type);
                statement.executeUpdate();

                UUID entityUUID = getEntityUUID(playerUUID, type);
                Entity entity = getEntityByUniqueId(entityUUID);
                entity.setCustomName(newName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Create entity
    public void addEntityToData(String playerName, UUID playerUUID, UUID entityUUID, String type) {
        this.service.execute(() -> {
            try {
                setConnection();
                PreparedStatement insert;
                insert = getConnection().prepareStatement("INSERT INTO avatarcreatures (name,playeruuid,entityuuid,type,alive) VALUE (?,?,?,?,?)");
                insert.setString(1, playerName + "'s Appa");
                insert.setString(2, playerUUID.toString());
                insert.setString(3, entityUUID.toString());
                insert.setString(4, type);
                insert.setBoolean(5, true);
                insert.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void removeEntityFromData(UUID entityUUID) {
        this.service.execute(() -> {
           try {
               setConnection();
               PreparedStatement statement = getConnection()
                       .prepareStatement("DELETE FROM avatarcreatures WHERE entityuuid=?");
               statement.setString(1, entityUUID.toString());

               statement.executeUpdate();
           } catch (SQLException e) {
                e.printStackTrace();
           }
        });
    }

    // After a player has had their entity respawned, update to its new UUID
    public void updateEntityUUID(UUID playerUUID, UUID newUUID) {
        try {
            setConnection();
            PreparedStatement statement = getConnection()
                    .prepareStatement("UPDATE avatarcreatures SET entityuuid=? WHERE playeruuid=?");
            statement.setString(1, newUUID.toString());
            statement.setString(2, playerUUID.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if player is the owner of entity
    public boolean isOwner(UUID playerUUID, UUID entityUUID) {
        try {
            setConnection();
            PreparedStatement statement = getConnection()
                        .prepareStatement("SELECT * FROM avatarcreatures WHERE playeruuid=? AND entityuuid=?");
            statement.setString(1, playerUUID.toString());
            statement.setString(2, entityUUID.toString());

            ResultSet results = statement.executeQuery();

            if(results.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if entity of certain player and type is alive
    public boolean isAlive(UUID playerUUID, String type) {
        try {
            setConnection();
            PreparedStatement statement = getConnection()
                    .prepareStatement("SELECT * FROM avatarcreatures WHERE playeruuid=? AND type=? AND alive=?");
            statement.setString(1, playerUUID.toString());
            statement.setString(2, type);
            statement.setString(3, "1");

            ResultSet results = statement.executeQuery();

            if (results.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Set entity's state to alive
    public void setAlive(UUID entityUUID, Boolean alive) {
        this.service.execute(() -> {
            try {
                setConnection();
                PreparedStatement statement = getConnection()
                        .prepareStatement("UPDATE avatarcreatures SET alive=? WHERE entityuuid=?");
                statement.setBoolean(1, alive);
                statement.setString(2, entityUUID.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void asyncUpdate(String query) {
        // The ExecutorService will execute your sync MySQL update asynchronously.
        this.service.execute(() -> this.syncUpdate(query));
    }

    private void syncUpdate(String query) {
        // Work with PreparedStatements to prevent SQL injections.
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
