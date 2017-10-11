package gg.revival.core.staff;

import gg.revival.core.Revival;
import gg.revival.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class ModeratorListener implements Listener {

    @Getter private Revival revival;

    public ModeratorListener(Revival revival) {
        this.revival = revival;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player)event.getWhoClicked();

        if(player.getInventory().equals(event.getClickedInventory())) return;

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

        if(!player.hasPermission(Permissions.MOD_TOOLS) && !player.hasPermission(Permissions.ADMIN_TOOLS)) return;

        revival.getStaffManager().hidePlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(revival.getStaffManager().getVanished().contains(player.getUniqueId()))
            revival.getStaffManager().showPlayer(player);

        if(revival.getStaffManager().isTransparent(player))
            revival.getStaffManager().setTransparent(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom(), to = event.getTo();
        GameMode gameMode = player.getGameMode();

        if(from.getX() == to.getX() && from.getZ() == to.getZ()) return;

        if(!revival.getStaffManager().getTransparent().contains(player.getUniqueId())) return;

        if(to.getBlock().getType().isSolid() || to.clone().add(0, 0.5, 0).getBlock().getType().isSolid()) {
            if(!gameMode.equals(GameMode.SPECTATOR)) player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        if(to.clone().add(0.5, 0, 0).getBlock().getType().isSolid() || to.clone().subtract(0.5, 0, 0).getBlock().getType().isSolid()) {
            if(!gameMode.equals(GameMode.SPECTATOR)) player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        if(to.clone().add(0, 0.5, 0).getBlock().getType().isSolid() || to.clone().subtract(0, 0.5, 0).getBlock().getType().isSolid()) {
            if(!gameMode.equals(GameMode.SPECTATOR)) player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        if(to.clone().add(0.0, 0, 0.5).getBlock().getType().isSolid() || to.clone().subtract(0, 0, 0.5).getBlock().getType().isSolid()) {
            if(!gameMode.equals(GameMode.SPECTATOR)) player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        if(!gameMode.equals(GameMode.CREATIVE)) player.setGameMode(GameMode.CREATIVE);
    }

}
