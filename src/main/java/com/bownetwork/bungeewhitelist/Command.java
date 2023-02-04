package com.bownetwork.bungeewhitelist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import java.io.IOException;
import java.util.List;
import static com.bownetwork.bungeewhitelist.Main.*;

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
            Configuration config = null;
            try {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                        loadResource(main, "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String prefix = config.getString("Prefix");
            if (player.hasPermission("whitelist.cmd")) {
                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(prefix + " " + ChatColor.DARK_AQUA + "Whitelist Command Help");
                        player.sendMessage(ChatColor.AQUA + "/whitelist help: See this message.");
                        player.sendMessage(ChatColor.AQUA + "/whitelist addplayer (player) (group): Add player to group.");
                        player.sendMessage(ChatColor.AQUA + "/whitelist removeplayer (player) (group): Remove player from group.");
                        player.sendMessage(ChatColor.AQUA + "/whitelist allowgroup (group): Allow all players in a certain group to join.");
                        player.sendMessage(ChatColor.AQUA + "/whitelist disallowgroup (group): Don't allow all players in certain a group to join.");
                        player.sendMessage(ChatColor.AQUA + "/whitelist reload: Reload the plugin's config.");
                    } else if (args[0].equalsIgnoreCase("addplayer")) {
                        if (args.length == 3) {
                            List group = config.getStringList(args[2]);
                            group.add(args[1]);
                            config.set(args[2], group);
                            player.sendMessage(prefix + " " + ChatColor.GREEN + "Added " + args[1] + " to " + args[2] + ".");
                        } else {
                            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Please run /whitelist help for usage.");
                        }
                    } else if (args[0].equalsIgnoreCase("removeplayer")) {
                        if (args.length == 3) {
                            List group = config.getStringList(args[2]);
                            try {
                                group.remove(args[1]);
                            } catch (NullPointerException e) {
                                player.sendMessage(ChatColor.RED + "There was an error while removing this player. If you are an administrator, check console for more info.");
                                e.printStackTrace();
                            }
                            config.set(args[2], group);
                            player.sendMessage(prefix + " " + ChatColor.GREEN + "Removed " + args[1] + " from " + args[2] + ".");
                        } else {
                            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Please run /whitelist help for usage.");
                        }
                    } else if (args[0].equalsIgnoreCase("allowgroup")) {
                        if (args.length == 2) {
                            List allowedgroups = config.getStringList(args[1]);
                            allowedgroups.add(args[1]);
                            config.set("AllowedGroups", allowedgroups);
                            try {
                                ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                                        loadResource(main, "config.yml"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            player.sendMessage(prefix + " " + ChatColor.GREEN + "Added " + args[1] + " to the Allowed Groups.");
                        } else if (args[0].equalsIgnoreCase("disallowgroup")) {
                            if (args.length == 2) {
                                List allowedgroups = config.getStringList(args[1]);
                                try {
                                    allowedgroups.remove(args[1]);
                                } catch (NullPointerException e) {
                                    player.sendMessage(prefix + " " + ChatColor.RED + "There was an error while removing this group. If you are an administrator, check console for more info.");
                                    e.printStackTrace();
                                }
                                config.set("AllowedGroups", allowedgroups);
                                player.sendMessage(prefix + " " + ChatColor.GREEN + "Removed " + args[1] + " from the Allowed Groups.");
                            } else {
                                player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Please run /whitelist help for usage.");
                            }
                        } else if (args[0].equalsIgnoreCase("reload")) {
                            try {
                                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                                        loadResource(main, "config.yml"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                                player.sendMessage(prefix + " " + ChatColor.GREEN + "Reloaded.");
                            } else {
                                player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Please run /whitelist help for usage.");
                            }
                        } else {
                            player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Please run /whitelist help for usage.");
                        }
                    } else {
                        player.sendMessage(prefix + " " + ChatColor.RED + "Incorrect usage! Please run /whitelist help for usage.");
                }
            } else {
                player.sendMessage(prefix + " " + ChatColor.RED + "You don't have permission to do this!");
            }
        } else {
            System.out.println("Console can't run this!");
        }
    }
}
