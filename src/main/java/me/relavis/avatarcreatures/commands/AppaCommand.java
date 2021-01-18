package me.relavis.avatarcreatures.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@CommandAlias("appa")
public class AppaCommand extends BaseCommand {

    DataHandler data = DataHandler.getInstance();

    @HelpCommand
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("rename")
//    @Description()
    @CommandPermission("avatarcreatures.appa.rename")
    @CommandCompletion("@nothing")
    public void rename(Player player, String[] name) {
        String newName;
        UUID playerUUID = player.getUniqueId();
        //TODO remove hardcode
        EntityType type = EntityType.RAVAGER;
        name = Arrays.copyOfRange(name, 1, name.length);
        newName = Joiner.on(' ').skipNulls().join(name);
        if (data.entityExists(playerUUID, type)) {
            if (data.isAlive(playerUUID, type)) {
                if (newName.length() < 16) {
                    if (newName.trim().length() > 0) {
                        data.setEntityName(playerUUID, type, newName, true);
                        player.sendMessage(ChatColor.GREEN + "Name changed successfully.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Names cannot be blank.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "That name is too long to use!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Your Appa is away! Use " + ChatColor.GOLD + "/appa call" + ChatColor.RED + " to call your Appa.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't have an Appa!");
        }
    }

    @Subcommand("away")
//    @Description()
    @CommandPermission("avatarcreatures.appa.away")
    @CommandCompletion("@nothing")
    public void away(Player player) {
        String newName;
        UUID playerUUID = player.getUniqueId();
        //TODO remove hardcode
        EntityType type = EntityType.RAVAGER;
        if (data.entityExists(playerUUID, type)) {
            if (data.isAlive(playerUUID, type)) {
                UUID entityUUID = data.getEntityUUID(playerUUID, type);
                Entity entity = data.getEntityByUniqueId(entityUUID);
                entity.remove();
                data.setAlive(playerUUID, entityUUID, false);
                player.sendMessage(ChatColor.GREEN + "Your Appa flies away.");
            } else {
                player.sendMessage(ChatColor.RED + "Your Appa is away! Use " + ChatColor.GOLD + "/appa call" + ChatColor.RED + " to call your Appa.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't have an Appa!");
        }
    }

    @Subcommand("call")
//    @Description()
    @CommandPermission("avatarcreatures.appa.call")
    @CommandCompletion("@nothing")
    public void call(Player player) {
        String newName;
        UUID playerUUID = player.getUniqueId();
        //TODO remove hardcode
        EntityType type = EntityType.RAVAGER;
        if (data.entityExists(playerUUID, type)) {
            if (data.isAlive(playerUUID, type)) {
                UUID entityUUID = data.getEntityUUID(playerUUID, type);
                Entity entity = data.getEntityByUniqueId(entityUUID);

                Location playerLocation = player.getLocation();

                entity.teleport(playerLocation);
                player.sendMessage(ChatColor.GREEN + "Your Appa comes to you.");
            } else {
                if (data.entityExists(playerUUID, type)) { // Check if the user has already had a mount entity. If so:
                    player.getWorld().spawn(player.getLocation().add(0.0, 1.0, 0.0), Ravager.class, entity -> { // Spawn entity
                        entity.setRemoveWhenFarAway(false);
                        entity.setGravity(false);
                        entity.setCustomNameVisible(true);
                        entity.setCustomName(data.getEntityName(playerUUID, type)); // Change entity name to whatever is in DB
                        UUID entityUUID = entity.getUniqueId(); // Get new entity UUID
                        data.updateEntityUUID(playerUUID, entity.getType(), entityUUID); // Update entity UUID to new UUID
                        data.setAlive(playerUUID, entityUUID, true); // Set entity's state
                        player.sendMessage(ChatColor.GREEN + "Your Appa comes to you.");
                    });
                }
            }

        } else {
            player.sendMessage(ChatColor.RED + "You don't have an Appa!");
        }
    }
}
