package gg.revival.core.essentials.cont;/*
** John @ 9/2/2017
*/

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EMessageCommand extends ECommand
{

    public EMessageCommand()
    {
        super(
                "message",
                "/message <player> <message>",
                "Send a private message",
                null,
                2,
                Integer.MAX_VALUE,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;
        String namedPlayer = args[0];

        if(Bukkit.getPlayer(namedPlayer) == null)
        {
            player.sendMessage(MsgUtils.getMessage("errors.player-not-found"));
            return;
        }

        if(Bukkit.getPlayer(namedPlayer).getUniqueId().equals(player.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + "You can't message yourself");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();

        for(int i = 1; i < args.length; i++)
        {
            messageBuilder.append(args[i] + " ");
        }

        String message = ChatColor.stripColor(messageBuilder.toString().trim());

        Revival.getMessageManager().sendMessage(player, Bukkit.getPlayer(namedPlayer), message);
    }

}
