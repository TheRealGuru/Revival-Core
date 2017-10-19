package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EGamemodeCommand extends ECommand {

    public EGamemodeCommand(Revival revival) {
        super(
                revival,
                "gamemode",
                "/gamemode [player] <0/1/2/3>",
                "Change a players gamemode",
                Permissions.ADMIN_TOOLS,
                1,
                2,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(args.length == 1) {
            String namedGamemode = args[0];
            GameMode gamemode = getRevival().getPlayerTools().getGamemodeByName(namedGamemode);

            if(gamemode == null) {
                player.sendMessage(ChatColor.RED + "Gamemode not found");
                return;
            }

            player.setGameMode(gamemode);
            player.sendMessage(ChatColor.GREEN + "Changed gamemode to " + StringUtils.capitalize(gamemode.toString().toLowerCase()));

            return;
        }

        String namedPlayer = args[0];
        String namedGamemode = args[1];
        GameMode gamemode = getRevival().getPlayerTools().getGamemodeByName(namedGamemode);

        if(Bukkit.getPlayer(namedPlayer) == null) {
            player.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
            return;
        }

        Player gamemodePlayer = Bukkit.getPlayer(namedPlayer);

        if(gamemode == null) {
            player.sendMessage(ChatColor.RED + "Gamemode not found");
            return;
        }

        gamemodePlayer.setGameMode(gamemode);
        gamemodePlayer.sendMessage(ChatColor.GREEN + player.getName() + " changed your gamemode to " + StringUtils.capitalize(gamemode.toString().toLowerCase()));
        player.sendMessage(ChatColor.GREEN + "Changed " + gamemodePlayer.getName() + "'s gamemode to " + StringUtils.capitalize(gamemode.toString().toLowerCase()));

        getRevival().getLog().log(player.getName() + " changed " + gamemodePlayer.getName() + "'s gamemode to " + StringUtils.capitalize(gamemode.toString().toLowerCase()));
    }

}
