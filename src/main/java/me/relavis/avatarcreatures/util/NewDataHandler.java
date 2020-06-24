package me.relavis.avatarcreatures.util;

import com.zaxxer.hikari.HikariDataSource;
import me.relavis.avatarcreatures.AvatarCreatures;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class NewDataHandler implements Listener {

    private static NewDataHandler instance;
    private final ExecutorService service = Executors.newCachedThreadPool();
    private final HashMap<UUID, PlayerData> playerData = new HashMap<>();
    ConfigHandler config = ConfigHandler.getInstance();
    AvatarCreatures plugin = AvatarCreatures.getInstance();
    private HikariDataSource hikari;
    private Connection connection;

    public static NewDataHandler getInstance() {
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
                    asyncUpdate("CREATE TABLE IF NOT EXISTS avatarcreatures ( `id` INT NOT NULL AUTO_INCREMENT , `name` TINYTEXT NOT NULL , `playeruuid` TINYTEXT NOT NULL , `entityuuid` TINYTEXT NOT NULL , `type` TINYTEXT NOT NULL , `alive` BOOLEAN NOT NULL , PRIMARY KEY (`id`))");
                } else if (config.getStorage("type").equals("flatfile")) {
                    asyncUpdate("CREATE TABLE IF NOT EXISTS avatarcreatures ( `id` INT PRIMARY KEY , `name` TINYTEXT NOT NULL , `playeruuid` TINYTEXT NOT NULL , `entityuuid` TINYTEXT NOT NULL , `type` TINYTEXT NOT NULL , `alive` BOOLEAN NOT NULL)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        PlayerData data = new PlayerData(player.getUniqueId());
        int appaID;
        String appaName;
        UUID appaUUID;
        boolean appaAlive;
        try {
            PreparedStatement statement = null;
            ResultSet results;

            if (playerHasData(player)) {
                statement = connection.prepareStatement("SELECT * FROM masks WHERE playeruuid=? AND type=?");
                statement.setString(1, player.getUniqueId().toString());
                // TODO Make this modular so that we can get data from multiple entity types (in a for loop)
                statement.setString(1, "RAVAGER");

                results = statement.executeQuery();
                if (results.next()) {
                    appaID = results.getInt("id");
                    appaName = results.getString("entityuuid");
                    appaUUID = UUID.fromString(results.getString("type"));
                    appaAlive = results.getBoolean("alive");
                    results.close();

                    data.setEntityID(EntityType.RAVAGER, appaID);
                    data.setEntityDisplayName(EntityType.RAVAGER, appaName);
                    data.setEntityUUID(EntityType.RAVAGER, appaUUID);
                    data.setEntityAlive(EntityType.RAVAGER, appaAlive);
                    playerData.put(player.getUniqueId(), data);
                }
            } else {
                if (config.getStorage("type").equals("mysql")) {
                    statement = getConnection().prepareStatement("INSERT INTO avatarcreatures (name,playeruuid,entityuuid,type,alive) VALUE (?,?,?,?,?)");
                } else if (config.getStorage("type").equals("flatfile")) {
                    statement = getConnection().prepareStatement("INSERT INTO avatarcreatures (name,playeruuid,entityuuid,type,alive) VALUES (?,?,?,?,?)");
                }
                assert statement != null;
                statement.setString(1, "");
                statement.setString(2, player.getUniqueId().toString());
                statement.setString(3, "");
                statement.setString(4, "");
                statement.setBoolean(5, false);

                statement.executeUpdate();
                statement.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Unload+save player data
    public void unloadPlayerData(Player player) {
        int appaID = playerData.get(player.getUniqueId()).getEntityID(EntityType.RAVAGER);
        String appaName = playerData.get(player.getUniqueId()).getEntityDisplayName(EntityType.RAVAGER);
        UUID appaUUID = playerData.get(player.getUniqueId()).getEntityUUID(EntityType.RAVAGER);
        Boolean appaAlive = playerData.get(player.getUniqueId()).getEntityAlive(EntityType.RAVAGER);
        this.service.execute(() -> {
            try {
                setConnection();
                PreparedStatement statement = getConnection()
                        .prepareStatement("UPDATE avatarcreatures SET name=?, entityuuid=?, alive=? WHERE playeruuid=? AND type=?");
                statement.setString(1, appaName);
                statement.setString(2, appaUUID.toString());
                statement.setBoolean(3, appaAlive);
                statement.setString(4, player.getUniqueId().toString());
                // TODO make this modular to support multiple mount types
                statement.setString(5, EntityType.RAVAGER.toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        playerData.remove(player.getUniqueId());
    }

    public boolean playerHasData(Player player) {
        try {
            setConnection();
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
