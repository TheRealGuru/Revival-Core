package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EMuteCommand extends ECommand {

    public EMuteCommand(Revival revival) {
        super(
                revival,
              "mute",
                "/mute <player> [reason]",
                "Mute a player forever",
                Permissions.PUNISHMENT_PERM_MUTE,
                1,
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
        String reasonResult = "Reason not given";

        if(sender instanceof Player) {
            Player player = (Player)sender;
            namedPunisher = player.getName();
            punisherResult = player.getUniqueId();
        }

        if(args.length > 1) {
            StringBuilder reasonBuilder = new StringBuilder();

            for(int i = 1; i < args.length; i++)
                reasonBuilder.append(args[i] + " ");

            reasonResult = reasonBuilder.toString().trim();
        }

        final UUID punisher = punisherResult;
        final String reason = reasonResult;
        final String punisherName = namedPunisher;

        getRevival().getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null) {
                sender.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
                return;
            }

            getRevival().getAccountManager().getAccount(uuid, false, false, result -> {
                int address = result.getAddress();

                Punishment punishment = new Punishment(UUID.randomUUID(), uuid, address, punisher, reason, PunishType.MUTE, System.currentTimeMillis(), -1L);

                result.getPunishments().add(punishment);

                if(Bukkit.getPlayer(uuid) != null)
                    getRevival().getPunishments().getActiveMutes().put(uuid, punishment);
                else
                    getRevival().getAccountManager().saveAccount(result, false, Bukkit.getPlayer(uuid) == null);

                getRevival().getPlayerTools().sendPermissionMessage(getRevival().getMsgTools().getMessage("punish-notifications.player-muted")
                        .replace("%player%", username)
                        .replace("%muter%", punisherName), Permissions.PUNISHMENT_VIEW);

                if(Bukkit.getPlayer(uuid) != null)
                    Bukkit.getPlayer(uuid).sendMessage(getRevival().getMsgTools().getMessage("muted.forever").replace("%reason%", reason));

                getRevival().getLog().log(username + " has been muted by " + punisherName + " for " + punishment.getReason());
            });
        });
    }

}
