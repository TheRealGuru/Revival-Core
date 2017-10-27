package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ETempmuteCommand extends ECommand {

    public ETempmuteCommand(Revival revival) {
        super(
                revival,
                "tempmute",
                "/tempmute <player> <time> [reason]",
                "Tempmute a player",
                Permissions.PUNISHMENT_TEMP_MUTE,
                2,
                Integer.MAX_VALUE,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

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

            for (int i = 2; i < args.length; i++)
                reasonBuilder.append(args[i] + " ");

            reasonResult = reasonBuilder.toString().trim();
        }

        final UUID punisher = punisherResult;
        final String reason = reasonResult;
        final String punisherName = namedPunisher;
        final long muteDur = getRevival().getTimeTools().getTime(namedTime);

        if (muteDur <= 0) {
            sender.sendMessage(getRevival().getMsgTools().getMessage("errors.invalid-time"));
            return;
        }

        getRevival().getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null) {
                sender.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
                return;
            }

            getRevival().getAccountManager().getAccount(uuid, false, false, result -> {
                int address = result.getAddress();

                Punishment punishment = new Punishment(UUID.randomUUID(), uuid, address, punisher, reason, PunishType.MUTE, System.currentTimeMillis(), System.currentTimeMillis() + (muteDur * 1000L));

                result.getPunishments().add(punishment);

                if(Bukkit.getPlayer(uuid) != null)
                    getRevival().getPunishments().getActiveMutes().put(uuid, punishment);
                else
                    getRevival().getAccountManager().saveAccount(result, false, Bukkit.getPlayer(uuid) == null);

                Date date = new Date(punishment.getExpireDate());
                SimpleDateFormat formatter = new SimpleDateFormat("M-d-yyyy '@' hh:mm:ss a z");

                getRevival().getPlayerTools().sendPermissionMessage(getRevival().getMsgTools().getMessage("punish-notifications.player-tempmuted")
                        .replace("%player%", username)
                        .replace("%muter%", punisherName)
                        .replace("%time%", formatter.format(date)), Permissions.PUNISHMENT_VIEW);

                if(Bukkit.getPlayer(uuid) != null) {
                    Bukkit.getPlayer(uuid).sendMessage(getRevival().getMsgTools().getMessage("muted.temp")
                            .replace("%reason%", reason).replace("%time%", formatter.format(date)));
                }

                getRevival().getLog().log(username + " has been muted by " + punisherName + " for " + punishment.getReason() + "\n" + "This mute will expire on " + formatter.format(date));
            });
        });
    }

}
