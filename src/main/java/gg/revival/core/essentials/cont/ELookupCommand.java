package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ELookupCommand extends ECommand {

    public ELookupCommand() {
        super(
                "lookup",
                "/lookup <player>",
                "View a players past punishments",
                Permissions.PUNISHMENT_VIEW,
                1,
                1,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;
        String namedPlayer = args[0];

        Revival.getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null) {
                player.sendMessage(MsgUtils.getMessage("errors.player-not-found"));
                return;
            }

            Revival.getAccountManager().getAccount(uuid, result -> {
                if(result == null) {
                    player.sendMessage(MsgUtils.getMessage("errors.player-not-found"));
                    return;
                }

                if(result.getPunishments() == null || result.getPunishments().isEmpty()) {
                    player.sendMessage(MsgUtils.getMessage("errors.no-punishments-found"));
                    return;
                }

                MsgUtils.sendPunishments(player, username, result);
            });
        });
    }

}
