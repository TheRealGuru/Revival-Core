package gg.revival.core.staff;

import com.google.common.collect.Sets;
import gg.revival.core.Revival;
import gg.revival.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.UUID;

public class StaffManager {

    @Getter private Revival revival;
    @Getter public Set<UUID> vanished = Sets.newConcurrentHashSet();
    @Getter public Set<UUID> transparent = Sets.newConcurrentHashSet();

    public StaffManager(Revival revival) {
        this.revival = revival;
    }

    public void setTransparent(Player player) {
        if(transparent.contains(player.getUniqueId()))
            transparent.remove(player.getUniqueId());
        else
            transparent.add(player.getUniqueId());
    }

    public boolean isTransparent(Player player) {
        return transparent.contains(player.getUniqueId());
    }

    public void hidePlayer(Player player) {
        for(Player players : Bukkit.getOnlinePlayers()) {
            if(!players.canSee(player)) continue;
            if(players.hasPermission(Permissions.MOD_TOOLS) || players.hasPermission(Permissions.ADMIN_TOOLS)) continue;

            players.hidePlayer(player);
        }

        vanished.add(player.getUniqueId());

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        player.spigot().setCollidesWithEntities(false);
    }

    public void showPlayer(Player player) {
        for(Player players : Bukkit.getOnlinePlayers()) {
            if(players.canSee(player)) continue;
            players.showPlayer(player);
        }

        vanished.remove(player.getUniqueId());

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.spigot().setCollidesWithEntities(true);
    }

}
