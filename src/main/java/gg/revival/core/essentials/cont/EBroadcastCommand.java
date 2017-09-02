package gg.revival.core.essentials.cont;

import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EBroadcastCommand extends ECommand
{

    public EBroadcastCommand()
    {
        super(
                "broadcast",
                "/broadcast [-r] <message>",
                "Send out a broadcast to all players",
                Permissions.ADMIN_TOOLS,
                1,
                Integer.MAX_VALUE,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        StringBuilder broadcastBuilder = new StringBuilder();
        boolean raw = false;

        if(args.length >= 2)
        {
            if(args[0].equalsIgnoreCase("-r"))
            {
                raw = true;
            }
        }

        if(raw) {
            for(int i = 1; i < args.length; i++)
            {
                broadcastBuilder.append(args[i] + " ");
            }

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcastBuilder.toString().trim()));
        }

        else
        {
            for(int i = 0; i < args.length; i++)
            {
                broadcastBuilder.append(args[i] + " ");
            }

            Bukkit.broadcastMessage("[" + ChatColor.RED + "HCFR" + ChatColor.WHITE + "] " + ChatColor.stripColor(broadcastBuilder.toString().trim()));
        }
    }

}
