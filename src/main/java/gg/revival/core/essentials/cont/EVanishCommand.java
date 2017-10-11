package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EVanishCommand extends ECommand {

    public EVanishCommand(Revival revival) {
        super(revival, "vanish", "/vanish", "Toggle vanish", Permissions.ADMIN_TOOLS, 0, 0, true);
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(getRevival().getStaffManager().getVanished().contains(player.getUniqueId())) {
            getRevival().getStaffManager().showPlayer(player);
            player.sendMessage(ChatColor.YELLOW + "You are now " + ChatColor.GREEN + "unvanished");

            if(getRevival().getStaffManager().isTransparent(player))
                player.sendMessage(ChatColor.RED + "WARNING: You are still in no-clip mode. Type /noclip to return to normal");
        }

        else {
            getRevival().getStaffManager().hidePlayer(player);
            player.sendMessage(ChatColor.YELLOW + "You are now " + ChatColor.AQUA + "vanished");
        }
    }
}
