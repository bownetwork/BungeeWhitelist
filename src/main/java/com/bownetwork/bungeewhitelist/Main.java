package com.bownetwork.bungeewhitelist;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.List;

public final class Main extends Plugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getProxy().getPluginManager().registerListener(this, this);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Command(this));
        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                    loadResource(this, "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("BungeeWhitelist has been loaded.");
    }

    public static File loadResource(Plugin plugin, String resource) {
        File folder = plugin.getDataFolder();
        if (!folder.exists())
            folder.mkdir();
        File resourceFile = new File(folder, resource);
        try {
            if (!resourceFile.exists()) {
                resourceFile.createNewFile();
                try (InputStream in = plugin.getResourceAsStream(resource);
                     OutputStream out = new FileOutputStream(resourceFile)) {
                    ByteStreams.copy(in, out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceFile;
    }

    public Configuration configuration;

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("BungeeWhitelist has been unloaded.");
    }

    @EventHandler
    public void onLogin(PostLoginEvent e) throws IOException {
        Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                loadResource(this, "config.yml"));
        List<String> admin = config.getStringList("Admin");
        List<String> group1 = config.getStringList("Group1");
        List<String> group2 = config.getStringList("Group2");
        List<String> group3 = config.getStringList("Group3");
        List<String> AllowedGroups = config.getStringList("AllowedGroups");
        String KickMessage = ChatColor.RED + "You aren't in a group, or your group isn't currently allowed. Please contact us if you have any questions.";
        String prefix = config.getString("Prefix");
        if (admin.contains(e.getPlayer().getName())) {
                e.getPlayer().sendMessage(prefix + " " + ChatColor.AQUA + "Welcome, " + e.getPlayer().getName() + "! You have logged in under Admin.");
        } else if (group1.contains(e.getPlayer().getName())) {
            if (AllowedGroups.contains("Group1")) {
                e.getPlayer().sendMessage(prefix + " " + ChatColor.AQUA + "Welcome, " + e.getPlayer().getName() + "! You have logged in under Group 1.");
            } else {
                e.getPlayer().disconnect(KickMessage);
            }
        } else if (group2.contains(e.getPlayer().getName())) {
            if (AllowedGroups.contains("Group2")) {
                e.getPlayer().sendMessage(prefix + " " + ChatColor.AQUA + "Welcome, " + e.getPlayer().getName() + "! You have logged in under Group 2.");
            } else {
                e.getPlayer().disconnect(KickMessage);
            }
        } else if (group3.contains(e.getPlayer().getName())) {
            if (AllowedGroups.contains("Group3")) {
                e.getPlayer().sendMessage(prefix + " " + ChatColor.AQUA + "Welcome, " + e.getPlayer().getName() + "! You have logged in under Group 3.");
            } else {
                e.getPlayer().disconnect(KickMessage);
            }
        } else {
            e.getPlayer().disconnect(KickMessage);
        }
    }
}

