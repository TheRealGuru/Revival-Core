package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EXPCommand extends ECommand {

    public EXPCommand() {
        super(
                "xp",
                "/xp [player]",
                "View XP",
                null,
                0,
                1,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "You currently have " + ChatColor.AQUA + Revival.getAccountManager().getAccount(player.getUniqueId()).getXp() + ChatColor.YELLOW + " XP");
            return;
        }

        if(!player.hasPermission(Permissions.XP_VIEW)) {
            player.sendMessage(MsgUtils.getMessage("errors.no-permission"));
            return;
        }

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

                player.sendMessage(ChatColor.YELLOW + username + " currently has " + ChatColor.AQUA + result.getXp() + ChatColor.YELLOW + " XP");
            });
        });
    }

}
