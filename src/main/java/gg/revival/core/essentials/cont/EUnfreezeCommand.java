package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EUnfreezeCommand extends ECommand
{

    public EUnfreezeCommand()
    {
        super(
                "unfreeze",
                "/unfreeze <player>",
                "Thaw a player",
                Permissions.MOD_TOOLS,
                1,
                1,
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

        Player unfreezePlayer = Bukkit.getPlayer(namedPlayer);

        if(!Revival.getFreezeManager().isFrozen(unfreezePlayer.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + "This player is not frozen");
            return;
        }

        Revival.getFreezeManager().unfreezePlayer(unfreezePlayer);

        player.sendMessage(ChatColor.GREEN + "You have thawed " + unfreezePlayer.getName());
    }

}
