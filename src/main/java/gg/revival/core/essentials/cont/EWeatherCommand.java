package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EWeatherCommand extends ECommand {

    public EWeatherCommand(Revival revival) {
        super(
                revival,
                "weather",
                "/weather <clear/storm>",
                "Change the weather",
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
        World world = player.getWorld();

        if(args[0].startsWith("c")) {
            world.setStorm(false);
            world.setWeatherDuration(3600 * 20);

            player.sendMessage(ChatColor.GREEN + "Weather is now clear");

            return;
        }

        if(args[0].startsWith("s") || args[0].startsWith("r")) {
            world.setStorm(true);
            world.setWeatherDuration(3600 * 20);

            player.sendMessage(ChatColor.GREEN + "Weather is now rainy");

            return;
        }

        player.sendMessage(ChatColor.RED + getSyntax());
    }

}
