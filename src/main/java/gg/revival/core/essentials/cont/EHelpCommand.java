package gg.revival.core.essentials.cont;

import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Config;
import gg.revival.core.tools.MsgUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EHelpCommand extends ECommand
{

    public EHelpCommand()
    {
        super(
                "help",
                "/help [topic]",
                "Learn how to use certain features",
                null,
                0,
                1,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(args.length == 0)
        {
            List<String> content = new ArrayList<>(Config.HELP_TOPICS.keySet());

            MsgUtils.sendHelpPage(player, null, content);

            return;
        }

        if(args.length == 1)
        {
            String searchQuery = args[0];

            for(String topics : Config.HELP_TOPICS.keySet())
            {
                if(topics.equalsIgnoreCase(searchQuery))
                {
                    List<String> content = Config.HELP_TOPICS.get(topics);

                    MsgUtils.sendHelpPage(player, topics, content);

                    return;
                }
            }

            player.sendMessage(ChatColor.RED + "Topic not found");

            return;
        }
    }

}
