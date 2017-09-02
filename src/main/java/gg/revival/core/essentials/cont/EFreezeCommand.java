package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EFreezeCommand extends ECommand
{

    public EFreezeCommand()
    {
        super(
                "freeze",
                "/freeze <player>",
                "Freeze a player",
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

        Player freezePlayer = Bukkit.getPlayer(namedPlayer);

        if(Revival.getFreezeManager().isFrozen(freezePlayer.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + "This player is already frozen");
            return;
        }

        Revival.getFreezeManager().freezePlayer(freezePlayer);

        player.sendMessage(ChatColor.GREEN + "You have frozen " + freezePlayer.getName());
    }

}
