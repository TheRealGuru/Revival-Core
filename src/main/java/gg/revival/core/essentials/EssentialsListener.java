package gg.revival.core.essentials;

import gg.revival.core.Revival;
import gg.revival.core.tools.Config;
import gg.revival.driver.MongoAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EssentialsListener implements Listener {

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event) {
        if(Config.DB_ENABLED && !MongoAPI.isConnected())
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The server is still starting up");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        event.setJoinMessage(null);

        Revival.getServerTools().sendFormattedTabList(player, location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        if(!Config.TAB_DISPLAY_STATUS) return;

        if(
                from.getBlockX() == to.getBlockX() &&
                from.getBlockY() == to.getBlockY() &&
                from.getBlockZ() == from.getBlockZ() &&
                Revival.getServerTools().yawToFace(from.getYaw(), true) == Revival.getServerTools().yawToFace(to.getYaw(), true)) return;

        Revival.getServerTools().sendFormattedTabList(player, to.getBlockX(), to.getBlockY(), to.getBlockZ(), to.getYaw());
    }

}
