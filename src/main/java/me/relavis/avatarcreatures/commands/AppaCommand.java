package me.relavis.avatarcreatures.commands;

import com.google.common.base.Joiner;
import me.relavis.avatarcreatures.util.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AppaCommand implements TabExecutor {

    DataHandler data = new DataHandler();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();
            String type = "RAVAGER";
            if(args.length > 0) {
                if(args[0].equalsIgnoreCase("rename")) {
                    String newName = "";
                    args = Arrays.copyOfRange(args, 1, args.length);
                    newName = Joiner.on(' ').skipNulls().join(args);
                    if(data.entityExists(playerUUID, type)) {
                        if (data.isAlive(playerUUID, type)) {
                            if (newName.length() < 16) {
                                if (newName.trim().length() > 0) {
                                    data.setEntityName(playerUUID, type, newName);
                                    sender.sendMessage(ChatColor.GREEN + "Name changed successfully.");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Names cannot be blank.");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "That name is too long to use!");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Your Appa is away! Use " + ChatColor.GOLD + "/appa call" + ChatColor.RED + " to call your Appa.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have an Appa!");
                    }
                } else if(args[0].equalsIgnoreCase("away")) {
                    if(data.isAlive(playerUUID, type)) {
                        UUID entityUUID = data.getEntityUUID(playerUUID, type);
                        Entity entity = data.getEntityByUniqueId(entityUUID);
                        entity.remove();
                        data.setAlive(entityUUID, false);
                        sender.sendMessage(ChatColor.GREEN + "Your Appa flies away.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have an Appa!");
                    }
                } else if(args[0].equalsIgnoreCase("call")) {
                    if(data.entityExists(playerUUID, type)) {
                        if(data.isAlive(playerUUID, type)) {
                            UUID entityUUID = data.getEntityUUID(playerUUID, type);
                            Entity entity = data.getEntityByUniqueId(entityUUID);

                            Location playerLocation = player.getLocation();

                            entity.teleport(playerLocation);
                            sender.sendMessage(ChatColor.GREEN + "Your Appa comes to you.");
                        } else {
                            if (data.entityExists(playerUUID, type)) { // Check if the user has already had a mount entity. If so:
                                player.getWorld().spawn(player.getLocation().add(0.0, 1.0, 0.0), Ravager.class, entity -> { // Spawn entity
                                    entity.setRemoveWhenFarAway(false);
                                    entity.setCustomNameVisible(true);
                                    entity.setCustomName(data.getEntityName(playerUUID, type)); // Change entity name to whatever is in DB
                                    UUID entityUUID = entity.getUniqueId(); // Get new entity UUID
                                    data.updateEntityUUID(playerUUID, entityUUID); // Update entity UUID to new UUID
                                    data.setAlive(entityUUID, true); // Set entity's state
                                    sender.sendMessage(ChatColor.GREEN + "Your Appa comes to you.");
                                    return;
                                });
                            }
                        }

                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have an Appa!");
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("You must be a player to perform this command.");
        }
        return false;
    }

    @Override

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1){

            List<String> arguments = new ArrayList<>();

            arguments.add("away");
            arguments.add("call");
            arguments.add("rename");
            return arguments;

        }

        return null;

    }

}
