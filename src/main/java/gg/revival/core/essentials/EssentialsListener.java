package gg.revival.core.essentials;

import gg.revival.core.Revival;
import gg.revival.driver.MongoAPI;
import lombok.Getter;
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

    @Getter private Revival revival;

    public EssentialsListener(Revival revival) {
        this.revival = revival;
    }

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event) {
        if(revival.getCfg().DB_ENABLED && !MongoAPI.isConnected())
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "The server is still starting up");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        event.setJoinMessage(null);

        revival.getServerTools().sendFormattedTabList(player, location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();

        if(!revival.getCfg().TAB_DISPLAY_STATUS) return;

        if(
                from.getBlockX() == to.getBlockX() &&
                from.getBlockY() == to.getBlockY() &&
                from.getBlockZ() == to.getBlockZ() &&
                        revival.getServerTools().yawToFace(from.getYaw(), true) == revival.getServerTools().yawToFace(to.getYaw(), true)) return;

        revival.getServerTools().sendFormattedTabList(player, to.getBlockX(), to.getBlockY(), to.getBlockZ(), to.getYaw());
    }

}
