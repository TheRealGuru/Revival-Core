package gg.revival.core.tools;

import gg.revival.core.Revival;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerTools
{

    /**
     * Returns a callback containing an OfflinePlayer lookup for UUID and Username
     * @param name Username to ping the Mojang API
     * @param callback Callback result containing UUID and Username
     */
    public void getOfflinePlayer(String name, OfflinePlayerCallback callback)
    {
        if(Bukkit.getPlayer(name) != null)
        {
            Player player = Bukkit.getPlayer(name);
            UUID uuid = player.getUniqueId();
            String username = player.getName();

            callback.onQueryDone(uuid, username);
        }

        new BukkitRunnable()
        {
            public void run()
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

                if(offlinePlayer != null)
                {
                    UUID uuid = offlinePlayer.getUniqueId();
                    String username = offlinePlayer.getName();

                    new BukkitRunnable()
                    {
                        public void run()
                        {
                            callback.onQueryDone(uuid, username);
                        }
                    }.runTask(Revival.getCore());
                }

                else
                {
                    new BukkitRunnable()
                    {
                        public void run()
                        {
                            callback.onQueryDone(null, null);
                        }
                    }.runTask(Revival.getCore());
                }
            }
        }.runTaskAsynchronously(Revival.getCore());
    }

}
