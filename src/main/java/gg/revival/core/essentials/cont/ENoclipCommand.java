package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ENoclipCommand extends ECommand {

    public ENoclipCommand(Revival revival) {
        super(revival, "noclip", "/noclip", "Toggle noclip mode", Permissions.MOD_TOOLS, 0, 0, true);
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        getRevival().getStaffManager().setTransparent(player);

        if(getRevival().getStaffManager().isTransparent(player))
            player.sendMessage(ChatColor.YELLOW + "You are now " + ChatColor.AQUA + "transparent");
        else
            player.sendMessage(ChatColor.YELLOW + "You are now " + ChatColor.GREEN + "solid");
    }
}
