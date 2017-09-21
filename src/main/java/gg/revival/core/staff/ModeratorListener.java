package gg.revival.core.staff;

import gg.revival.core.tools.Permissions;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ModeratorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player)event.getWhoClicked();

        if(player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();

            if(player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS))
                event.setCancelled(true);
        }

        if(event.getDamager() instanceof Player) {
            Player player = (Player)event.getDamager();

            if(player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjLaunch(ProjectileLaunchEvent event) {
        if(event.getEntity().getShooter() instanceof Player) {
            Player player = (Player)event.getEntity().getShooter();

            if(player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS)) {
            player.setGameMode(GameMode.SPECTATOR);
            player.setFlySpeed(player.getFlySpeed() * 4);
        }
    }

}
