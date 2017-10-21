package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ELookupCommand extends ECommand {

    public ELookupCommand(Revival revival) {
        super(
                revival,
                "lookup",
                "/lookup <player>",
                "Lookup player information",
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

        getRevival().getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null) {
                player.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
                return;
            }

            getRevival().getAccountManager().getAccount(uuid, false, false, result -> {
                if(result == null) {
                    player.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
                    return;
                }

                getRevival().getMsgTools().sendLookup(player, username, result);
            });
        });
    }

}
