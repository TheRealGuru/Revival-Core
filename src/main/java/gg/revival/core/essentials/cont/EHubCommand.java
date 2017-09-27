package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EHubCommand extends ECommand {

    public EHubCommand(Revival revival) {
        super(  revival,
                "hub",
                "/hub",
                "Return to the Hub",
                null,
                0,
                0,
                true);
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(getRevival().getCfg().DISABLE_HUB_COMMAND) {
            player.sendMessage(ChatColor.RED + "This command is disabled on this server");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Returning you to the hub...");

        new BukkitRunnable()
        {
            public void run()
            {
                if(player != null)
                    player.kickPlayer(ChatColor.GREEN + "Returned to hub");
            }
        }.runTaskLater(Revival.getCore(), 20L);
    }

}
