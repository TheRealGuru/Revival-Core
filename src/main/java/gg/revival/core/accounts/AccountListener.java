package gg.revival.core.accounts;

import gg.revival.core.Revival;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.IPTools;
import gg.revival.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class AccountListener implements Listener {

    @Getter private Revival revival;

    public AccountListener(Revival revival) {
        this.revival = revival;
    }

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        revival.getAccountManager().getAccount(uuid, true, true, result -> {
            if(result == null || result.getPunishments().isEmpty()) return;

            for(Punishment punishment : result.getPunishments()) {
                if(punishment.getType().equals(PunishType.BAN)) {
                    if(punishment.isForever() || !punishment.isExpired()) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, revival.getMsgTools().getBanMessage(punishment));
                        return;
                    }

                    continue;
                }

                if(punishment.getType().equals(PunishType.MUTE)) {
                    if(punishment.isForever() || !punishment.isExpired())
                        revival.getPunishments().getActiveMutes().put(uuid, punishment);
                }
            }

            result.setAddress(IPTools.ipStringToInteger(event.getAddress().getHostAddress()));
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        revival.getPunishments().scanAddress(IPTools.ipStringToInteger(player.getAddress().getAddress().getHostAddress()), result -> {
            if(result == null || result.isEmpty() || player.hasPermission(Permissions.PUNISHMENT_PARDON)) return;

            for(Punishment punishment : result) {
                if(punishment.getType().equals(PunishType.BAN) && (punishment.isForever() || !punishment.isExpired())) {
                    player.kickPlayer(revival.getMsgTools().getBanMessage(punishment));

                    return;
                }

                if(punishment.getType().equals(PunishType.MUTE) && revival.getPunishments().getActiveMute(player.getUniqueId()) == null && (punishment.isForever() || !punishment.isExpired()))
                    revival.getPunishments().getActiveMutes().put(player.getUniqueId(), punishment);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Account account = revival.getAccountManager().getAccount(player.getUniqueId());

        if(account == null) return;

        account.setLastSeen(System.currentTimeMillis());

        revival.getAccountManager().saveAccount(revival.getAccountManager().getAccount(player.getUniqueId()), false, true);
    }

}
