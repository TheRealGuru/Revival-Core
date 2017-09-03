package gg.revival.core.essentials;

import gg.revival.core.tools.Config;
import gg.revival.driver.MongoAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EssentialsListener implements Listener
{

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event)
    {
        if(Config.DB_ENABLED && !MongoAPI.isConnected())
        {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "The server is still starting up");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        event.setQuitMessage(null);
    }

}
