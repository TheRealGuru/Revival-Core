package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Logger;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EKickCommand extends ECommand
{

    public EKickCommand()
    {
        super(
                "kick",
                "/kick <player> [reason]",
                "Kick a player from the server",
                Permissions.PUNISHMENT_KICK,
                1,
                Integer.MAX_VALUE,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        String kicker = "Console";
        String reason = "Reason not given";
        String namedPlayer = args[0];

        if(sender instanceof Player)
        {
            Player player = (Player)sender;
            kicker = player.getName();
        }

        if(Bukkit.getPlayer(namedPlayer) == null || !Bukkit.getPlayer(namedPlayer).isOnline())
        {
            sender.sendMessage(MsgUtils.getMessage("errors.player-not-found"));
            return;
        }

        Player player = Bukkit.getPlayer(namedPlayer);

        if(args.length > 1)
        {
            StringBuilder reasonBuilder = new StringBuilder();

            for(int i = 1; i < args.length; i++)
            {
                reasonBuilder.append(args[i] + " ");
            }

            reason = reasonBuilder.toString().trim();
        }

        player.kickPlayer(MsgUtils.getKickMessage(reason));

        Revival.getPlayerTools().sendPermissionMessage(MsgUtils.getMessage("punish-notifications.player-kicked")
                .replace("%player%", player.getName())
                .replace("%kicker%", kicker), Permissions.PUNISHMENT_VIEW);

        Logger.log(player.getName() + " has been kicked by " + kicker + " for " + reason);
    }

}
