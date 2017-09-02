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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EUnmuteCommand extends ECommand
{

    public EUnmuteCommand()
    {
        super(
                "unmute",
                "/unmute <player>",
                "Unmute a player",
                Permissions.PUNISHMENT_UNMUTE,
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
        String namedUnmuter = "Console";

        if (sender instanceof Player)
        {
            Player player = (Player)sender;
            namedUnmuter = player.getName();
        }

        final String unmuter = namedUnmuter;

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
                int removedMutes = 0;

                for(Punishment punishment : punishmentCache)
                {
                    if(!punishment.getType().equals(PunishType.MUTE)) continue;

                    if(punishment.isForever() || !punishment.isExpired())
                    {
                        result.getPunishments().remove(punishment);

                        punishment.setExpireDate(System.currentTimeMillis());

                        removedMutes++;
                    }
                }

                if(Revival.getPunishments().getActiveMute(uuid) != null)
                {
                    Revival.getPunishments().getActiveMutes().remove(uuid);
                }

                if(removedMutes == 0)
                {
                    sender.sendMessage(MsgUtils.getMessage("errors.player-not-muted"));
                    return;
                }

                Revival.getAccountManager().saveAccount(result, false, Bukkit.getPlayer(uuid) == null);

                Revival.getPlayerTools().sendPermissionMessage(MsgUtils.getMessage("punish-notifications.player-unmuted")
                        .replace("%player%", username)
                        .replace("%unmuter%", unmuter)
                        .replace("%punishments%", String.valueOf(removedMutes)), Permissions.PUNISHMENT_VIEW);

                Logger.log(username + " has been unmuted by " + unmuter + ", " + removedMutes + " punishment(s) were removed");
            });
        });
    }

}
