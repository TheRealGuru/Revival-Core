package gg.revival.core.tools;

import gg.revival.core.Revival;
import gg.revival.core.ranks.Rank;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    @Getter private Revival revival;

    public Config(Revival revival) {
        this.revival = revival;
    }

    public boolean DB_ENABLED = false;
    public String DB_HOST = "localhost";
    public int DB_PORT = 27017;
    public boolean DB_CREDS = false;
    public String DB_USERNAME = "root";
    public String DB_PASSWORD = "password";
    public String DB_DATABASE = "revival";

    public boolean BROADCASTS_ENABLED = true;
    public boolean BROADCASTS_RANDOM = true;
    public int BROADCASTS_INTERVAL = 60;
    public String BROADCASTS_PREFIX = ChatColor.GOLD + "Broadcast: " + ChatColor.RESET;

    public boolean CHAT_FILTER_ENABLED = true;
    public int CHAT_FILTER_INTERVAL = 3;

    public boolean TICKETS_ENABLED = true;
    public int TICKETS_COOLDOWN = 30;

    public boolean DISABLE_HUB_COMMAND = false;

    public boolean TAB_ENABLED = true;
    public String TAB_HEADER = "Default Header";
    public String TAB_FOOTER = "Default Footer";
    public boolean TAB_DISPLAY_STATUS = true;

    public double PLAYER_VELOCITY_H = 1.0;
    public double PLAYER_VELOCITY_V = 1.0;

    public Map<String, List<String>> HELP_TOPICS = new HashMap<>();

    /**
     * Loads configuration from config.yml
     */
    public void loadConfiguration() {
        DB_ENABLED = revival.getConfig().getBoolean("database.enabled");
        DB_HOST = revival.getConfig().getString("database.hostname");
        DB_PORT = revival.getConfig().getInt("database.port");
        DB_CREDS = revival.getConfig().getBoolean("database.creds.enabled");
        DB_USERNAME = revival.getConfig().getString("database.creds.username");
        DB_PASSWORD = revival.getConfig().getString("database.creds.password");
        DB_DATABASE = revival.getConfig().getString("database.creds.database");

        BROADCASTS_ENABLED = revival.getConfig().getBoolean("broadcasts.enabled");
        BROADCASTS_RANDOM = revival.getConfig().getBoolean("broadcasts.random");
        BROADCASTS_INTERVAL = revival.getConfig().getInt("broadcasts.interval");
        BROADCASTS_PREFIX = ChatColor.translateAlternateColorCodes('&', revival.getConfig().getString("broadcasts.prefix"));

        CHAT_FILTER_ENABLED = revival.getConfig().getBoolean("chat-filter.enabled");
        CHAT_FILTER_INTERVAL = revival.getConfig().getInt("chat-filter.interval");

        TICKETS_ENABLED = revival.getConfig().getBoolean("tickets.enabled");
        TICKETS_COOLDOWN = revival.getConfig().getInt("tickets.cooldown");

        DISABLE_HUB_COMMAND = revival.getConfig().getBoolean("command-settings.disable-hub-command");

        TAB_ENABLED = revival.getConfig().getBoolean("tab.enabled");
        TAB_HEADER = ChatColor.translateAlternateColorCodes('&', revival.getConfig().getString("tab.header"));
        TAB_FOOTER = ChatColor.translateAlternateColorCodes('&', revival.getConfig().getString("tab.footer"));
        TAB_DISPLAY_STATUS = revival.getConfig().getBoolean("tab.display-status");

        PLAYER_VELOCITY_H = revival.getConfig().getDouble("patches.player-velocity.h");
        PLAYER_VELOCITY_V = revival.getConfig().getDouble("patches.player-velocity.v");

        ConfigurationSection helpTopicSection = revival.getConfig().getConfigurationSection("help-topics");

        for(String topics : helpTopicSection.getKeys(false)) {
            String displayName = revival.getConfig().getString("help-topics." + topics + ".display-name");
            List<String> unformattedLore = revival.getConfig().getStringList("help-topics." + topics + ".page");
            List<String> formattedLore = new ArrayList<>();

            for(String lore : unformattedLore)
                formattedLore.add(ChatColor.translateAlternateColorCodes('&', lore));

            HELP_TOPICS.put(displayName, formattedLore);
        }

        Logger.log("Loaded " + HELP_TOPICS.size() + " Help Topics");
    }

    /**
     * Loads filtered words from config.yml
     */
    public void loadFilteredWords() {
        revival.getChatFilter().getBadWords().addAll(revival.getConfig().getStringList("chat-filter.filter-out"));
        Logger.log("Loaded " + revival.getChatFilter().getBadWords().size() + " Chat Filters");
    }

    /**
     * Loads ranks from ranks.yml
     */
    public void loadRanks() {
        ConfigurationSection section = revival.getFileManager().getRanks().getConfigurationSection("ranks");

        for(String ranks : section.getKeys(false)) {
            String name = revival.getFileManager().getRanks().getString("ranks." + ranks + ".name");
            String tag = revival.getFileManager().getRanks().getString("ranks." + ranks + ".tag");
            String permission = revival.getFileManager().getRanks().getString("ranks." + ranks + ".permission");

            Rank rank = new Rank(name, tag, permission);

            revival.getRankManager().getRanks().add(rank);
        }

        Logger.log("Loaded " + revival.getRankManager().getRanks().size() + " Ranks");
    }

    /**
     * Loads broadcasts from broadcasts.yml
     */
    public void loadBroadcasts() {
        List<String> unformatted = revival.getFileManager().getBroadcasts().getStringList("broadcasts");
        List<String> broadcasts = new ArrayList<>();

        for(String broadcast : unformatted)
            broadcasts.add(ChatColor.translateAlternateColorCodes('&', broadcast));

        revival.getBroadcasts().getLoadedBroadcasts().addAll(broadcasts);
        Logger.log("Loaded " + revival.getBroadcasts().getLoadedBroadcasts().size() + " Broadcasts");
    }

}
