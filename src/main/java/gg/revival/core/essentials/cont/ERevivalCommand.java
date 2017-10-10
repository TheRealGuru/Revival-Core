package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ERevivalCommand extends ECommand {

    public ERevivalCommand(Revival revival) {
        super(revival, "revival", "/revival", "Revival Core Command", Permissions.ADMIN_TOOLS, 1, 1, false);
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        if(args[0].equals("reload")) {
            getRevival().getCfg().reloadConfiguration();

            sender.sendMessage(ChatColor.GREEN + "Reload request has been sent for Revival Core");

            return;
        }

        sender.sendMessage(ChatColor.RED + getSyntax() + " reload");
    }
}
