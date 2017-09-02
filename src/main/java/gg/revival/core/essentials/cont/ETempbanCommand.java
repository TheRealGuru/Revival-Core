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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ETempbanCommand extends ECommand
{

    public ETempbanCommand()
    {
        super(
                "tempban",
                "/tempban <player> <time> [reason]",
                "Temporarily ban a player",
                Permissions.PUNISHMENT_TEMP_BAN,
                2,
                Integer.MAX_VALUE,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if (!validate(sender, args)) return;

        UUID punisherResult = null;
        String namedPlayer = args[0];
        String namedPunisher = "Console";
        String namedTime = args[1];
        String reasonResult = "Reason not given";

        if (sender instanceof Player) {
            Player player = (Player) sender;
            namedPunisher = player.getName();
            punisherResult = player.getUniqueId();
        }

        if (args.length > 2) {
            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                reasonBuilder.append(args[i] + " ");
            }

            reasonResult = reasonBuilder.toString().trim();
        }

        final UUID punisher = punisherResult;
        final String reason = reasonResult;
        final String punisherName = namedPunisher;
        final long banDur = Revival.getTimeTools().getTime(namedTime);

        if (banDur <= 0)
        {
            sender.sendMessage(MsgUtils.getMessage("errors.invalid-time"));
            return;
        }

        Revival.getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null)
            {
                sender.sendMessage(MsgUtils.getMessage("errors.player-not-found"));
                return;
            }

            Revival.getAccountManager().getAccount(uuid, result -> {
                int address = 0;

                if(result.getRegisteredAddresses() != null && !result.getRegisteredAddresses().isEmpty())
                {
                    address = result.getRegisteredAddresses().get(0);
                }

                Punishment punishment = new Punishment(UUID.randomUUID(), uuid, address, punisher, reason, PunishType.BAN, System.currentTimeMillis(), System.currentTimeMillis() + (banDur * 1000L));

                result.getPunishments().add(punishment);

                if(Bukkit.getPlayer(uuid) != null)
                {
                    Bukkit.getPlayer(uuid).kickPlayer(MsgUtils.getBanMessage(punishment));
                }

                else
                {
                    Revival.getAccountManager().saveAccount(result, false, Bukkit.getPlayer(uuid) == null);
                }

                Date date = new Date(punishment.getExpireDate());
                SimpleDateFormat formatter = new SimpleDateFormat("M-d-yyyy '@' hh:mm:ss a z");

                Revival.getPlayerTools().sendPermissionMessage(MsgUtils.getMessage("punish-notifications.player-tempbanned")
                        .replace("%player%", username)
                        .replace("%banner%", punisherName)
                        .replace("%time%", formatter.format(date)), Permissions.PUNISHMENT_VIEW);

                Logger.log(username + " has been banned by " + punisherName + " for " + punishment.getReason() + "\n" + "This ban will expire on " + formatter.format(date));
            });
        });
    }

}
