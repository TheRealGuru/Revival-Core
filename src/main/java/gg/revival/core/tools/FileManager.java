package gg.revival.core.tools;

import gg.revival.core.Revival;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public File configFile;
    public File rankFile;
    public File broadcastFile;
    public File messageFile;
    public FileConfiguration configConfig;
    public FileConfiguration rankConfig;
    public FileConfiguration broadcastConfig;
    public FileConfiguration messageConfig;

    public void createFiles() {
        try {
            if (!Revival.getCore().getDataFolder().exists()) {
                Revival.getCore().getDataFolder().mkdirs();
            }

            configFile = new File(Revival.getCore().getDataFolder(), "config.yml");
            rankFile = new File(Revival.getCore().getDataFolder(), "ranks.yml");
            broadcastFile = new File(Revival.getCore().getDataFolder(), "braodcasts.yml");
            messageFile = new File(Revival.getCore().getDataFolder(), "messages.yml");

            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                Revival.getCore().saveResource("config.yml", true);
            }

            if (!rankFile.exists()) {
                rankFile.getParentFile().mkdirs();
                Revival.getCore().saveResource("ranks.yml", true);
            }

            if(!broadcastFile.exists()) {
                broadcastFile.getParentFile().mkdirs();
                Revival.getCore().saveResource("broadcasts.yml", true);
            }

            if(!messageFile.exists()) {
                messageFile.getParentFile().mkdirs();
                Revival.getCore().saveResource("messages.yml", true);
            }

            configConfig = new YamlConfiguration();
            rankConfig = new YamlConfiguration();
            broadcastConfig = new YamlConfiguration();
            messageConfig = new YamlConfiguration();

            try {
                configConfig.load(configFile);
                rankConfig.load(rankFile);
                broadcastConfig.load(broadcastFile);
                messageConfig.load(messageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return configConfig;
    }

    public FileConfiguration getRanks() {
        return rankConfig;
    }

    public FileConfiguration getBroadcasts() {
        return broadcastConfig;
    }

    public FileConfiguration getMessages() {
        return messageConfig;
    }

    public void saveConfig() {
        try {
            configConfig.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRanks() {
        try {
            rankConfig.save(rankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBroadcasts() {
        try {
            broadcastConfig.save(broadcastFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMessages() {
        try {
            messageConfig.save(messageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadFiles() {
        try {
            if (!Revival.getCore().getDataFolder().exists()) {
                Revival.getCore().getDataFolder().mkdirs();
            }

            configFile = new File(Revival.getCore().getDataFolder(), "config.yml");
            rankFile = new File(Revival.getCore().getDataFolder(), "ranks.yml");
            broadcastFile = new File(Revival.getCore().getDataFolder(), "broadcasts.yml");

            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                Revival.getCore().saveResource("config.yml", true);
            }

            if (!rankFile.exists()) {
                rankFile.getParentFile().mkdirs();
                Revival.getCore().saveResource("ranks.yml", true);
            }

            if (!broadcastFile.exists()) {
                broadcastFile.getParentFile().mkdirs();
                Revival.getCore().saveResource("broadcasts.yml", true);
            }

            if(!messageFile.exists()) {
                messageFile.getParentFile().mkdirs();
                Revival.getCore().saveResource("messages.yml", true);
            }

            configConfig = new YamlConfiguration();
            rankConfig = new YamlConfiguration();
            broadcastConfig = new YamlConfiguration();
            messageConfig = new YamlConfiguration();

            try {
                configConfig.load(configFile);
                rankConfig.load(rankFile);
                broadcastConfig.load(broadcastFile);
                messageConfig.load(messageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}