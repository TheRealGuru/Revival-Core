package gg.revival.core.accounts;

import gg.revival.core.Revival;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.IPTools;
import gg.revival.core.tools.Logger;
import gg.revival.core.tools.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;

public class AccountListener implements Listener
{

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event)
    {
        UUID uuid = event.getUniqueId();

        Revival.getAccountManager().getAccount(uuid, result -> {
            if(result == null) return;
            if(result.getPunishments().isEmpty()) return;

            for(Punishment punishment : result.getPunishments())
            {
                if(punishment.getType().equals(PunishType.BAN))
                {
                    if(punishment.isForever() || !punishment.isExpired())
                    {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, MsgUtils.getBanMessage(punishment));

                        if(Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline())
                        {
                            Bukkit.getPlayer(uuid).kickPlayer(MsgUtils.getBanMessage(punishment));
                        }
                    }

                    continue;
                }

                if(punishment.getType().equals(PunishType.MUTE))
                {
                    if(punishment.isForever() || !punishment.isExpired())
                    {
                        // TODO: Add user to active mutes list
                    }
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        Account account = Revival.getAccountManager().getAccount(player.getUniqueId());

        account.setLastSeen(System.currentTimeMillis());
        account.setXp(account.getXp() + 10);

        Punishment punishment = new Punishment(UUID.randomUUID(),
                player.getUniqueId(),
                IPTools.ipStringToInteger(player.getAddress().getAddress().getHostAddress()),
                player.getUniqueId(), "Reason not given", PunishType.BAN, System.currentTimeMillis(), System.currentTimeMillis() + 10000L);

        account.getPunishments().add(punishment);

        Revival.getAccountManager().saveAccount(Revival.getAccountManager().getAccount(player.getUniqueId()), false);
    }

}
