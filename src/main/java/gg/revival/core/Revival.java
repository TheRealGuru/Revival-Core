package gg.revival.core;

import gg.revival.core.chat.Broadcasts;
import gg.revival.core.chat.ChatListener;
import gg.revival.core.chat.Filter;
import gg.revival.core.ranks.RankManager;
import gg.revival.core.tools.Config;
import gg.revival.core.tools.FileManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Revival extends JavaPlugin
{

    @Getter static Revival core;
    @Getter static RankManager rankManager;
    @Getter static FileManager fileManager;
    @Getter static Broadcasts broadcasts;
    @Getter static Filter chatFilter;

    @Override
    public void onEnable()
    {
        core = this;
        rankManager = new RankManager();
        fileManager = new FileManager();
        broadcasts = new Broadcasts();
        chatFilter = new Filter();

        fileManager.createFiles();

        Config.loadConfiguration();
        Config.loadRanks();
        Config.loadBroadcasts();
        Config.loadFilteredWords();

        if(Config.BROADCASTS_ENABLED && !broadcasts.getLoadedBroadcasts().isEmpty())
            broadcasts.performBroadcast(Config.BROADCASTS_RANDOM, Config.BROADCASTS_INTERVAL);

        loadListeners();
    }

    @Override
    public void onDisable()
    {
        core = null;
        rankManager = null;
        fileManager = null;
        broadcasts = null;
        chatFilter = null;
    }

    /**
     * Loads all listeners
     */
    public void loadListeners()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ChatListener(), this);
    }

}
