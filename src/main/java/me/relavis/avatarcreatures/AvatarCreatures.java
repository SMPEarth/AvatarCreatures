package me.relavis.avatarcreatures;

import com.comphenix.protocol.ProtocolLibrary;
import me.relavis.avatarcreatures.commands.AppaCommand;
import me.relavis.avatarcreatures.events.*;
import me.relavis.avatarcreatures.listeners.*;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public final class AvatarCreatures extends JavaPlugin implements Listener {

    public static Double movementSpeed;
    public static Double jumpSpeed;
    public static Boolean disableJump;

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        saveDefaultConfig();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Configuration initialization successful.");

        DataHandler sql = new DataHandler();
        sql.mysqlSetup();

        Bukkit.getPluginManager().registerEvents(new SpawnItemClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamagedEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityHostileTargetEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityDismountEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MountEntityDeathEvent(), this);
        //Bukkit.getPluginManager().registerEvents(new PlayerQuitEvent(), this);
        //Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), this);


        this.getCommand("appa").setExecutor(new AppaCommand());


        FileConfiguration config = getConfig();
        movementSpeed = config.getDouble("movement-steer-speed");
        jumpSpeed = config.getDouble("movement-jump-speed");
        disableJump = config.getBoolean("disable-jump");

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this,
                ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
            public void onPacketReceiving(PacketEvent event) {
                MountMoveListener.onMountEntityMove(event);
            }
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
