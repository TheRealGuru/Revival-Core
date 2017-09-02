package gg.revival.core.tools;

import gg.revival.core.Revival;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezeManager
{

    /**
     * Contains all frozen players
     */
    @Getter Map<UUID, Location> frozenPlayers = new HashMap<>();

    /**
     * Returns the players original location if they're frozen
     * @param uuid The player UUID
     * @return The original Location
     */
    public Location getLocation(UUID uuid)
    {
        if(frozenPlayers.containsKey(uuid))
            return frozenPlayers.get(uuid);

        return null;
    }

    /**
     * Returns true if the given player UUID is frozen
     * @param uuid The player UUID
     * @return The player is frozen
     */
    public boolean isFrozen(UUID uuid)
    {
        if(getLocation(uuid) != null)
            return true;

        return false;
    }

    /**
     * Sets a player frozen
     * @param player
     */
    public void freezePlayer(Player player)
    {
        frozenPlayers.put(player.getUniqueId(), player.getLocation());

        player.sendMessage(ChatColor.DARK_RED + "You have been frozen!");

        checkLocation(player);
    }

    /**
     * Unfreezes a player
     * @param player
     */
    public void unfreezePlayer(Player player)
    {
        frozenPlayers.remove(player.getUniqueId());

        player.sendMessage(ChatColor.GREEN + "You have been unfrozen!");
    }

    /**
     * Loop method that constantly keeps checking if the player has moved until they are offline or no longer frozen
     * @param player
     */
    public void checkLocation(Player player)
    {
        if(player == null) return;

        Location location = getLocation(player.getUniqueId());

        if(location == null) return;

        if(player.getLocation().distance(location) >= 1.0)
        {
            player.teleport(location);
            player.sendMessage(ChatColor.RED + "You are not allowed to move while frozen");
        }

        new BukkitRunnable()
        {
            public void run()
            {
                checkLocation(player);
            }
        }.runTaskLater(Revival.getCore(), 20L);
    }

}
