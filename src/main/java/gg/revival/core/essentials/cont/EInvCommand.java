package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EInvCommand extends ECommand {

    public EInvCommand(Revival revival) {
        super(
                revival,
                "inventory",
                "/inventory <player>",
                "View a players inventory",
                Permissions.MOD_TOOLS,
                1,
                1,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;
        String namedPlayer = args[0];

        if(Bukkit.getPlayer(namedPlayer) == null) {
            player.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
            return;
        }

        Player invPlayer = Bukkit.getPlayer(namedPlayer);

        getRevival().getPlayerTools().openInventory(player, invPlayer);
    }

}
