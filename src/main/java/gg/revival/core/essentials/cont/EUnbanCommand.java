package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.Logger;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EUnbanCommand extends ECommand
{

    public EUnbanCommand()
    {
        super(
                "unban",
                "/unban <player>",
                "Remove a players active ban",
                Permissions.PUNISHMENT_BAN,
                1,
                1,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        String namedPlayer = args[0];
        String namedUnbanner = "Console";

        if (sender instanceof Player)
        {
            Player player = (Player)sender;
            namedUnbanner = player.getName();
        }

        final String unbanner = namedUnbanner;

        Revival.getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null)
            {
                sender.sendMessage(MsgUtils.getMessage("errors.player-not-found"));
                return;
            }

            Revival.getAccountManager().getAccount(uuid, result -> {
                if(result.getPunishments().isEmpty())
                {
                    sender.sendMessage(MsgUtils.getMessage("errors.player-not-banned"));
                    return;
                }

                List<Punishment> punishmentCache = new CopyOnWriteArrayList<>(result.getPunishments());
                int removedBans = 0;

                for(Punishment punishment : punishmentCache)
                {
                    if(!punishment.getType().equals(PunishType.BAN)) continue;

                    if(punishment.isForever() || !punishment.isExpired())
                    {
                        result.getPunishments().remove(punishment);

                        punishment.setExpireDate(System.currentTimeMillis());

                        removedBans++;
                    }
                }

                if(removedBans == 0)
                {
                    sender.sendMessage(MsgUtils.getMessage("errors.player-not-banned"));
                    return;
                }

                Revival.getAccountManager().saveAccount(result, false, Bukkit.getPlayer(uuid) == null);

                Revival.getPlayerTools().sendPermissionMessage(MsgUtils.getMessage("punish-notifications.player-unbanned")
                        .replace("%player%", username)
                        .replace("%unbanner%", unbanner)
                        .replace("%punishments%", String.valueOf(removedBans)), Permissions.PUNISHMENT_VIEW);

                Logger.log(username + " has been unbanned by " + unbanner + ", " + removedBans + " punishment(s) were removed");
            });
        });
    }

}