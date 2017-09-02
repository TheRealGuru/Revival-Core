package gg.revival.core.tools;

import gg.revival.core.Revival;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class FreezeListener implements Listener
{

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if(damaged instanceof Player)
        {
            Player player = (Player)damaged;

            if(Revival.getFreezeManager().isFrozen(player.getUniqueId()))
            {
                if(damager instanceof Player)
                {
                    Player damagerPlayer = (Player)damager;

                    damagerPlayer.sendMessage(ChatColor.RED + "You are not allowed to perform this task while this player is frozen");
                }

                event.setCancelled(true);

                return;
            }
        }

        if(damager instanceof Player)
        {
            Player player = (Player)damager;

            if(Revival.getFreezeManager().isFrozen(player.getUniqueId()))
            {
                player.sendMessage(ChatColor.RED + "You are not allowed to perform this task while frozen");
                event.setCancelled(true);

                return;
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player)event.getEntity();

        if(Revival.getFreezeManager().isFrozen(player.getUniqueId()))
        {
            event.setCancelled(true);

            return;
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();

        if(Revival.getFreezeManager().isFrozen(player.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + "You are not allowed to perform this task while frozen");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event)
    {
        Player player = event.getPlayer();

        if(Revival.getFreezeManager().isFrozen(player.getUniqueId()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if(Revival.getFreezeManager().isFrozen(player.getUniqueId()))
        {
            if(!event.getAction().equals(Action.PHYSICAL))
                player.sendMessage(ChatColor.RED + "You are not allowed to perform this task while frozen");

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        Player player = event.getPlayer();

        if(Revival.getFreezeManager().isFrozen(player.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + "You are not allowed to perform this task while frozen");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();

        if(Revival.getFreezeManager().isFrozen(player.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + "You are not allowed to perform this task while frozen");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();

        if(Revival.getFreezeManager().isFrozen(player.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + "You are not allowed to perform this task while frozen");
            event.setCancelled(true);
        }
    }

}
