package gg.revival.core;

import gg.revival.core.accounts.AccountListener;
import gg.revival.core.accounts.AccountManager;
import gg.revival.core.chat.Broadcasts;
import gg.revival.core.chat.ChatListener;
import gg.revival.core.chat.Filter;
import gg.revival.core.database.DBManager;
import gg.revival.core.essentials.CommandManager;
import gg.revival.core.punishments.PunishmentListener;
import gg.revival.core.punishments.PunishmentManager;
import gg.revival.core.ranks.RankManager;
import gg.revival.core.tools.Config;
import gg.revival.core.tools.FileManager;
import gg.revival.core.tools.PlayerTools;
import gg.revival.driver.MongoAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Revival extends JavaPlugin
{

    @Getter static Revival core;
    @Getter static RankManager rankManager;
    @Getter static FileManager fileManager;
    @Getter static Broadcasts broadcasts;
    @Getter static Filter chatFilter;
    @Getter static DBManager dbManager;
    @Getter static AccountManager accountManager;
    @Getter static PunishmentManager punishments;
    @Getter static CommandManager commandManager;
    @Getter static PlayerTools playerTools;

    @Override
    public void onEnable()
    {
        core = this;
        rankManager = new RankManager();
        fileManager = new FileManager();
        broadcasts = new Broadcasts();
        chatFilter = new Filter();
        dbManager = new DBManager();
        accountManager = new AccountManager();
        commandManager = new CommandManager();
        punishments = new PunishmentManager();
        playerTools = new PlayerTools();

        fileManager.createFiles();

        Config.loadConfiguration();
        Config.loadRanks();
        Config.loadBroadcasts();
        Config.loadFilteredWords();

        if(Config.DB_ENABLED)
            dbManager.establishConnection();

        if(Config.BROADCASTS_ENABLED && !broadcasts.getLoadedBroadcasts().isEmpty())
            broadcasts.performBroadcast(Config.BROADCASTS_RANDOM, Config.BROADCASTS_INTERVAL);

        loadListeners();
    }

    @Override
    public void onDisable()
    {
        for(Player players : Bukkit.getOnlinePlayers())
        {
            accountManager.saveAccount(accountManager.getAccount(players.getUniqueId()), true, true);
        }

        if(MongoAPI.isConnected())
            MongoAPI.disconnect();

        core = null;
        rankManager = null;
        fileManager = null;
        broadcasts = null;
        chatFilter = null;
        accountManager = null;
        commandManager = null;
        punishments = null;
        playerTools = null;
    }

    /**
     * Loads all listeners
     */
    public void loadListeners()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new AccountListener(), this);
        pluginManager.registerEvents(new PunishmentListener(), this);
    }

}
