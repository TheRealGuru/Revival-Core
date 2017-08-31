package gg.revival.core.tools;

import gg.revival.core.Revival;
import gg.revival.core.ranks.Rank;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class Config
{

    public static boolean DB_ENABLED = false;
    public static String DB_HOST = "localhost";
    public static int DB_PORT = 27017;
    public static boolean DB_CREDS = false;
    public static String DB_USERNAME = "root";
    public static String DB_PASSWORD = "password";
    public static String DB_DATABASE = "revival";

    public static boolean BROADCASTS_ENABLED = true;
    public static boolean BROADCASTS_RANDOM = true;
    public static int BROADCASTS_INTERVAL = 60;
    public static String BROADCASTS_PREFIX = ChatColor.GOLD + "Broadcast: " + ChatColor.RESET;

    public static boolean CHAT_FILTER_ENABLED = true;
    public static int CHAT_FILTER_INTERVAL = 3;

    /**
     * Loads configuration from config.yml
     */
    public static void loadConfiguration()
    {
        DB_ENABLED = Revival.getFileManager().getConfig().getBoolean("database.enabled");
        DB_HOST = Revival.getFileManager().getConfig().getString("database.hostname");
        DB_PORT = Revival.getFileManager().getConfig().getInt("database.port");
        DB_CREDS = Revival.getFileManager().getConfig().getBoolean("database.creds.enabled");
        DB_USERNAME = Revival.getFileManager().getConfig().getString("database.creds.username");
        DB_PASSWORD = Revival.getFileManager().getConfig().getString("database.creds.password");
        DB_DATABASE = Revival.getFileManager().getConfig().getString("database.creds.database");

        BROADCASTS_ENABLED = Revival.getFileManager().getConfig().getBoolean("broadcasts.enabled");
        BROADCASTS_RANDOM = Revival.getFileManager().getConfig().getBoolean("broadcasts.random");
        BROADCASTS_INTERVAL = Revival.getFileManager().getConfig().getInt("broadcasts.interval");
        BROADCASTS_PREFIX = ChatColor.translateAlternateColorCodes('&', Revival.getFileManager().getConfig().getString("broadcasts.prefix"));

        CHAT_FILTER_ENABLED = Revival.getFileManager().getConfig().getBoolean("chat-filter.enabled");
        CHAT_FILTER_INTERVAL = Revival.getFileManager().getConfig().getInt("chat-filter.interval");
    }

    /**
     * Loads filtered words from config.yml
     */
    public static void loadFilteredWords()
    {
        Revival.getChatFilter().getBadWords().addAll(Revival.getFileManager().getConfig().getStringList("chat-filter.filter-out"));

        Logger.log("Loaded " + Revival.getChatFilter().getBadWords().size() + " Chat Filters");
    }

    /**
     * Loads ranks from ranks.yml
     */
    public static void loadRanks()
    {
        ConfigurationSection section = Revival.getFileManager().getRanks().getConfigurationSection("ranks");

        for(String ranks : section.getKeys(false))
        {
            String name = Revival.getFileManager().getRanks().getString("ranks." + ranks + ".name");
            String tag = Revival.getFileManager().getRanks().getString("ranks." + ranks + ".tag");
            String permission = Revival.getFileManager().getRanks().getString("ranks." + ranks + ".permission");

            Rank rank = new Rank(name, tag, permission);

            Revival.getRankManager().getRanks().add(rank);
        }

        Logger.log("Loaded " + Revival.getRankManager().getRanks().size() + " Ranks");
    }

    /**
     * Loads broadcasts from broadcasts.yml
     */
    public static void loadBroadcasts()
    {
        List<String> unformatted = Revival.getFileManager().getBroadcasts().getStringList("broadcasts");
        List<String> broadcasts = new ArrayList<>();

        for(String broadcast : unformatted)
        {
            broadcasts.add(ChatColor.translateAlternateColorCodes('&', broadcast));
        }

        Revival.getBroadcasts().getLoadedBroadcasts().addAll(broadcasts);

        Logger.log("Loaded " + Revival.getBroadcasts().getLoadedBroadcasts().size() + " Broadcasts");
    }

}
