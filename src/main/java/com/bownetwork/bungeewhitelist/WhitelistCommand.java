package com.bownetwork.bungeewhitelist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WhitelistCommand extends Command implements TabExecutor {

    private Main main;

    public WhitelistCommand(Main main) {
        super("bwhitelist", "", "bwl, blist");
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                main.reloadConfig();
                main.getLogger().log(Level.INFO, "Plugin reloaded.");
                return;
            } else {
                main.getLogger().log(Level.INFO, "Whitelist Command Help - Console");
                main.getLogger().log(Level.INFO, "/whitelist reload: Reloads the whitelist.");
                return;
            }
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        String prefix = main.config.getString("Prefix");

        if (!player.hasPermission("whitelist.cmd")) {
            player.sendMessage(prefix + " " + ChatColor.RED + "You don't have permission to do this!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.DARK_AQUA + "Whitelist Command Help");
            player.sendMessage(ChatColor.AQUA + "/whitelist group add (group name): Adds that group to the whitelist.");
            player.sendMessage(ChatColor.AQUA + "/whitelist group remove (group name): Removes that group from the whitelist.");
            player.sendMessage(ChatColor.AQUA + "/whitelist user add (username) (group name): Adds that user to that group.");
            player.sendMessage(ChatColor.AQUA + "/whitelist user remove (username) (group name): Removes that user from that group.");
            player.sendMessage(ChatColor.AQUA + "/whitelist reload: Reload the config file.");
            return;
        }

        String actionType = args[0].toLowerCase();

        if (actionType.equals("group")) {
            handleGroupAction(player, args);
        } else if (actionType.equals("user")) {
            handleUserAction(player, args);
        } else if (actionType.equals("reload")) {
            main.reloadConfig();
            player.sendMessage(prefix + " " + ChatColor.GREEN + "Reloaded successfully.");
        } else {
            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
        }
    }

    private void handleGroupAction(ProxiedPlayer player, String[] args) {
        String prefix = main.config.getString("Prefix");
        if (args.length != 3) {
            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
            return;
        }

        String groupAction = args[1].toLowerCase();
        String groupName = args[2];

        if (!main.validateGroupName(groupName, false)) {
            player.sendMessage(prefix + " " + ChatColor.RED + "This group doesn't exist!");
            return;
        }

        List<String> groupList = main.config.getStringList("AllowedGroups");

        if (groupAction.equals("add")) {
            if (!groupList.contains(groupName)) {
                groupList.add(groupName);
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
        } else if (groupAction.equals("remove")) {
            if (groupList.contains(groupName)) {
                groupList.remove(groupName);
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
            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
        }
    }

    private void handleUserAction(ProxiedPlayer player, String[] args) {
        String prefix = main.config.getString("Prefix");
        if (args.length != 4) {
            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
            return;
        }

        String userAction = args[1].toLowerCase();
        String username = args[2];
        String groupName = args[3];

        if (!main.validateGroupName(groupName, true)) {
            player.sendMessage(prefix + " " + ChatColor.RED + "This group doesn't exist! Valid groups are Group1, Group2, and Group3.");
            return;
        }

        List<String> groupToChange = main.config.getStringList(groupName);

        if (userAction.equals("add")) {
            if (!groupToChange.contains(username)) {
                groupToChange.add(username);
                main.config.set(groupName, groupToChange);
                try {
                    main.saveConfig();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                player.sendMessage(prefix + " " + ChatColor.GREEN + "This player has been added to this group.");
            } else {
                player.sendMessage(prefix + " " + ChatColor.RED + "This player is already on the allowed list!");
            }
        } else if (userAction.equals("remove")) {
            if (groupToChange.contains(username)) {
                groupToChange.remove(username);
                main.config.set(groupName, groupToChange);
                try {
                    main.saveConfig();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                player.sendMessage(prefix + " " + ChatColor.GREEN + "This player has been removed from this group.");
            } else {
                player.sendMessage(prefix + " " + ChatColor.RED + "This player isn't on the allowed list.");
            }
        } else {
            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Run /whitelist to get the help message.");
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("group");
            suggestions.add("user");
            suggestions.add("reload");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                suggestions.add("add");
                suggestions.add("remove");
            } else if (args[0].equalsIgnoreCase("user")) {
                suggestions.add("add");
                suggestions.add("remove");
            }
        } else if (args.length == 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            if (args[0].equalsIgnoreCase("group")) {
                // Provide suggestions for group names here
                suggestions.add("Group1");
                suggestions.add("Group2");
                suggestions.add("Group3");
            } else if (args[0].equalsIgnoreCase("user")) {
                suggestions.add("(user name)");
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("user")) {
            // Provide suggestions for group names here
            suggestions.add("Group1");
            suggestions.add("Group2");
            suggestions.add("Group3");
            suggestions.add("Admin");
        }
        return suggestions;
    }
}