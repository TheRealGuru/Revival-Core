package gg.revival.core.patches;

import gg.revival.core.Revival;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class PlayerVelocity implements Listener {

    @Getter private Revival revival;

    public PlayerVelocity(Revival revival) {
        this.revival = revival;
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        EntityDamageEvent lastDamageEvent = player.getLastDamageCause();

        if(lastDamageEvent == null || !(lastDamageEvent instanceof EntityDamageByEntityEvent))
            return;

        if(((EntityDamageByEntityEvent) lastDamageEvent).getDamager() instanceof Player)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;

        if(event.isCancelled())
            return;

        Player damaged = (Player)event.getEntity();
        Player damager = (Player)event.getDamager();

        if (damaged.getNoDamageTicks() > damaged.getMaximumNoDamageTicks() / 2D)
            return;

        double horizontalMultiplier = revival.getCfg().PLAYER_VELOCITY_H;
        double verticalMultiplier = revival.getCfg().PLAYER_VELOCITY_V;
        double sprintMultiplier = damager.isSprinting() ? 0.8D : 0.5D;
        double knockbackMultiplier = damager.getItemInHand() == null ? 0 : damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 0.2D;
        double airMultiplier = damaged.isOnGround() ? 1 : 0.5;

        Vector vector = damaged.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();

        vector.setX((vector.getX() * sprintMultiplier + knockbackMultiplier) * horizontalMultiplier);
        vector.setY(0.35D * airMultiplier * verticalMultiplier);
        vector.setZ((vector.getZ() * sprintMultiplier + knockbackMultiplier) * horizontalMultiplier);

        EntityPlayer entityPlayer = ((CraftPlayer)damaged).getHandle();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(damaged.getEntityId(), vector.getX(), vector.getY(), vector.getZ()));
    }

}
