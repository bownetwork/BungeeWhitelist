package com.bownetwork.bungeewhitelist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.List;

public class Command extends net.md_5.bungee.api.plugin.Command {

    private Main main;
    public Command(Main main) {
        super("whitelist");
        this.main=main;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            String prefix = main.config.getString("Prefix");
            if (player.hasPermission("whitelist.cmd")) {
                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("group")) {
                        if (args.length == 3) {
                            if (args[1].equalsIgnoreCase("add")) {
                                if (main.validateGroupName(args[2], false)) {
                                    List<String> groupList = main.config.getStringList("AllowedGroups");
                                    if (!groupList.contains(args[2])) {
                                        groupList.add(args[2]);
                                        main.config.set("AllowedGroups", groupList);
                                        try {
                                            main.saveConfig();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.sendMessage(prefix + " " + ChatColor.GREEN + "This group has been added to the allowed list.");
                                    } else {
                                        player.sendMessage(prefix + " " + ChatColor.RED + "This group is already in the allowed list.");
                                    }
                                } else {
                                    player.sendMessage(prefix + " " + ChatColor.RED + "This group doesn't exist!");
                                }
                            } else if (args[1].equalsIgnoreCase("remove")) {
                                if (main.validateGroupName(args[2], false)) {
                                    List<String> groupList = main.config.getStringList("AllowedGroups");
                                    if (groupList.contains(args[2])) {
                                        groupList.remove(args[2]);
                                        main.config.set("AllowedGroups", groupList);
                                        try {
                                            main.saveConfig();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.sendMessage(prefix + " " + ChatColor.GREEN + "This group has been removed from the allowed list.");
                                    } else {
                                        player.sendMessage(prefix + " " + ChatColor.RED + "This group isn't on the allowed list!");
                                    }
                                } else {
                                    player.sendMessage(prefix + " " + ChatColor.RED + "This group doesn't exist! Valid groups are Group1, Group2, and Group3.");
                                }
                            } else {
                                player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
                            }
                        } else {
                            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
                        }
                    } else if (args[0].equalsIgnoreCase("user")) {
                        if (args.length == 4) {
                            if (args[1].equalsIgnoreCase("add")) {
                                if (main.validateGroupName(args[3], true)) {
                                    List<String> groupToChange = main.config.getStringList(args[3]);
                                    if (!groupToChange.contains(args[2])) {
                                        groupToChange.remove(args[2]);
                                        main.config.set(args[3], groupToChange);
                                        try {
                                            main.saveConfig();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.sendMessage(prefix + " " + ChatColor.GREEN + "This player has been removed from this group.");
                                    }
                                } else {
                                    player.sendMessage(prefix + " " + ChatColor.RED + "This player is already on the allowed list!");
                                }
                            } else if (args[1].equalsIgnoreCase("remove")) {
                                if (main.validateGroupName(args[3], true)) {
                                    List<String> groupToChange = main.config.getStringList(args[3]);
                                    if (groupToChange.contains(args[2])) {
                                        groupToChange.remove(args[2]);
                                        try {
                                            main.saveConfig();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.sendMessage(prefix + " " + ChatColor.GREEN + "This player has been removed from this group.");
                                    }
                                } else {
                                    player.sendMessage(prefix + " " + ChatColor.RED + "This player isn't on the allowed list.");
                                }
                            } else {
                                player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
                            }
                        } else {
                            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
                        }
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        main.reloadConfig();
                    } else {
                        player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
                    }
                } else {
                    player.sendMessage(prefix + " " + ChatColor.DARK_AQUA + "Whitelist Command Help");
                    player.sendMessage(ChatColor.AQUA + "/whitelist group add (group name): Adds that group to the whitelist.");
                    player.sendMessage(ChatColor.AQUA + "/whitelist group remove (group name): Removes that group from the whitelist.");
                    player.sendMessage(ChatColor.AQUA + "/whitelist user add (username) (group name): Adds that user to that group.");
                    player.sendMessage(ChatColor.AQUA + "/whitelist user remove (username) (group name): Removes that user from that group.");
                    player.sendMessage(ChatColor.AQUA + "/whitelist reload: Reload the config file.");
                }
            } else {
                player.sendMessage(prefix + " " + ChatColor.RED + "You don't have permission to do this!");
            }
        } else {
            System.out.println("Console can't run this!");
        }
    }
}
