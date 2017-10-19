package gg.revival.core;

import gg.revival.core.accounts.AccountListener;
import gg.revival.core.accounts.AccountManager;
import gg.revival.core.chat.Broadcasts;
import gg.revival.core.chat.ChatListener;
import gg.revival.core.chat.Filter;
import gg.revival.core.chat.MessageManager;
import gg.revival.core.database.DBManager;
import gg.revival.core.essentials.CommandManager;
import gg.revival.core.essentials.EssentialsListener;
import gg.revival.core.network.NetworkListener;
import gg.revival.core.punishments.PunishmentListener;
import gg.revival.core.punishments.PunishmentManager;
import gg.revival.core.ranks.RankManager;
import gg.revival.core.staff.FreezeListener;
import gg.revival.core.staff.FreezeManager;
import gg.revival.core.staff.ModeratorListener;
import gg.revival.core.staff.StaffManager;
import gg.revival.core.tickets.Ticket;
import gg.revival.core.tickets.TicketGUI;
import gg.revival.core.tickets.TicketListener;
import gg.revival.core.tickets.TicketManager;
import gg.revival.core.tools.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Revival extends JavaPlugin {

    @Getter public static Revival core;
    @Getter public RankManager rankManager;
    @Getter public FileManager fileManager;
    @Getter public Broadcasts broadcasts;
    @Getter public Filter chatFilter;
    @Getter public DBManager databaseManager;
    @Getter public AccountManager accountManager;
    @Getter public PunishmentManager punishments;
    @Getter public CommandManager commandManager;
    @Getter public FreezeManager freezeManager;
    @Getter public MessageManager messageManager;
    @Getter public TicketManager ticketManager;
    @Getter public StaffManager staffManager;
    @Getter public TicketGUI ticketGui;
    @Getter public PlayerTools playerTools;
    @Getter public ServerTools serverTools;
    @Getter public TimeTools timeTools;
    @Getter public ItemTools itemTools;
    @Getter public MsgTools msgTools;
    @Getter public Processor processor;
    @Getter public Config cfg;
    @Getter public Log log;

    @Override
    public void onEnable() {
        core = this;
        rankManager = new RankManager();
        fileManager = new FileManager();
        broadcasts = new Broadcasts(this);
        chatFilter = new Filter();
        databaseManager = new DBManager(this);
        accountManager = new AccountManager(this);
        commandManager = new CommandManager(this);
        punishments = new PunishmentManager(this);
        freezeManager = new FreezeManager();
        messageManager = new MessageManager(this);
        ticketManager = new TicketManager(this);
        staffManager = new StaffManager(this);
        ticketGui = new TicketGUI(this);
        playerTools = new PlayerTools();
        serverTools = new ServerTools(this);
        timeTools = new TimeTools();
        itemTools = new ItemTools();
        msgTools = new MsgTools(this);
        processor = new Processor(this);
        cfg = new Config(this);
        log = new Log();

        fileManager.createFiles();

        cfg.loadConfiguration();
        cfg.loadRanks();
        cfg.loadBroadcasts();
        cfg.loadFilteredWords();

        if(cfg.DB_ENABLED)
            databaseManager.establishConnection();

        if(cfg.TICKETS_ENABLED)
            ticketManager.pullUpdates(false);

        broadcasts.performBroadcast(cfg.BROADCASTS_RANDOM, cfg.BROADCASTS_INTERVAL);

        loadListeners();
        loadPluginChannels();
    }

    @Override
    public void onDisable() {
        for(Player players : Bukkit.getOnlinePlayers())
            accountManager.saveAccount(accountManager.getAccount(players.getUniqueId()), true, true);

        if(cfg.TICKETS_ENABLED) {
            for(Ticket tickets : ticketManager.getLoadedTickets())
                ticketManager.saveTicket(tickets, true);
        }

        core = null;
        rankManager = null;
        fileManager = null;
        broadcasts = null;
        chatFilter = null;
        accountManager = null;
        commandManager = null;
        freezeManager = null;
        messageManager = null;
        ticketManager = null;
        ticketGui = null;
        punishments = null;
        playerTools = null;
        serverTools = null;
        timeTools = null;
        itemTools = null;
        msgTools = null;
        processor = null;
        log = null;
    }

    /**
     * Loads all listeners
     */
    private void loadListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ChatListener(this), this);
        pluginManager.registerEvents(new AccountListener(this), this);
        pluginManager.registerEvents(new PunishmentListener(this), this);
        pluginManager.registerEvents(new FreezeListener(this), this);
        pluginManager.registerEvents(new ModeratorListener(this), this);
        pluginManager.registerEvents(new EssentialsListener(this), this);
        pluginManager.registerEvents(new TicketListener(this), this);
    }

    /**
     * Loads Plugin-Message channels to communicate with clients and other servers running on our network
     */
    private void loadPluginChannels() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new NetworkListener(this));
    }
}
